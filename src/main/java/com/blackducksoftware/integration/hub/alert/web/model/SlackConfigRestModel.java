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
package com.blackducksoftware.integration.hub.alert.web.model;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SlackConfigRestModel extends ConfigRestModel {
    private static final long serialVersionUID = -2360827976516988339L;

    private String channelName;
    private String username;
    private String webhook;

    public SlackConfigRestModel() {
    }

    public SlackConfigRestModel(final String id, final String channelName, final String username, final String webhook) {
        super(id);
        this.channelName = channelName;
        this.username = username;
        this.webhook = webhook;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getUsername() {
        return username;
    }

    public String getWebhook() {
        return webhook;
    }

    @Override
    public String toString() {
        final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this, RecursiveToStringStyle.JSON_STYLE);
        return reflectionToStringBuilder.toString();
    }

}