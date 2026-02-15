package com.adg.integration;

import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.Imp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Downstream(Mock) 실패 시나리오: Decision은 200으로 OpenRTB Bid Response를 반환하며,
 * fallback 시 seatbid[0].bid[0].crid=house-default(또는 adid=house)로 응답.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DecisionFallbackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenMockFails_decisionReturnsBidResponseWithFallback() throws Exception {
        BidRequest request = BidRequest.builder()
                .id("fallback-req-1")
                .imp(List.of(Imp.builder().id("p1").build()))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("fallback-req-1"))
                .andExpect(jsonPath("$.seatbid").isArray())
                .andExpect(jsonPath("$.seatbid[0].bid").isArray())
                .andExpect(jsonPath("$.seatbid[0].bid[0].impid").value("p1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].crid").exists());
    }
}
