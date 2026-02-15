package com.adg.admin.api;

import com.adg.admin.app.PolicyService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.PolicyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin API HTTP 컨트롤러. 정책 조회·수정 (admin 전용).
 */
@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin", description = "Policy management (admin only)")
public class AdminPolicyController {

    private final PolicyService policyService;

    public AdminPolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping("/policies")
    @Operation(summary = "List active policies")
    public ResponseEntity<List<PolicyDto>> listPolicies(HttpServletRequest httpRequest) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(policyService.listActivePolicies());
    }

    @PostMapping("/policies")
    @Operation(summary = "Create or update policy")
    public ResponseEntity<PolicyDto> updatePolicy(
            @Valid @RequestBody PolicyDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        String actor = (String) httpRequest.getAttribute(AuthFilter.ATTR_CLIENT_ID);
        return ResponseEntity.ok(policyService.update(dto, actor));
    }
}
