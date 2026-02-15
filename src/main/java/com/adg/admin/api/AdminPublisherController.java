package com.adg.admin.api;

import com.adg.admin.app.PublisherService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.PublisherDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin API. 퍼블리셔(Publisher) 조회·생성·수정 (admin 전용).
 */
@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin", description = "Publisher management (admin only)")
public class AdminPublisherController {

    private final PublisherService publisherService;

    public AdminPublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("/publishers")
    @Operation(summary = "List publishers")
    public ResponseEntity<List<PublisherDto>> listPublishers(HttpServletRequest httpRequest) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(publisherService.list());
    }

    @GetMapping("/publishers/{publisherId}")
    @Operation(summary = "Get publisher by publisherId")
    public ResponseEntity<PublisherDto> getPublisher(
            @PathVariable String publisherId,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        PublisherDto dto = publisherService.getByPublisherId(publisherId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/publishers")
    @Operation(summary = "Create publisher")
    public ResponseEntity<PublisherDto> createPublisher(
            @Valid @RequestBody PublisherDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(publisherService.create(dto));
    }

    @PutMapping("/publishers/{id}")
    @Operation(summary = "Update publisher")
    public ResponseEntity<PublisherDto> updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody PublisherDto dto,
            HttpServletRequest httpRequest
    ) {
        if (!Boolean.TRUE.equals(httpRequest.getAttribute(AuthFilter.ATTR_IS_ADMIN))) {
            return ResponseEntity.status(403).build();
        }
        PublisherDto updated = publisherService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
