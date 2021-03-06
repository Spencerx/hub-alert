/**
 * hub-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.alert.channel.manager;

import java.util.Collections;

import javax.transaction.Transactional;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.alert.channel.DistributionChannel;
import com.blackducksoftware.integration.hub.alert.datasource.SimpleKeyRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.datasource.entity.distribution.DistributionChannelConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.global.GlobalChannelConfigEntity;
import com.blackducksoftware.integration.hub.alert.digest.model.ProjectData;
import com.blackducksoftware.integration.hub.alert.enumeration.DigestTypeEnum;
import com.blackducksoftware.integration.hub.alert.event.AbstractChannelEvent;
import com.blackducksoftware.integration.hub.alert.exception.AlertException;
import com.blackducksoftware.integration.hub.alert.web.ObjectTransformer;
import com.blackducksoftware.integration.hub.alert.web.model.distribution.CommonDistributionConfigRestModel;

@Transactional
public abstract class DistributionChannelManager<G extends GlobalChannelConfigEntity, D extends DistributionChannelConfigEntity, E extends AbstractChannelEvent, R extends CommonDistributionConfigRestModel> {
    private final DistributionChannel<E, G, D> distributionChannel;
    private final SimpleKeyRepositoryWrapper<G, ?> globalRepository;
    private final SimpleKeyRepositoryWrapper<D, ?> localRepository;
    private final ObjectTransformer objectTransformer;

    public DistributionChannelManager(final DistributionChannel<E, G, D> distributionChannel, final SimpleKeyRepositoryWrapper<G, ?> globalRepository, final SimpleKeyRepositoryWrapper<D, ?> localRepository,
            final ObjectTransformer objectTransformer) {
        this.distributionChannel = distributionChannel;
        this.globalRepository = globalRepository;
        this.localRepository = localRepository;
        this.objectTransformer = objectTransformer;
    }

    public DistributionChannel<E, G, D> getDistributionChannel() {
        return distributionChannel;
    }

    public SimpleKeyRepositoryWrapper<G, ?> getGlobalRepository() {
        return globalRepository;
    }

    public SimpleKeyRepositoryWrapper<D, ?> getLocalRepository() {
        return localRepository;
    }

    public ObjectTransformer getObjectTransformer() {
        return objectTransformer;
    }

    public String testGlobalConfig(final G globalConfigEntity) {
        return getDistributionChannel().testGlobalConfig(globalConfigEntity);
    }

    public String sendTestMessage(final R restModel) throws AlertException {
        try {
            final D entity = getObjectTransformer().configRestModelToDatabaseEntity(restModel, getDatabaseEntityClass());
            final E event = createChannelEvent(getTestMessageProjectData(), null);
            getDistributionChannel().sendAuditedMessage(event, entity);
            return "Successfully sent test message";
        } catch (final IntegrationException ex) {
            return ex.getMessage();
        }
    }

    public ProjectData getTestMessageProjectData() {
        return new ProjectData(DigestTypeEnum.REAL_TIME, "Hub Alert", "Test Message", Collections.emptyList(), Collections.emptyMap());
    }

    public abstract Class<D> getDatabaseEntityClass();

    public abstract boolean isApplicable(final String supportedChannelName);

    public abstract E createChannelEvent(final ProjectData projectData, final Long commonDistributionConfigId);

}
