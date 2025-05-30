/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.common.descriptor.action;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blackduck.integration.alert.api.common.model.exception.AlertRuntimeException;
import com.blackduck.integration.alert.api.descriptor.model.DescriptorKey;

public final class DescriptorActionMap<T extends DescriptorAction> {
    private final Map<String, T> universalKeyToAction;

    public DescriptorActionMap(Collection<T> descriptorActions) {
        this.universalKeyToAction = initializeMap(descriptorActions);
    }

    public final T findRequiredAction(DescriptorKey descriptorKey) throws AlertRuntimeException {
        return findRequiredAction(descriptorKey.getUniversalKey());
    }

    public final T findRequiredAction(String descriptorUniversalKey) throws AlertRuntimeException {
        return findOptionalAction(descriptorUniversalKey)
                   .orElseThrow(() -> new AlertRuntimeException(String.format("Missing required action for descriptor: %s", descriptorUniversalKey)));
    }

    public final Optional<T> findOptionalAction(DescriptorKey descriptorKey) {
        return findOptionalAction(descriptorKey.getUniversalKey());
    }

    public final Optional<T> findOptionalAction(String descriptorUniversalKey) {
        T foundAction = universalKeyToAction.get(descriptorUniversalKey);
        return Optional.ofNullable(foundAction);
    }

    private static <U extends DescriptorAction> Map<String, U> initializeMap(Collection<U> descriptorActions) {
        return descriptorActions
                   .stream()
                   .collect(Collectors.toMap(action -> action.getDescriptorKey().getUniversalKey(), Function.identity()));
    }

}
