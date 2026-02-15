package com.adg.event.api;

import com.adg.event.app.EventService;
import com.adg.shared.dto.EventUrlRequest;
import com.adg.shared.dto.EventUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Event API HTTP 컨트롤러.
 * <p>노출(impression)/클릭(click)/nurl(win notice)/burl(billing)/lurl(loss) 5종 엔드포인트.
 * Request·Response 동일: {@code { "url": "https://..." }}. 인증 없음(public path).</p>
 */
@RestController
@RequestMapping("/v1/events")
@Tag(name = "Events", description = "Impression, click, nurl, burl, lurl (OpenRTB)")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /** 노출(Impression) 콜백 URL 수신. 유효성 → 어뷰징 → 로깅 후 동일 url 반환. */
    @PostMapping("/impression")
    @Operation(summary = "Record impression (URL)")
    public ResponseEntity<EventUrlResponse> impression(@Valid @RequestBody EventUrlRequest request) {
        return ResponseEntity.ok(eventService.handleEvent(request, "impression"));
    }

    /** 클릭(Click) 콜백 URL 수신. */
    @PostMapping("/click")
    @Operation(summary = "Record click (URL)")
    public ResponseEntity<EventUrlResponse> click(@Valid @RequestBody EventUrlRequest request) {
        return ResponseEntity.ok(eventService.handleEvent(request, "click"));
    }

    /** OpenRTB Win Notice(nurl) URL 수신. */
    @PostMapping("/nurl")
    @Operation(summary = "Record win notice (nurl)")
    public ResponseEntity<EventUrlResponse> nurl(@Valid @RequestBody EventUrlRequest request) {
        return ResponseEntity.ok(eventService.handleEvent(request, "nurl"));
    }

    /** OpenRTB Billing Notice(burl) URL 수신. */
    @PostMapping("/burl")
    @Operation(summary = "Record billing notice (burl)")
    public ResponseEntity<EventUrlResponse> burl(@Valid @RequestBody EventUrlRequest request) {
        return ResponseEntity.ok(eventService.handleEvent(request, "burl"));
    }

    /** OpenRTB Loss Notice(lurl) URL 수신. */
    @PostMapping("/lurl")
    @Operation(summary = "Record loss notice (lurl)")
    public ResponseEntity<EventUrlResponse> lurl(@Valid @RequestBody EventUrlRequest request) {
        return ResponseEntity.ok(eventService.handleEvent(request, "lurl"));
    }
}
