/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.api.common.model;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

class AlertConstantsTest {
    @Test
    void privateConstructorTest() {
        for (Constructor<?> constructor : AlertConstants.class.getConstructors()) {
            try {
                constructor.newInstance();
                fail("Expected exception to be thrown");
            } catch (InstantiationException e) {
                // Pass
            } catch (Exception e) {
                fail("Unexpected exception type", e);
            }
        }
    }

}
