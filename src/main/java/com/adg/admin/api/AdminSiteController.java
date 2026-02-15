package com.adg.admin.api;

import com.adg.admin.app.SiteService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.SiteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin API. 사이트(Site) 조회·생성·수정 (admin 전용).
 */
@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin", description = "Site management (admin only)")
public class AdminSiteController {

    private final SiteService siteService;

    public AdminSiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/sites")
    @Operation(summary = "List sites")
    public ResponseEntity<List<SiteDto>> listSites(
            @RequestParam(required = false) Long publisherId,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(siteService.list(publisherId));
    }

    @GetMapping("/sites/{siteId}")
    @Operation(summary = "Get site by siteId")
    public ResponseEntity<SiteDto> getSite(
            @PathVariable String siteId,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        SiteDto dto = siteService.getBySiteId(siteId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/sites")
    @Operation(summary = "Create site")
    public ResponseEntity<SiteDto> createSite(
            @Valid @RequestBody SiteDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(siteService.create(dto));
    }

    @PutMapping("/sites/{id}")
    @Operation(summary = "Update site")
    public ResponseEntity<SiteDto> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody SiteDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        SiteDto updated = siteService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
