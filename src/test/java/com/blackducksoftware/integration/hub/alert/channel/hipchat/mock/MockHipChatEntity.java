/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.alert.channel.hipchat.mock;

import com.blackducksoftware.integration.hub.alert.channel.hipchat.repository.distribution.HipChatDistributionConfigEntity;
import com.blackducksoftware.integration.hub.alert.mock.entity.MockEntityUtil;
import com.google.gson.JsonObject;

public class MockHipChatEntity extends MockEntityUtil<HipChatDistributionConfigEntity> {
    private final Integer roomId;
    private final Boolean notify;
    private final String color;
    private final Long id;

    public MockHipChatEntity() {
        this(11, false, "black", 1L);
    }

    private MockHipChatEntity(final Integer roomId, final Boolean notify, final String color, final Long id) {
        this.roomId = roomId;
        this.notify = notify;
        this.color = color;
        this.id = id;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Boolean isNotify() {
        return notify;
    }

    public String getColor() {
        return color;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public HipChatDistributionConfigEntity createEntity() {
        final HipChatDistributionConfigEntity hipChatDistributionConfigEntity = new HipChatDistributionConfigEntity(roomId, notify, color);
        hipChatDistributionConfigEntity.setId(id);
        return hipChatDistributionConfigEntity;
    }

    @Override
    public HipChatDistributionConfigEntity createEmptyEntity() {
        return new HipChatDistributionConfigEntity();
    }

    @Override
    public String getEntityJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("roomId", roomId);
        json.addProperty("notify", notify);
        json.addProperty("color", color);
        json.addProperty("id", id);
        return json.toString();
    }

}
