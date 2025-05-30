/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.provider.blackduck.processor.message.service.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackduck.integration.blackduck.service.BlackDuckApiClient;

@Component
public class BlackDuckComponentPolicyDetailsCreatorFactory {
    private final BlackDuckPolicySeverityConverter policySeverityConverter;

    @Autowired
    public BlackDuckComponentPolicyDetailsCreatorFactory(BlackDuckPolicySeverityConverter policySeverityConverter) {
        this.policySeverityConverter = policySeverityConverter;
    }

    public BlackDuckComponentPolicyDetailsCreator createBlackDuckComponentPolicyDetailsCreator(BlackDuckApiClient blackDuckApiClient) {
        return new BlackDuckComponentPolicyDetailsCreator(policySeverityConverter, blackDuckApiClient);
    }
}
