package com.adg.admin.api;

import com.adg.admin.app.CreativeService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.CreativeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin API. 광고 소재(Creative) 조회·생성·수정 (admin 전용).
 */
@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin", description = "Creative management (admin only)")
public class AdminCreativeController {

    private final CreativeService creativeService;

    public AdminCreativeController(CreativeService creativeService) {
        this.creativeService = creativeService;
    }

    @GetMapping("/creatives")
    @Operation(summary = "List creatives")
    public ResponseEntity<List<CreativeDto>> listCreatives(
            @RequestParam(required = false) String campaignId,
            @RequestParam(required = false) String status,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(creativeService.list(campaignId, status));
    }

    @GetMapping("/creatives/{creativeId}")
    @Operation(summary = "Get creative by creativeId")
    public ResponseEntity<CreativeDto> getCreative(
            @PathVariable String creativeId,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        CreativeDto dto = creativeService.getByCreativeId(creativeId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/creatives")
    @Operation(summary = "Create creative")
    public ResponseEntity<CreativeDto> createCreative(
            @Valid @RequestBody CreativeDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(creativeService.create(dto));
    }

    @PutMapping("/creatives/{id}")
    @Operation(summary = "Update creative")
    public ResponseEntity<CreativeDto> updateCreative(
            @PathVariable Long id,
            @Valid @RequestBody CreativeDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        CreativeDto updated = creativeService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
