/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.provider.blackduck.task;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import com.blackduck.integration.alert.api.common.model.exception.AlertRuntimeException;
import com.blackduck.integration.alert.api.descriptor.BlackDuckProviderKey;
import com.blackduck.integration.alert.api.provider.lifecycle.ProviderTask;
import com.blackduck.integration.alert.api.provider.state.ProviderProperties;
import com.blackduck.integration.alert.api.task.ScheduledTask;
import com.blackduck.integration.alert.common.persistence.accessor.ProviderDataAccessor;
import com.blackduck.integration.alert.common.persistence.model.ProviderProject;
import com.blackduck.integration.alert.provider.blackduck.BlackDuckProperties;
import com.blackduck.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.blackduck.integration.blackduck.api.generated.view.UserView;
import com.blackduck.integration.blackduck.api.manual.view.ProjectView;
import com.blackduck.integration.blackduck.http.client.BlackDuckHttpClient;
import com.blackduck.integration.blackduck.service.BlackDuckApiClient;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.blackduck.service.dataservice.ProjectUsersService;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.log.Slf4jIntLogger;
import com.blackduck.integration.rest.HttpUrl;

public class BlackDuckDataSyncTask extends ProviderTask {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckDataSyncTask.class);
    private final ProviderDataAccessor blackDuckDataAccessor;

    public BlackDuckDataSyncTask(BlackDuckProviderKey blackDuckProviderKey, TaskScheduler taskScheduler, ProviderDataAccessor blackDuckDataAccessor, ProviderProperties providerProperties) {
        super(blackDuckProviderKey, taskScheduler, providerProperties);
        this.blackDuckDataAccessor = blackDuckDataAccessor;
    }

    @Override
    public void runProviderTask() {
        try {
            BlackDuckProperties providerProperties = getProviderProperties();
            Optional<BlackDuckHttpClient> optionalBlackDuckHttpClient = providerProperties.createBlackDuckHttpClientAndLogErrors(logger);
            if (optionalBlackDuckHttpClient.isPresent()) {
                BlackDuckHttpClient blackDuckHttpClient = optionalBlackDuckHttpClient.get();
                BlackDuckServicesFactory blackDuckServicesFactory = providerProperties.createBlackDuckServicesFactory(blackDuckHttpClient, new Slf4jIntLogger(logger));
                ProjectUsersService projectUsersService = blackDuckServicesFactory.createProjectUsersService();
                BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
                ApiDiscovery apiDiscovery = blackDuckServicesFactory.getApiDiscovery();

                List<ProjectView> projectViews = blackDuckApiClient.getAllResponses(apiDiscovery.metaProjectsLink());
                Map<ProjectView, ProviderProject> blackDuckToAlertProjects = mapBlackDuckProjectsToAlertProjects(projectViews, blackDuckApiClient);

                Map<ProviderProject, Set<String>> projectToEmailAddresses = getEmailsPerProject(blackDuckToAlertProjects, projectUsersService);
                Set<String> allRelevantBlackDuckUsers = getAllActiveBlackDuckUserEmailAddresses(blackDuckApiClient, apiDiscovery);
                blackDuckDataAccessor.updateProjectAndUserData(providerProperties.getConfigId(), projectToEmailAddresses, allRelevantBlackDuckUsers);
            } else {
                logger.error("Missing Black Duck global configuration.");
            }
        } catch (IntegrationException | AlertRuntimeException e) {
            logger.error(String.format("Could not retrieve the current data from the Black Duck server: %s", e.getMessage()), e);
        }
    }

    @Override
    protected BlackDuckProperties getProviderProperties() {
        return (BlackDuckProperties) super.getProviderProperties();
    }

    private Map<ProjectView, ProviderProject> mapBlackDuckProjectsToAlertProjects(List<ProjectView> projectViews, BlackDuckApiClient blackDuckApiClient) {
        Map<ProjectView, ProviderProject> projectMap = new ConcurrentHashMap<>();
        projectViews
            .parallelStream()
            .forEach(projectView -> {
                String projectOwnerEmail = null;
                if (StringUtils.isNotBlank(projectView.getProjectOwner())) {
                    try {
                        HttpUrl projectOwnerHttpUrl = new HttpUrl(projectView.getProjectOwner());
                        UserView projectOwner = blackDuckApiClient.getResponse(projectOwnerHttpUrl, UserView.class);
                        projectOwnerEmail = projectOwner.getEmail();
                    } catch (IntegrationException e) {
                        logger.error(String.format("Could not get the project owner for Project: %s. Error: %s", projectView.getName(), e.getMessage()), e);
                    }
                }
                projectMap.put(projectView, new ProviderProject(projectView.getName(), StringUtils.trimToEmpty(projectView.getDescription()), projectView.getMeta().getHref().toString(), projectOwnerEmail));
            });
        return projectMap;
    }

    private Map<ProviderProject, Set<String>> getEmailsPerProject(Map<ProjectView, ProviderProject> blackDuckToAlertProjects, ProjectUsersService projectUsersService) {
        Map<ProviderProject, Set<String>> projectToEmailAddresses = new ConcurrentHashMap<>();
        blackDuckToAlertProjects.entrySet()
            .parallelStream()
            .forEach(entry -> {
                try {
                    ProjectView blackDuckProjectView = entry.getKey();
                    ProviderProject alertProject = entry.getValue();
                    Set<String> projectUserEmailAddresses = projectUsersService.getAllActiveUsersForProject(blackDuckProjectView)
                                                                .stream()
                                                                .filter(UserView::getActive)
                                                                .map(UserView::getEmail)
                                                                .filter(StringUtils::isNotBlank)
                                                                .collect(Collectors.toSet());
                    if (StringUtils.isNotBlank(alertProject.getProjectOwnerEmail())) {
                        projectUserEmailAddresses.add(alertProject.getProjectOwnerEmail());
                    }
                    projectToEmailAddresses.put(alertProject, projectUserEmailAddresses);
                } catch (IntegrationException e) {
                    // We do this to break out of the stream
                    throw new AlertRuntimeException(e.getMessage(), e);
                }
            });
        return projectToEmailAddresses;
    }

    private Set<String> getAllActiveBlackDuckUserEmailAddresses(BlackDuckApiClient blackDuckService, ApiDiscovery apiDiscovery) throws IntegrationException {
        return blackDuckService.getAllResponses(apiDiscovery.metaUsersLink())
                   .stream()
                   .filter(UserView::getActive)
                   .map(UserView::getEmail)
                   .filter(StringUtils::isNotBlank)
                   .collect(Collectors.toSet());
    }

    @Override
    public String scheduleCronExpression() {
        return ScheduledTask.ONCE_DAILY_CRON_EXPRESSION;
    }

}
