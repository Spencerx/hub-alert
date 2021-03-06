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
package com.blackducksoftware.integration.hub.alert.scheduling.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.blackducksoftware.integration.hub.alert.datasource.entity.GlobalEntityTest;
import com.blackducksoftware.integration.hub.alert.scheduling.mock.MockGlobalSchedulingEntity;
import com.blackducksoftware.integration.hub.alert.scheduling.repository.global.GlobalSchedulingConfigEntity;

public class GlobalSchedulingConfigEntityTest extends GlobalEntityTest<GlobalSchedulingConfigEntity> {

    @Override
    public void assertGlobalEntityFieldsNull(final GlobalSchedulingConfigEntity entity) {
        assertNull(entity.getDailyDigestHourOfDay());
        assertNull(entity.getPurgeDataFrequencyDays());
    }

    @Override
    public void assertGlobalEntityFieldsFull(final GlobalSchedulingConfigEntity entity) {
        assertEquals(getMockUtil().getDailyDigestHourOfDay(), entity.getDailyDigestHourOfDay());
        assertEquals(getMockUtil().getPurgeDataFrequencyDays(), entity.getPurgeDataFrequencyDays());
    }

    @Override
    public Class<GlobalSchedulingConfigEntity> getGlobalEntityClass() {
        return GlobalSchedulingConfigEntity.class;
    }

    @Override
    public MockGlobalSchedulingEntity getMockUtil() {
        return new MockGlobalSchedulingEntity();
    }

}
