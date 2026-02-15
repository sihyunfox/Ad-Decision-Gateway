package com.adg.decision.app;

import com.adg.config.AdgMetrics;
import com.adg.decision.pipeline.AdDecisionPipeline;
import com.adg.decision.pipeline.AdDecisionPipelineContext;
import com.adg.shared.dto.*;
import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.BidResponse;
import com.adg.shared.port.EventQueuePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Decision 파이프라인 오케스트레이션 서비스.
 * <p>BidRequest 수신 → 파이프라인 실행(DataLoad→검증→필터→선택→노출). Profile/Campaign/Policy/Cap은 파이프라인 1단계(DB·캐시)에서 로드.
 * 이력은 이벤트 큐 1건 적재, Drainer가 파일/Kafka로 전달.</p>
 */
@Service
public class DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);
    private static final String TRACE_ID = "traceId";

    private final EventQueuePort eventQueuePort;
    private final ObjectMapper objectMapper;
    private final AdgMetrics metrics;
    private final AdDecisionPipeline pipeline;

    public DecisionService(EventQueuePort eventQueuePort, ObjectMapper objectMapper,
                           AdgMetrics metrics, AdDecisionPipeline pipeline) {
        this.eventQueuePort = eventQueuePort;
        this.objectMapper = objectMapper;
        this.metrics = metrics;
        this.pipeline = pipeline;
    }

    /**
     * OpenRTB BidRequest로 의사결정 수행 후 BidResponse 반환.
     * @param bidRequest OpenRTB 2.x Bid Request (id, imp 필수)
     * @param clientId 요청 주체 식별자 (public path 시 "openrtb")
     * @return OpenRTB 2.x Bid Response (seatbid[].bid[] 포함)
     */
    public BidResponse decide(BidRequest bidRequest, String clientId) {
        DecisionRequest request = OpenRtbMapper.toDecisionRequest(bidRequest);
        request.setClientId(clientId);

        long start = System.currentTimeMillis();
        String traceId = MDC.get(TRACE_ID);
        String requestId = bidRequest.getId() != null ? bidRequest.getId() : UUID.randomUUID().toString();
        String decisionId = UUID.randomUUID().toString();

        AdDecisionPipelineContext context = AdDecisionPipelineContext.builder()
                .requestId(requestId)
                .decisionId(decisionId)
                .traceId(traceId)
                .clientId(clientId)
                .request(request)
                .build();
        pipeline.run(context);

        boolean fallbackUsed = context.isFallbackUsed();
        CampaignItem winner = context.getWinner().orElse(houseAd());
        String impid = request.getPlacementId();
        String trackingUrl = "https://track.example.com/" + decisionId;

        DecisionRecord record = DecisionRecord.builder()
                .decisionId(decisionId)
                .traceId(traceId)
                .requestId(requestId)
                .clientId(clientId)
                .placementId(request.getPlacementId())
                .winnerCampaignId(winner.getCampaignId())
                .winnerCreativeId(winner.getCreativeId())
                .winnerBid(winner.getBid())
                .winnerScore(winner.getBid())
                .trackingUrl(trackingUrl)
                .fallbackUsed(fallbackUsed)
                .build();

        BidResponse bidResponse = OpenRtbMapper.toBidResponse(
                requestId, decisionId, clientId, impid, winner, fallbackUsed, trackingUrl);

        String eventPayload = buildDecisionCreatedPayload(requestId, decisionId, traceId, clientId,
                bidRequest, record, bidResponse);
        appendToEventQueue("decision-created", eventPayload);

        metrics.getDecisionRequests().increment();
        if (fallbackUsed) {
            metrics.getDecisionFallback().increment();
        }
        metrics.getDecisionLatency().record(java.time.Duration.ofMillis(System.currentTimeMillis() - start));

        return bidResponse;
    }

    /**
     * Worker/파일·Kafka용 확장 payload: requestId, decisionId, traceId, clientId, bidRequestJson, decisionRecordJson, bidResponseJson.
     */
    private String buildDecisionCreatedPayload(String requestId, String decisionId, String traceId, String clientId,
                                              BidRequest bidRequest, DecisionRecord record, BidResponse bidResponse) {
        try {
            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("requestId", requestId);
            payload.put("decisionId", decisionId);
            payload.put("traceId", traceId != null ? traceId : "");
            payload.put("clientId", clientId);
            payload.put("bidRequestJson", objectMapper.writeValueAsString(bidRequest));
            payload.put("decisionRecordJson", objectMapper.writeValueAsString(record));
            payload.put("bidResponseJson", objectMapper.writeValueAsString(bidResponse));
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Failed to build decision-created payload: {}", e.getMessage());
            return String.format("{\"requestId\":\"%s\",\"decisionId\":\"%s\",\"traceId\":\"%s\",\"clientId\":\"%s\"}",
                    requestId, decisionId, traceId != null ? traceId : "", clientId);
        }
    }

    private void appendToEventQueue(String eventType, String payloadJson) {
        try {
            eventQueuePort.append(eventType, payloadJson);
        } catch (Exception e) {
            log.warn("Event queue append failed (response still returned): eventType={}, {}", eventType, e.getMessage());
        }
    }

    private CampaignItem houseAd() {
        return CampaignItem.builder()
                .campaignId("house")
                .creativeId("house-default")
                .status("ACTIVE")
                .bid(BigDecimal.ZERO)
                .build();
    }
}
