package com.adg.decision.api;

import com.adg.decision.app.DecisionService;
import com.adg.shared.OpenRtbTestFixtures;
import com.adg.shared.dto.openrtb.Bid;
import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.BidResponse;
import com.adg.shared.dto.openrtb.Imp;
import com.adg.shared.dto.openrtb.SeatBid;
import com.adg.config.AppAuthProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DecisionController.class)
@Import(AppAuthProperties.class)
@AutoConfigureMockMvc(addFilters = false)
class DecisionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DecisionService decisionService;

    @Test
    void decide_returnsOkWithBidResponse() throws Exception {
        BidRequest request = BidRequest.builder()
                .id("req-1")
                .imp(List.of(Imp.builder().id("p1").build()))
                .build();
        BidResponse response = BidResponse.builder()
                .id("req-1")
                .bidid("bid-1")
                .cur("USD")
                .seatbid(List.of(
                        SeatBid.builder()
                                .seat("default")
                                .bid(List.of(Bid.builder()
                                        .id("d1")
                                        .impid("p1")
                                        .price(new BigDecimal("1.50"))
                                        .adid("camp-1")
                                        .crid("cr-1")
                                        .nurl("https://track.example.com/d1")
                                        .build()))
                                .build()))
                .build();
        when(decisionService.decide(any(BidRequest.class), eq("openrtb"))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("req-1"))
                .andExpect(jsonPath("$.bidid").value("bid-1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].id").value("d1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].impid").value("p1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].crid").value("cr-1"));
    }

    @Test
    void decide_returns400WhenIdBlank() throws Exception {
        String body = "{\"id\":\"\",\"imp\":[{\"id\":\"p1\"}]}";
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void decide_returns400WhenImpEmpty() throws Exception {
        String body = "{\"id\":\"req-1\",\"imp\":[]}";
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void decide_withFullOpenRtbRequest_returnsOkWithBidResponse() throws Exception {
        BidRequest request = OpenRtbTestFixtures.fullBidRequestForAdResponse();
        BidResponse response = BidResponse.builder()
                .id(request.getId())
                .bidid("bid-full-1")
                .cur("USD")
                .seatbid(List.of(
                        SeatBid.builder()
                                .seat("openrtb")
                                .bid(List.of(Bid.builder()
                                        .id("d-full-1")
                                        .impid("placement-1")
                                        .price(new BigDecimal("2.00"))
                                        .adid("camp-1")
                                        .crid("cr-1")
                                        .nurl("https://track.example.com/d-full-1")
                                        .build()))
                                .build()))
                .build();
        when(decisionService.decide(any(BidRequest.class), eq("openrtb"))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("full-req-1"))
                .andExpect(jsonPath("$.bidid").value("bid-full-1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].impid").value("placement-1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].crid").value("cr-1"))
                .andExpect(jsonPath("$.seatbid[0].bid[0].price").value(2.0));
    }
}
