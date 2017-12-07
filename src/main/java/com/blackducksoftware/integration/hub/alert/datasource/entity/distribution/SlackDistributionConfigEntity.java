/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.alert.datasource.entity.distribution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.blackducksoftware.integration.hub.alert.datasource.entity.DatabaseEntity;

@Entity
@Table(schema = "alert", name = "slack_distribution_config")
public class SlackDistributionConfigEntity extends DatabaseEntity {
    private static final long serialVersionUID = 3607759169675906880L;

    @Column(name = "webhook")
    private String webhook;

    @Column(name = "channel_username")
    private String channelUsername = "Hub-alert";

    @Column(name = "channel_name")
    private String channelName;

    public SlackDistributionConfigEntity() {
    }

    public SlackDistributionConfigEntity(final String webhook, final String channelUsername, final String channelName) {
        super();
        this.webhook = webhook;
        this.channelUsername = channelUsername;
        this.channelName = channelName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getWebhook() {
        return webhook;
    }

    public String getChannelUsername() {
        return channelUsername;
    }

    public String getChannelName() {
        return channelName;
    }

}