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
package com.blackducksoftware.integration.hub.alert.channel.hipchat.controller.global;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.hub.alert.channel.hipchat.repository.global.GlobalHipChatConfigEntity;
import com.blackducksoftware.integration.hub.alert.channel.hipchat.repository.global.GlobalHipChatRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.web.ObjectTransformer;
import com.blackducksoftware.integration.hub.alert.web.controller.ConfigController;
import com.blackducksoftware.integration.hub.alert.web.controller.handler.CommonConfigHandler;
import com.blackducksoftware.integration.hub.alert.web.controller.handler.CommonGlobalConfigHandler;

@RestController
public class GlobalHipChatConfigController extends ConfigController<GlobalHipChatConfigRestModel> {
    private final CommonConfigHandler<GlobalHipChatConfigEntity, GlobalHipChatConfigRestModel, GlobalHipChatRepositoryWrapper> commonConfigHandler;

    @Autowired
    public GlobalHipChatConfigController(final GlobalHipChatConfigActions configActions, final ObjectTransformer objectTransformer) {
        commonConfigHandler = new CommonGlobalConfigHandler<>(GlobalHipChatConfigEntity.class, GlobalHipChatConfigRestModel.class, configActions, objectTransformer);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/configuration/global/hipchat")
    public List<GlobalHipChatConfigRestModel> getConfig(@RequestParam(value = "id", required = false) final Long id) {
        return commonConfigHandler.getConfig(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/configuration/global/hipchat")
    public ResponseEntity<String> postConfig(@RequestBody(required = false) final GlobalHipChatConfigRestModel hipChatConfig) {
        return commonConfigHandler.postConfig(hipChatConfig);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/configuration/global/hipchat")
    public ResponseEntity<String> putConfig(@RequestBody(required = false) final GlobalHipChatConfigRestModel hipChatConfig) {
        return commonConfigHandler.putConfig(hipChatConfig);
    }

    @Override
    public ResponseEntity<String> validateConfig(final GlobalHipChatConfigRestModel hipChatConfig) {
        return commonConfigHandler.validateConfig(hipChatConfig);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/configuration/global/hipchat")
    public ResponseEntity<String> deleteConfig(@RequestBody(required = false) final GlobalHipChatConfigRestModel hipChatConfig) {
        return commonConfigHandler.deleteConfig(hipChatConfig);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/configuration/global/hipchat/test")
    public ResponseEntity<String> testConfig(@RequestBody(required = false) final GlobalHipChatConfigRestModel hipChatConfig) {
        return commonConfigHandler.testConfig(hipChatConfig);
    }

}
