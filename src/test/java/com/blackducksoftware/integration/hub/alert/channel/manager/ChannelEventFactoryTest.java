/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.alert.channel.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.alert.datasource.entity.distribution.DistributionChannelConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.global.GlobalChannelConfigEntity;
import com.blackducksoftware.integration.hub.alert.digest.model.ProjectData;
import com.blackducksoftware.integration.hub.alert.enumeration.DigestTypeEnum;
import com.blackducksoftware.integration.hub.alert.event.AbstractChannelEvent;
import com.blackducksoftware.integration.hub.alert.web.model.distribution.CommonDistributionConfigRestModel;

public class ChannelEventFactoryTest {
    private static final String DISTRIBUTION_TYPE = "TYPE";

    @Test
    public void createEventWithNoChannelManagersTest() {
        final ChannelEventFactory<AbstractChannelEvent, DistributionChannelConfigEntity, GlobalChannelConfigEntity, CommonDistributionConfigRestModel> factory = new ChannelEventFactory<>(Collections.emptyList());
        assertNull(factory.createEvent(1L, DISTRIBUTION_TYPE, null));
    }

    @Test
    public void createEventWithChannelManagerTest() {
        final DistributionChannelManager<GlobalChannelConfigEntity, DistributionChannelConfigEntity, AbstractChannelEvent, CommonDistributionConfigRestModel> manager = Mockito.mock(DistributionChannelManager.class);
        final List<DistributionChannelManager<GlobalChannelConfigEntity, DistributionChannelConfigEntity, AbstractChannelEvent, CommonDistributionConfigRestModel>> managers = Arrays.asList(manager);
        final ChannelEventFactory<AbstractChannelEvent, DistributionChannelConfigEntity, GlobalChannelConfigEntity, CommonDistributionConfigRestModel> factory = new ChannelEventFactory<>(managers);

        final Long id = 25L;
        final ProjectData projectData = new ProjectData(DigestTypeEnum.REAL_TIME, "Project Name", "Project Version", Collections.emptyList(), Collections.emptyMap());
        final AbstractChannelEvent mockEvent = new AbstractChannelEvent(projectData, id) {
            @Override
            public String getTopic() {
                return DISTRIBUTION_TYPE;
            }
        };
        Mockito.when(manager.isApplicable(DISTRIBUTION_TYPE)).thenReturn(true);
        Mockito.when(manager.createChannelEvent(Mockito.any(), Mockito.anyLong())).thenReturn(mockEvent);

        final AbstractChannelEvent event = factory.createEvent(id, "TYPE", projectData);
        assertEquals(mockEvent, event);
    }

}
