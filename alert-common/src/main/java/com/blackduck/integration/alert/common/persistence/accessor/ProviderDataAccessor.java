/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.common.persistence.accessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.blackduck.integration.alert.api.common.model.exception.AlertConfigurationException;
import com.blackduck.integration.alert.common.persistence.model.ProviderProject;
import com.blackduck.integration.alert.common.persistence.model.ProviderUserModel;
import com.blackduck.integration.alert.common.rest.model.AlertPagedModel;

public interface ProviderDataAccessor {
    Optional<ProviderProject> getProjectByHref(Long providerConfigId, String projectHref);

    List<ProviderProject> getProjectsByProviderConfigName(String providerConfigName);

    List<ProviderProject> getProjectsByProviderConfigId(Long providerConfigId);

    AlertPagedModel<ProviderProject> getProjectsByProviderConfigId(Long providerConfigId, int pageNumber, int pageSize, String searchTerm);

    AlertPagedModel<String> getProjectVersionNamesByHref(Long providerConfigId, String projectHref, int pageNumber);

    @Deprecated
    void deleteProjects(Collection<ProviderProject> providerProjects);

    Set<String> getEmailAddressesForProjectHref(Long providerConfigId, String projectHref);

    ProviderUserModel getProviderConfigUserById(Long providerConfigId) throws AlertConfigurationException;

    List<ProviderUserModel> getUsersByProviderConfigId(Long providerConfigId);

    AlertPagedModel<ProviderUserModel> getUsersByProviderConfigId(Long providerConfigId, int pageNumber, int pageSize, String searchTerm);

    List<ProviderUserModel> getUsersByProviderConfigName(String providerConfigName);

    Optional<ProviderUserModel> findFirstUserByEmailAddress(Long providerConfigId, String emailAddress);

    @Deprecated
    void updateProjectAndUserData(Long providerConfigId, Map<ProviderProject, Set<String>> projectToUserData, Set<String> additionalRelevantUsers);

}
