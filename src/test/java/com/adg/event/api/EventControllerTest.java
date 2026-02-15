package com.adg.event.api;

import com.adg.event.app.EventService;
import com.adg.shared.dto.EventUrlRequest;
import com.adg.shared.dto.EventUrlResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(AppAuthProperties.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EventService eventService;

    @Test
    void impression_returns200WithSameUrl() throws Exception {
        EventUrlRequest request = EventUrlRequest.builder().url("https://track.example.com/imp/1").build();
        when(eventService.handleEvent(any(EventUrlRequest.class), eq("impression")))
                .thenReturn(EventUrlResponse.builder().url(request.getUrl()).build());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/impression")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://track.example.com/imp/1"));
    }

    @Test
    void click_returns200WithSameUrl() throws Exception {
        EventUrlRequest request = EventUrlRequest.builder().url("https://track.example.com/click/1").build();
        when(eventService.handleEvent(any(EventUrlRequest.class), eq("click")))
                .thenReturn(EventUrlResponse.builder().url(request.getUrl()).build());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://track.example.com/click/1"));
    }

    @Test
    void nurl_returns200WithSameUrl() throws Exception {
        EventUrlRequest request = EventUrlRequest.builder().url("https://track.example.com/nurl/1").build();
        when(eventService.handleEvent(any(EventUrlRequest.class), eq("nurl")))
                .thenReturn(EventUrlResponse.builder().url(request.getUrl()).build());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/nurl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://track.example.com/nurl/1"));
    }

    @Test
    void burl_returns200WithSameUrl() throws Exception {
        EventUrlRequest request = EventUrlRequest.builder().url("https://track.example.com/burl/1").build();
        when(eventService.handleEvent(any(EventUrlRequest.class), eq("burl")))
                .thenReturn(EventUrlResponse.builder().url(request.getUrl()).build());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/burl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://track.example.com/burl/1"));
    }

    @Test
    void lurl_returns200WithSameUrl() throws Exception {
        EventUrlRequest request = EventUrlRequest.builder().url("https://track.example.com/lurl/1").build();
        when(eventService.handleEvent(any(EventUrlRequest.class), eq("lurl")))
                .thenReturn(EventUrlResponse.builder().url(request.getUrl()).build());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/lurl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://track.example.com/lurl/1"));
    }

    @Test
    void impression_returns400WhenUrlBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/impression")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void click_returns400WhenUrlMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/events/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
