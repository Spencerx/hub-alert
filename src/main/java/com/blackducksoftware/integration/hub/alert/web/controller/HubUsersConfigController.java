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
package com.blackducksoftware.integration.hub.alert.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.hub.alert.exception.AlertFieldException;
import com.blackducksoftware.integration.hub.alert.web.actions.HubUsersConfigWrapperActions;
import com.blackducksoftware.integration.hub.alert.web.model.HubUsersConfigWrapper;

@RestController
public class HubUsersConfigController implements ConfigController<HubUsersConfigWrapper> {
    private final HubUsersConfigWrapperActions configActions;

    @Autowired
    HubUsersConfigController(final HubUsersConfigWrapperActions configActions) {
        this.configActions = configActions;
    }

    @Override
    @GetMapping(value = "/configuration/users")
    public List<HubUsersConfigWrapper> getConfig(@RequestBody(required = false) final Long id) {
        return configActions.getConfig(id);
    }

    @Override
    @PostMapping(value = "/configuration/users")
    public ResponseEntity<String> postConfig(@RequestBody(required = false) final HubUsersConfigWrapper restModel) {
        return configActions.postConfig(restModel);
    }

    @Override
    @PutMapping(value = "/configuration/users")
    public ResponseEntity<String> putConfig(@RequestBody(required = false) final HubUsersConfigWrapper restModel) {
        return configActions.putConfig(restModel);
    }

    @Override
    @DeleteMapping(value = "/configuration/users")
    public ResponseEntity<String> deleteConfig(@RequestBody(required = false) final HubUsersConfigWrapper restModel) {
        return configActions.deleteConfig(restModel);
    }

    @Override
    @PostMapping(value = "/configuration/users/test")
    public ResponseEntity<String> testConfig(@RequestBody(required = false) final HubUsersConfigWrapper restModel) {
        return configActions.postConfig(restModel);
    }

    @Override
    public ResponseEntity<String> validateConfig(final HubUsersConfigWrapper restModel) {
        try {
            return configActions.validateConfig(restModel).equals("Valid") ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (final AlertFieldException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
