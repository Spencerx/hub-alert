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
package com.blackducksoftware.integration.hub.alert.datasource.entity.repository.global;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.encryption.PasswordDecrypter;
import com.blackducksoftware.integration.encryption.PasswordEncrypter;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.alert.datasource.SimpleKeyRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.datasource.entity.global.GlobalHubConfigEntity;

@Component
public class GlobalHubRepositoryWrapper extends SimpleKeyRepositoryWrapper<GlobalHubConfigEntity, GlobalHubRepository> {

    @Autowired
    public GlobalHubRepositoryWrapper(final GlobalHubRepository repository) {
        super(repository);
    }

    @Override
    public GlobalHubConfigEntity encryptSensitiveData(final GlobalHubConfigEntity entity) throws EncryptionException {
        String hubApiKey = entity.getHubApiKey();

        if (StringUtils.isBlank(hubApiKey)) {
            return entity;
        } else {
            final Integer hubTimeout = entity.getHubTimeout();
            hubApiKey = PasswordEncrypter.encrypt(hubApiKey);
            final GlobalHubConfigEntity newEntity = new GlobalHubConfigEntity(hubTimeout, hubApiKey);
            newEntity.setId(entity.getId());
            return newEntity;
        }
    }

    @Override
    public GlobalHubConfigEntity decryptSensitiveData(final GlobalHubConfigEntity entity) throws EncryptionException {
        String hubApiKey = entity.getHubApiKey();

        if (StringUtils.isBlank(hubApiKey)) {
            return entity;
        } else {
            final Integer hubTimeout = entity.getHubTimeout();
            hubApiKey = PasswordDecrypter.decrypt(hubApiKey);
            final GlobalHubConfigEntity newEntity = new GlobalHubConfigEntity(hubTimeout, hubApiKey);
            newEntity.setId(entity.getId());
            return newEntity;
        }
    }
}
