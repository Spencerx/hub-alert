/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.alert.scheduling.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.alert.NotificationManager;
import com.blackducksoftware.integration.hub.alert.channel.ChannelTemplateManager;
import com.blackducksoftware.integration.hub.alert.config.AccumulatorConfig;
import com.blackducksoftware.integration.hub.alert.config.DailyDigestBatchConfig;
import com.blackducksoftware.integration.hub.alert.config.GlobalProperties;
import com.blackducksoftware.integration.hub.alert.config.PurgeConfig;
import com.blackducksoftware.integration.hub.alert.datasource.entity.repository.NotificationRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.datasource.entity.repository.VulnerabilityRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.exception.AlertFieldException;
import com.blackducksoftware.integration.hub.alert.scheduling.mock.MockGlobalSchedulingEntity;
import com.blackducksoftware.integration.hub.alert.scheduling.repository.global.GlobalSchedulingConfigEntity;
import com.blackducksoftware.integration.hub.alert.scheduling.repository.global.GlobalSchedulingRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.web.ObjectTransformer;
import com.blackducksoftware.integration.hub.alert.web.actions.global.GlobalActionsTest;

public class GlobalSchedulingConfigActionsTest extends GlobalActionsTest<GlobalSchedulingConfigRestModel, GlobalSchedulingConfigEntity, GlobalSchedulingRepositoryWrapper, GlobalSchedulingConfigActions> {

    @Override
    public GlobalSchedulingConfigActions getMockedConfigActions() {
        return createMockedConfigActionsUsingObjectTransformer(new ObjectTransformer());
    }

    @Override
    public GlobalSchedulingConfigActions createMockedConfigActionsUsingObjectTransformer(final ObjectTransformer objectTransformer) {
        final AccumulatorConfig mockedAccumulatorConfig = Mockito.mock(AccumulatorConfig.class);
        Mockito.when(mockedAccumulatorConfig.getMillisecondsToNextRun()).thenReturn(33000l);
        final DailyDigestBatchConfig mockedDailyDigestBatchConfig = Mockito.mock(DailyDigestBatchConfig.class);
        Mockito.when(mockedDailyDigestBatchConfig.getFormatedNextRunTime()).thenReturn("01/19/2018 02:00 AM UTC");
        final PurgeConfig mockedPurgeConfig = Mockito.mock(PurgeConfig.class);
        Mockito.when(mockedPurgeConfig.getFormatedNextRunTime()).thenReturn("01/21/2018 12:00 AM UTC");

        final GlobalSchedulingRepositoryWrapper globalSchedulingRepository = Mockito.mock(GlobalSchedulingRepositoryWrapper.class);

        final GlobalProperties globalProperties = Mockito.mock(GlobalProperties.class);
        final ChannelTemplateManager channelTemplateManager = Mockito.mock(ChannelTemplateManager.class);
        final NotificationRepositoryWrapper notificationRepository = Mockito.mock(NotificationRepositoryWrapper.class);
        final VulnerabilityRepositoryWrapper vulnerabilityRepository = Mockito.mock(VulnerabilityRepositoryWrapper.class);
        final GlobalSchedulingConfigActions configActions = new GlobalSchedulingConfigActions(mockedAccumulatorConfig, mockedDailyDigestBatchConfig, mockedPurgeConfig, globalSchedulingRepository,
                objectTransformer, globalProperties, channelTemplateManager, new NotificationManager(notificationRepository, vulnerabilityRepository));
        return configActions;
    }

    @Override
    public Class<GlobalSchedulingConfigEntity> getGlobalEntityClass() {
        return GlobalSchedulingConfigEntity.class;
    }

    @Override
    public void testConfigurationChangeTriggers() {
        final AccumulatorConfig mockedAccumulatorConfig = Mockito.mock(AccumulatorConfig.class);
        final DailyDigestBatchConfig mockedDailyDigestBatchConfig = Mockito.mock(DailyDigestBatchConfig.class);
        final PurgeConfig mockedPurgeConfig = Mockito.mock(PurgeConfig.class);

        final GlobalSchedulingRepositoryWrapper globalSchedulingRepository = Mockito.mock(GlobalSchedulingRepositoryWrapper.class);
        Mockito.when(globalSchedulingRepository.findAll()).thenReturn(Arrays.asList(getGlobalEntityMockUtil().createGlobalEntity()));

        final GlobalProperties globalProperties = Mockito.mock(GlobalProperties.class);
        final ChannelTemplateManager channelTemplateManager = Mockito.mock(ChannelTemplateManager.class);
        final NotificationRepositoryWrapper notificationRepository = Mockito.mock(NotificationRepositoryWrapper.class);
        final VulnerabilityRepositoryWrapper vulnerabilityRepository = Mockito.mock(VulnerabilityRepositoryWrapper.class);
        final GlobalSchedulingConfigActions configActions = new GlobalSchedulingConfigActions(mockedAccumulatorConfig, mockedDailyDigestBatchConfig, mockedPurgeConfig, globalSchedulingRepository,
                new ObjectTransformer(), globalProperties, channelTemplateManager, new NotificationManager(notificationRepository, vulnerabilityRepository));
        configActions.configurationChangeTriggers(null);
        Mockito.verify(mockedAccumulatorConfig, Mockito.times(0)).scheduleJobExecution(Mockito.any());
        Mockito.verify(mockedDailyDigestBatchConfig, Mockito.times(0)).scheduleJobExecution(Mockito.any());
        Mockito.verify(mockedPurgeConfig, Mockito.times(0)).scheduleJobExecution(Mockito.any());
        Mockito.reset(mockedAccumulatorConfig);
        Mockito.reset(mockedDailyDigestBatchConfig);
        Mockito.reset(mockedPurgeConfig);

        final GlobalSchedulingConfigRestModel restModel = getGlobalRestModelMockUtil().createGlobalRestModel();
        configActions.configurationChangeTriggers(restModel);
        Mockito.verify(mockedAccumulatorConfig, Mockito.times(0)).scheduleJobExecution(Mockito.any());
        Mockito.verify(mockedDailyDigestBatchConfig, Mockito.times(1)).scheduleJobExecution(Mockito.any());
        Mockito.verify(mockedPurgeConfig, Mockito.times(1)).scheduleJobExecution(Mockito.any());
    }

    @Test
    @Override
    public void testInvalidConfig() {
        final String invalidCron = "invalid";
        final GlobalSchedulingConfigActions configActions = new GlobalSchedulingConfigActions(null, null, null, null, new ObjectTransformer(), null, null, null);
        GlobalSchedulingConfigRestModel restModel = new GlobalSchedulingConfigRestModel("1", invalidCron, invalidCron, invalidCron, invalidCron, invalidCron);

        AlertFieldException caughtException = null;
        try {
            configActions.validateConfig(restModel);
        } catch (final AlertFieldException e) {
            caughtException = e;
        }
        assertNotNull(caughtException);
        assertEquals("Must be a number between 0 and 23", caughtException.getFieldErrors().get("dailyDigestHourOfDay"));
        assertEquals("Must be a number between 1 and 7", caughtException.getFieldErrors().get("purgeDataFrequencyDays"));
        assertEquals(2, caughtException.getFieldErrors().size());

        restModel = new GlobalSchedulingConfigRestModel("1", "-1", "-1", "-1", "-1", "-1");

        caughtException = null;
        try {
            configActions.validateConfig(restModel);
        } catch (final AlertFieldException e) {
            caughtException = e;
        }
        assertNotNull(caughtException);
        assertEquals("Must be a number between 0 and 23", caughtException.getFieldErrors().get("dailyDigestHourOfDay"));
        assertEquals("Must be a number between 1 and 7", caughtException.getFieldErrors().get("purgeDataFrequencyDays"));
        assertEquals(2, caughtException.getFieldErrors().size());

        restModel = new GlobalSchedulingConfigRestModel("1", "100000", "100000", "100000", "100000", "100000");

        caughtException = null;
        try {
            configActions.validateConfig(restModel);
        } catch (final AlertFieldException e) {
            caughtException = e;
        }
        assertNotNull(caughtException);
        assertEquals("Must be a number less than 24", caughtException.getFieldErrors().get("dailyDigestHourOfDay"));
        assertEquals("Must be a number less than 8", caughtException.getFieldErrors().get("purgeDataFrequencyDays"));
        assertEquals(2, caughtException.getFieldErrors().size());

        restModel = new GlobalSchedulingConfigRestModel("1", "", "", "", "", "");

        caughtException = null;
        try {
            configActions.validateConfig(restModel);
        } catch (final AlertFieldException e) {
            caughtException = e;
        }
        assertNotNull(caughtException);
        assertEquals("Must be a number between 0 and 23", caughtException.getFieldErrors().get("dailyDigestHourOfDay"));
        assertEquals("Must be a number between 1 and 7", caughtException.getFieldErrors().get("purgeDataFrequencyDays"));
        assertEquals(2, caughtException.getFieldErrors().size());
    }

    @Test
    public void validateConfigWithValidArgsTest() {
        final GlobalSchedulingConfigActions configActions = new GlobalSchedulingConfigActions(null, null, null, null, new ObjectTransformer(), null, null, null);
        final GlobalSchedulingConfigRestModel restModel = getGlobalRestModelMockUtil().createGlobalRestModel();

        String validationString = null;
        AlertFieldException caughtException = null;
        try {
            validationString = configActions.validateConfig(restModel);
        } catch (final AlertFieldException e) {
            caughtException = e;
        }
        assertNull(caughtException);
        assertEquals("Valid", validationString);
    }

    @Test
    public void runAccumulator() {
        final GlobalSchedulingConfigActions configActions = getMockedConfigActions();
        try {
            configActions.runAccumulator();
        } catch (final Exception e) {
            fail("Should not have thrown an exception : " + e.getMessage());
        }
    }

    @Test
    @Override
    public void testGetConfig() throws Exception {
        Mockito.when(configActions.getRepository().findOne(Mockito.anyLong())).thenReturn(getGlobalEntityMockUtil().createGlobalEntity());
        Mockito.when(configActions.getRepository().findAll()).thenReturn(Arrays.asList(getGlobalEntityMockUtil().createGlobalEntity()));

        // We must mask the rest model because the configActions will have masked those returned by getConfig(...)
        final GlobalSchedulingConfigRestModel restModel = getGlobalRestModelMockUtil().createGlobalRestModel();
        configActions.maskRestModel(restModel);

        List<GlobalSchedulingConfigRestModel> configsById = configActions.getConfig(1L);
        List<GlobalSchedulingConfigRestModel> allConfigs = configActions.getConfig(null);

        assertTrue(configsById.size() == 1);
        assertTrue(allConfigs.size() == 1);

        final GlobalSchedulingConfigRestModel configById = configsById.get(0);
        final GlobalSchedulingConfigRestModel config = allConfigs.get(0);
        assertEquals(restModel, configById);
        assertEquals(restModel, config);

        Mockito.when(configActions.getRepository().findOne(Mockito.anyLong())).thenReturn(null);
        Mockito.when(configActions.getRepository().findAll()).thenReturn(null);

        configsById = configActions.getConfig(1L);
        allConfigs = configActions.getConfig(null);

        assertNotNull(configsById);
        assertNotNull(allConfigs);
        assertTrue(!configsById.isEmpty());
        assertTrue(!allConfigs.isEmpty());
    }

    @Override
    public MockGlobalSchedulingEntity getGlobalEntityMockUtil() {
        return new MockGlobalSchedulingEntity();
    }

    @Override
    public MockGlobalSchedulingRestModel getGlobalRestModelMockUtil() {
        return new MockGlobalSchedulingRestModel();
    }

}
