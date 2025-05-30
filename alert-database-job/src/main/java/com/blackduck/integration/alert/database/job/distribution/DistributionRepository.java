/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.database.job.distribution;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blackduck.integration.alert.database.job.DistributionJobEntity;

public interface DistributionRepository extends JpaRepository<DistributionJobEntity, UUID> {

    @Query(
        value =
            "SELECT CAST(job.job_id as varchar) AS id, job.enabled, job.name, job.channel_descriptor_name, job.distribution_frequency, completionStatus.last_run AS time_last_sent, completionStatus.latest_status AS status"
                + " FROM alert.distribution_jobs AS job"
                + " LEFT JOIN alert.job_completion_status AS completionStatus ON completionStatus.job_config_id = job.job_id"
                + " WHERE job.channel_descriptor_name IN (:channelDescriptorNames)",
        nativeQuery = true
    )
    Page<DistributionDBResponse> getDistributionWithAuditInfo(Pageable pageable, @Param("channelDescriptorNames") Collection<String> channelDescriptorNames);

    @Query(
        value =
            "SELECT CAST(job.job_id as varchar) AS id, job.enabled, job.name, job.channel_descriptor_name, job.distribution_frequency, completionStatus.last_run AS time_last_sent, completionStatus.latest_status AS status"
                + " FROM alert.distribution_jobs AS job"
                + " LEFT JOIN alert.job_completion_status AS completionStatus ON completionStatus.job_config_id = job.job_id"
                + " WHERE job.channel_descriptor_name IN (:channelDescriptorNames) AND (job.name ILIKE %:searchTerm%"
                + " OR job.distribution_frequency ILIKE %:searchTerm%"
                + " OR job.channel_descriptor_name ILIKE %:searchTerm%"
                + " OR COALESCE(to_char(completionStatus.last_run, 'MM/DD/YYYY, HH24:MI:SS'), '') LIKE %:searchTerm%"
                + " OR completionStatus.latest_status ILIKE %:searchTerm%)",
        nativeQuery = true
    )
    Page<DistributionDBResponse> getDistributionWithAuditInfoWithSearch(
        Pageable pageable,
        @Param("channelDescriptorNames") Collection<String> channelDescriptorNames,
        @Param("searchTerm") String searchTerm
    );
    
    interface DistributionDBResponse {
        String getId();

        Boolean getEnabled();

        String getName();

        String getChannel_Descriptor_Name();

        String getDistribution_Frequency();

        Instant getTime_Last_Sent();

        String getStatus();

    }
}
