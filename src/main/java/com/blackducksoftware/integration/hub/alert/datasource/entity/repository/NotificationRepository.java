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
package com.blackducksoftware.integration.hub.alert.datasource.entity.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blackducksoftware.integration.hub.alert.datasource.entity.NotificationEntity;

@Transactional
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    @Query("SELECT entity FROM NotificationEntity entity WHERE entity.createdAt BETWEEN ?1 AND ?2 ORDER BY created_at asc")
    List<NotificationEntity> findByCreatedAtBetween(final Date startDate, final Date endDate);

    @Query("SELECT entity FROM NotificationEntity entity WHERE entity.createdAt < ?1 ORDER BY created_at asc")
    List<NotificationEntity> findByCreatedAtBefore(final Date date);
}
