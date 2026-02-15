package com.adg.integration;

import com.adg.shared.OpenRtbTestFixtures;
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

/** Decision API E2E: OpenRTB Bid Request 전송 → Bid Response(seatbid, bid, crid) 검증. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DecisionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void decisionE2E_returnsBidResponseWithSeatBid() throws Exception {
        BidRequest request = BidRequest.builder()
                .id("e2e-req-1")
                .imp(List.of(Imp.builder().id("p1").build()))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e2e-req-1"))
                .andExpect(jsonPath("$.seatbid").isArray())
                .andExpect(jsonPath("$.seatbid[0].bid").isArray())
                .andExpect(jsonPath("$.seatbid[0].bid[0].impid").value("p1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].price").exists())
                .andExpect(jsonPath("$.seatbid[0].bid[0].crid").exists());
    }

    @Test
    void decisionE2E_withFullOpenRtbRequest_returnsBidResponseWithSeatBid() throws Exception {
        BidRequest request = OpenRtbTestFixtures.fullBidRequestForAdResponse();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("full-req-1"))
                .andExpect(jsonPath("$.seatbid").isArray())
                .andExpect(jsonPath("$.seatbid[0].bid").isArray())
                .andExpect(jsonPath("$.seatbid[0].bid[0].impid").value("placement-1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].price").exists())
                .andExpect(jsonPath("$.seatbid[0].bid[0].crid").exists())
                .andExpect(jsonPath("$.seatbid[0].bid[0].adid").exists());
    }
}
