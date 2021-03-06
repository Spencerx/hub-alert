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
package com.blackducksoftware.integration.hub.alert;

import org.mockito.Mockito;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.alert.config.GlobalProperties;
import com.blackducksoftware.integration.hub.alert.datasource.entity.global.GlobalHubConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.repository.global.GlobalHubRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.exception.AlertException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;

public class TestGlobalProperties extends GlobalProperties {
    private Integer hubTimeout;
    private String hubApiKey;

    private final TestProperties testProperties;

    public TestGlobalProperties() {
        this(Mockito.mock(GlobalHubRepositoryWrapper.class));
    }

    public TestGlobalProperties(final GlobalHubRepositoryWrapper globalHubRepositoryWrapper) {
        this(globalHubRepositoryWrapper, 400);
    }

    public TestGlobalProperties(final GlobalHubRepositoryWrapper globalHubRepositoryWrapper, final Integer hubTimeout) {
        super(globalHubRepositoryWrapper);
        this.hubTimeout = hubTimeout;

        testProperties = new TestProperties();
    }

    @Override
    public Integer getHubTimeout() {
        return hubTimeout;
    }

    public void setHubTimeout(final Integer hubTimeout) {
        this.hubTimeout = hubTimeout;
    }

    @Override
    public String getHubApiKey() {
        return hubApiKey;
    }

    public void setHubApiKey(final String hubApiKey) {
        this.hubApiKey = hubApiKey;
    }

    @Override
    public HubServicesFactory createHubServicesFactory(final IntLogger intLogger) throws IntegrationException {
        setHubUrl(testProperties.getProperty(TestPropertyKey.TEST_HUB_SERVER_URL));
        setHubTrustCertificate(true);
        return super.createHubServicesFactory(intLogger);
    }

    @Override
    public GlobalHubConfigEntity getHubConfig() {
        return new GlobalHubConfigEntity(Integer.valueOf(testProperties.getProperty(TestPropertyKey.TEST_HUB_TIMEOUT)), testProperties.getProperty(TestPropertyKey.TEST_HUB_API_KEY));
    }

    @Override
    public HubServerConfig createHubServerConfig(final IntLogger logger, final int hubTimeout, final String hubUsername, final String hubPassword) throws AlertException {
        return createHubServerConfigWithCredentials(logger);
    }

    public HubServerConfig createHubServerConfigWithCredentials(final IntLogger logger) throws NumberFormatException, AlertException {
        return super.createHubServerConfig(logger, Integer.valueOf(testProperties.getProperty(TestPropertyKey.TEST_HUB_TIMEOUT)), testProperties.getProperty(TestPropertyKey.TEST_USERNAME),
                testProperties.getProperty(TestPropertyKey.TEST_PASSWORD));
    }

    public HubServicesFactory createHubServicesFactoryWithCredential(final IntLogger logger) throws Exception {
        setHubUrl(testProperties.getProperty(TestPropertyKey.TEST_HUB_SERVER_URL));
        setHubTrustCertificate(true);
        final HubServerConfig hubServerConfig = createHubServerConfigWithCredentials(logger);
        final RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(logger);
        return new HubServicesFactory(restConnection);
    }

}
