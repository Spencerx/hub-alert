/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.channel.email.database.configuration.properties;

import java.util.UUID;

import com.blackduck.integration.alert.database.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(EmailConfigurationPropertyPK.class)
@Table(schema = "alert", name = "configuration_email_properties")
public class EmailConfigurationsPropertyEntity extends BaseEntity {
    private static final long serialVersionUID = 1999396035336143585L;
    @Id
    @Column(name = "configuration_id")
    private UUID configurationId;
    @Column(name = "property_key")
    private String propertyKey;
    @Column(name = "property_value")
    private String propertyValue;

    public EmailConfigurationsPropertyEntity() {
    }

    public EmailConfigurationsPropertyEntity(UUID configurationId, String propertyKey, String propertyValue) {
        this.configurationId = configurationId;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
    }

    public UUID getConfigurationId() {
        return configurationId;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}
