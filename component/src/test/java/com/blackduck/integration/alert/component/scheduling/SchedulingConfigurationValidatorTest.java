/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.component.scheduling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.blackduck.integration.alert.common.rest.model.FieldValueModel;
import com.blackduck.integration.alert.component.scheduling.descriptor.SchedulingDescriptor;
import com.blackduck.integration.alert.component.scheduling.descriptor.SchedulingDescriptorKey;
import com.blackduck.integration.alert.component.scheduling.validator.SchedulingConfigurationFieldModelValidator;
import com.blackduck.integration.alert.test.common.channel.GlobalConfigurationValidatorAsserter;
class SchedulingConfigurationValidatorTest {

    /*
     * daily processing hour: required, valid list option
     * purge data frequency: required, valid list option
     */

    @Test
    void verifyValidConfiguration() {
        GlobalConfigurationValidatorAsserter validatorAsserter = createValidatorAsserter();
        validatorAsserter.assertValid();
    }

    @Test
    void missingDailyProcessing() {
        GlobalConfigurationValidatorAsserter validatorAsserter = createValidatorAsserter();
        validatorAsserter.assertMissingValue(SchedulingDescriptor.KEY_DAILY_PROCESSOR_HOUR_OF_DAY);
    }

    @Test
    void missingPurgeFrequency() {
        GlobalConfigurationValidatorAsserter validatorAsserter = createValidatorAsserter();
        validatorAsserter.assertMissingValue(SchedulingDescriptor.KEY_PURGE_DATA_FREQUENCY_DAYS);
    }

    @Test
    void invalidOption() {
        GlobalConfigurationValidatorAsserter validatorAsserter = createValidatorAsserter();
        validatorAsserter.assertInvalidValue(SchedulingDescriptor.KEY_PURGE_DATA_FREQUENCY_DAYS, "potato");
    }

    @ParameterizedTest
    @ValueSource(strings = { "potato", "-1", "0", "31", "100" })
    void invalidAuditPurgeOption(String invalidOption) {
        GlobalConfigurationValidatorAsserter validatorAsserter = createValidatorAsserter();
        validatorAsserter.assertInvalidValue(SchedulingDescriptor.KEY_PURGE_AUDIT_FAILED_FREQUENCY_DAYS, invalidOption);
    }

    private GlobalConfigurationValidatorAsserter createValidatorAsserter() {
        return new GlobalConfigurationValidatorAsserter(new SchedulingDescriptorKey().getUniversalKey(), new SchedulingConfigurationFieldModelValidator(), createValidConfig());
    }

    private Map<String, FieldValueModel> createValidConfig() {
        Map<String, FieldValueModel> keyToValues = new HashMap<>();
        FieldValueModel processingHour = new FieldValueModel(List.of("1"), true);
        FieldValueModel purgeFrequency = new FieldValueModel(List.of("3"), true);
        FieldValueModel auditFailedPurgeFrequency = new FieldValueModel(List.of("10"), true);

        keyToValues.put(SchedulingDescriptor.KEY_DAILY_PROCESSOR_HOUR_OF_DAY, processingHour);
        keyToValues.put(SchedulingDescriptor.KEY_PURGE_DATA_FREQUENCY_DAYS, purgeFrequency);
        keyToValues.put(SchedulingDescriptor.KEY_PURGE_AUDIT_FAILED_FREQUENCY_DAYS, auditFailedPurgeFrequency);

        return keyToValues;
    }
}
