package com.adg.decision.pipeline;

import com.adg.shared.dto.CapCheckRequest;
import com.adg.shared.dto.CapCheckResponse;
import com.adg.shared.dto.DecisionRequest;
import com.adg.shared.port.CampaignPort;
import com.adg.shared.port.CapPort;
import com.adg.shared.port.PolicyPort;
import com.adg.shared.port.ProfilePort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * DataLoadStage 기본 구현. Profile/Campaign/Policy/Cap 포트 호출 후 context에 설정.
 */
@Component
public class DefaultDataLoadStage implements DataLoadStage {

    private final ProfilePort profilePort;
    private final CampaignPort campaignPort;
    private final PolicyPort policyPort;
    private final CapPort capPort;

    public DefaultDataLoadStage(ProfilePort profilePort, CampaignPort campaignPort,
                                PolicyPort policyPort, CapPort capPort) {
        this.profilePort = profilePort;
        this.campaignPort = campaignPort;
        this.policyPort = policyPort;
        this.capPort = capPort;
    }

    @Override
    public void load(AdDecisionPipelineContext context) {
        DecisionRequest request = context.getRequest();
        if (request == null) {
            context.setCandidates(Collections.emptyList());
            context.setCapResponse(CapCheckResponse.builder().allow(false).remaining(0).build());
            context.setProfile(Optional.empty());
            context.setPolicies(Collections.emptyList());
            return;
        }
        String placementId = request.getPlacementId() != null ? request.getPlacementId() : "";
        String userId = request.getUser() != null && request.getUser().getUserId() != null
                ? request.getUser().getUserId() : "unknown";
        String clientId = context.getClientId() != null ? context.getClientId() : "openrtb";

        context.setProfile(profilePort.getProfile(userId));
        context.setCandidates(campaignPort.getCampaigns(placementId));
        context.setPolicies(policyPort.getPolicies(clientId) != null ? policyPort.getPolicies(clientId) : Collections.emptyList());
        CapCheckRequest capRequest = CapCheckRequest.builder()
                .clientId(clientId)
                .userId(userId)
                .campaignId(null)
                .build();
        context.setCapResponse(capPort.check(capRequest));
    }
}
