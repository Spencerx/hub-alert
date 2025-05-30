/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.api.channel.issue.tracker.convert;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.blackduck.integration.alert.api.channel.convert.ChannelMessageFormatter;
import com.blackduck.integration.alert.api.channel.issue.tracker.model.IssueComponentUnknownVersionDetails;
import com.blackduck.integration.alert.api.channel.issue.tracker.model.IssueEstimatedRiskModel;
import com.blackduck.integration.alert.common.enumeration.ItemOperation;

public class IssueComponentUnknownVersionDetailsConverter {
    private static final String TEXT_COMPONENT_DELETE = "Component was removed or the version was set.";
    private static final String SECTION_LABEL_VULNERABILITY_COUNTS = "Vulnerability counts:";
    private final ChannelMessageFormatter formatter;

    public IssueComponentUnknownVersionDetailsConverter(ChannelMessageFormatter formatter) {
        this.formatter = formatter;
    }

    public List<String> createEstimatedRiskDetailsSectionPieces(IssueComponentUnknownVersionDetails unknownVersionDetails) {
        List<String> estimatedRiskSectionPieces = new LinkedList<>();

        if (ItemOperation.DELETE.equals(unknownVersionDetails.getItemOperation())) {
            estimatedRiskSectionPieces.add(formatter.encode(TEXT_COMPONENT_DELETE));
            estimatedRiskSectionPieces.add(formatter.getLineSeparator());
        } else {
            estimatedRiskSectionPieces.add(formatter.encode(SECTION_LABEL_VULNERABILITY_COUNTS));
            estimatedRiskSectionPieces.add(formatter.getLineSeparator());
            estimatedRiskSectionPieces.add(formatter.getLineSeparator());

            for (IssueEstimatedRiskModel estimatedRiskModel : unknownVersionDetails.getEstimatedRiskModelList()) {
                estimatedRiskSectionPieces.add(createEstimatedRiskString(estimatedRiskModel));
                estimatedRiskSectionPieces.add(formatter.getLineSeparator());
            }
        }
        return estimatedRiskSectionPieces;
    }

    private String createEstimatedRiskString(IssueEstimatedRiskModel estimatedRiskModel) {
        String severity = formatter.encode(estimatedRiskModel.getSeverity().getVulnerabilityLabel());
        String countString = formatter.encode(String.format("(%s)", estimatedRiskModel.getCount()));
        String componentName = createComponentNameLinkIfPresent(estimatedRiskModel);
        // "    <SEVERITY>: (<COUNT>) <COMPONENT_NAME>"
        StringBuilder builder = new StringBuilder(100);
        builder.append(formatter.getNonBreakingSpace());
        builder.append(formatter.getNonBreakingSpace());
        builder.append(formatter.getNonBreakingSpace());
        builder.append(formatter.getNonBreakingSpace());
        builder.append(severity);
        builder.append(formatter.encode(":"));
        builder.append(formatter.getNonBreakingSpace());
        builder.append(countString);
        builder.append(formatter.getNonBreakingSpace());
        builder.append(componentName);

        return builder.toString();
    }

    private String createComponentNameLinkIfPresent(IssueEstimatedRiskModel estimatedRiskModel) {
        String encodedName = formatter.encode(estimatedRiskModel.getName());
        String componentNameString = encodedName;

        Optional<String> optionalUrl = estimatedRiskModel.getComponentVersionUrl();

        if (optionalUrl.isPresent()) {
            String encodedUrl = formatter.encode(optionalUrl.get());
            componentNameString = formatter.createLink(encodedName, encodedUrl);
        }

        return componentNameString;
    }
}
