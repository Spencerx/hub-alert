/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.channel.jira.cloud.model;

import java.util.ArrayList;
import java.util.List;

import com.blackduck.integration.jira.common.model.components.TransitionComponent;
import com.blackduck.integration.jira.common.model.response.TransitionsResponseModel;

public class TestTransitionResponsesModel extends TransitionsResponseModel {
    List<TransitionComponent> transitions;

    public TestTransitionResponsesModel() {
        TransitionComponent doneTransition = new TransitionComponent("1", "done", new TestDoneStatusDetailsComponent(), null, null, null, null, null);
        TransitionComponent openTransition = new TransitionComponent("2", "new", new TestNewStatusDetailsComponent(), null, null, null, null, null);
        List<TransitionComponent> transitions = new ArrayList<>();
        transitions.add(doneTransition);
        transitions.add(openTransition);
        this.transitions = transitions;
    }

    @Override
    public List<TransitionComponent> getTransitions() {
        return transitions;
    }
}
