package com.adg.admin.api;

import com.adg.admin.app.PolicyService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.PolicyDto;
import com.adg.config.AppAuthProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPolicyController.class)
@Import(AppAuthProperties.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PolicyService policyService;

    @Test
    void listPolicies_returnsActiveOnlyWhenAdmin() throws Exception {
        when(policyService.listActivePolicies()).thenReturn(List.of(
                PolicyDto.builder().id(1L).clientId("default").active(true).build()
        ));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/admin/policies")
                        .with(req -> { req.setAttribute(AuthFilter.ATTR_IS_ADMIN, true); req.setAttribute(AuthFilter.ATTR_CLIENT_ID, "admin"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clientId").value("default"));
    }

    @Test
    void listPolicies_forbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/admin/policies")
                        .with(req -> { req.setAttribute(AuthFilter.ATTR_IS_ADMIN, false); return req; }))
                .andExpect(status().is(403));
    }
}
