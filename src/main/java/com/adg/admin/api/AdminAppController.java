package com.adg.admin.api;

import com.adg.admin.app.AppService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.AppDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin API. 앱(App) 조회·생성·수정 (admin 전용).
 */
@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin", description = "App management (admin only)")
public class AdminAppController {

    private final AppService appService;

    public AdminAppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/apps")
    @Operation(summary = "List apps")
    public ResponseEntity<List<AppDto>> listApps(
            @RequestParam(required = false) Long publisherId,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(appService.list(publisherId));
    }

    @GetMapping("/apps/{appId}")
    @Operation(summary = "Get app by appId")
    public ResponseEntity<AppDto> getApp(
            @PathVariable String appId,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        AppDto dto = appService.getByAppId(appId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/apps")
    @Operation(summary = "Create app")
    public ResponseEntity<AppDto> createApp(
            @Valid @RequestBody AppDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(appService.create(dto));
    }

    @PutMapping("/apps/{id}")
    @Operation(summary = "Update app")
    public ResponseEntity<AppDto> updateApp(
            @PathVariable Long id,
            @Valid @RequestBody AppDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        AppDto updated = appService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
