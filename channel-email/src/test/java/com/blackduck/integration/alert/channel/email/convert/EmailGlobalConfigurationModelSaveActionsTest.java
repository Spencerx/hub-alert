/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.channel.email.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.blackduck.integration.alert.api.descriptor.model.ChannelKeys;
import com.blackduck.integration.alert.api.descriptor.model.DescriptorKey;
import com.blackduck.integration.alert.channel.email.action.EmailGlobalCrudActions;
import com.blackduck.integration.alert.channel.email.database.accessor.EmailGlobalConfigAccessor;
import com.blackduck.integration.alert.channel.email.database.configuration.EmailConfigurationEntity;
import com.blackduck.integration.alert.channel.email.database.configuration.EmailConfigurationRepository;
import com.blackduck.integration.alert.channel.email.database.configuration.properties.EmailConfigurationPropertiesRepository;
import com.blackduck.integration.alert.channel.email.database.configuration.properties.EmailConfigurationsPropertyEntity;
import com.blackduck.integration.alert.channel.email.validator.EmailGlobalConfigurationValidator;
import com.blackduck.integration.alert.common.AlertProperties;
import com.blackduck.integration.alert.common.enumeration.ConfigContextEnum;
import com.blackduck.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.blackduck.integration.alert.common.persistence.model.ConfigurationModel;
import com.blackduck.integration.alert.common.persistence.model.PermissionKey;
import com.blackduck.integration.alert.common.persistence.model.PermissionMatrixModel;
import com.blackduck.integration.alert.common.persistence.util.FilePersistenceUtil;
import com.blackduck.integration.alert.common.security.EncryptionUtility;
import com.blackduck.integration.alert.common.security.authorization.AuthorizationManager;
import com.blackduck.integration.alert.service.email.enumeration.EmailPropertyKeys;
import com.blackduck.integration.alert.test.common.AuthenticationTestUtils;
import com.blackduck.integration.alert.test.common.MockAlertProperties;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.google.gson.Gson;

class EmailGlobalConfigurationModelSaveActionsTest {

    public static final String TEST_AUTH_REQUIRED = "true";
    public static final String TEST_FROM = "test.user@some.company.example.com";
    public static final String TEST_SMTP_HOST = "smtp.server.example.com";
    public static final String TEST_AUTH_PASSWORD = "apassword";
    public static final String TEST_SMTP_PORT = "2025";
    public static final String TEST_AUTH_USER = "auser";

    private final Gson gson = BlackDuckServicesFactory.createDefaultGson();
    private final AlertProperties alertProperties = new MockAlertProperties();
    private final FilePersistenceUtil filePersistenceUtil = new FilePersistenceUtil(alertProperties, gson);
    private final EncryptionUtility encryptionUtility = new EncryptionUtility(alertProperties, filePersistenceUtil);
    private final AuthorizationManager authorizationManager = createAuthorizationManager();
    private final EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
    private final EmailGlobalConfigurationModelConverter converter = new EmailGlobalConfigurationModelConverter(validator);

    @Test
    void getDescriptorKeyTest() {
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(null, null, null);
        assertEquals(ChannelKeys.EMAIL, saveActions.getDescriptorKey());
    }

    @Test
    void createTest() {
        AtomicReference<EmailConfigurationEntity> savedEntity = new AtomicReference<>();
        AtomicReference<EmailConfigurationsPropertyEntity> savedProperty = new AtomicReference<>();
        EmailConfigurationRepository emailConfigurationRepository = Mockito.mock(EmailConfigurationRepository.class);
        EmailConfigurationPropertiesRepository emailConfigurationPropertiesRepository = Mockito.mock(EmailConfigurationPropertiesRepository.class);
        Mockito.when(emailConfigurationRepository.save(Mockito.any(EmailConfigurationEntity.class))).thenAnswer(invocation -> {
            savedEntity.set(invocation.getArgument(0));
            return savedEntity.get();
        });

        Mockito.when(emailConfigurationPropertiesRepository.saveAll(Mockito.any(List.class))).thenAnswer(invocation -> {
            Iterable<EmailConfigurationsPropertyEntity> iterable = invocation.getArgument(0);
            for (EmailConfigurationsPropertyEntity entity : iterable) {
                savedProperty.set(entity);
            }
            return List.of(savedProperty.get());
        });

        EmailGlobalConfigAccessor configurationAccessor = new EmailGlobalConfigAccessor(encryptionUtility, emailConfigurationRepository, emailConfigurationPropertiesRepository);
        EmailGlobalCrudActions crudActions = new EmailGlobalCrudActions(authorizationManager, configurationAccessor, validator);
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(converter, crudActions, configurationAccessor);
        saveActions.createConcreteModel(createDefaultConfigurationModel());

        EmailConfigurationEntity actualEntity = savedEntity.get();
        assertEquals(Boolean.TRUE, actualEntity.getAuthRequired());
        assertEquals(TEST_AUTH_USER, actualEntity.getAuthUsername());
        assertEquals(TEST_AUTH_PASSWORD, encryptionUtility.decrypt(actualEntity.getAuthPassword()));
        assertEquals(TEST_SMTP_HOST, actualEntity.getSmtpHost());
        assertEquals(Integer.valueOf(TEST_SMTP_PORT), actualEntity.getSmtpPort());
        assertEquals(TEST_FROM, actualEntity.getSmtpFrom());

        EmailConfigurationsPropertyEntity emailProperty = savedProperty.get();
        assertNotNull(emailProperty);
        assertEquals(EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), emailProperty.getPropertyKey());
        assertEquals("true", emailProperty.getPropertyValue());
    }

    @Test
    void createInvalidConversionTest() {
        AtomicReference<EmailConfigurationEntity> savedEntity = new AtomicReference<>();
        AtomicReference<EmailConfigurationsPropertyEntity> savedProperty = new AtomicReference<>();
        EmailConfigurationRepository emailConfigurationRepository = Mockito.mock(EmailConfigurationRepository.class);
        EmailConfigurationPropertiesRepository emailConfigurationPropertiesRepository = Mockito.mock(EmailConfigurationPropertiesRepository.class);
        Mockito.when(emailConfigurationRepository.save(Mockito.any(EmailConfigurationEntity.class))).thenAnswer(invocation -> {
            savedEntity.set(invocation.getArgument(0));
            return savedEntity.get();
        });

        Mockito.when(emailConfigurationPropertiesRepository.saveAll(Mockito.any(List.class))).thenAnswer(invocation -> {
            Iterable<EmailConfigurationsPropertyEntity> iterable = invocation.getArgument(0);
            for (EmailConfigurationsPropertyEntity entity : iterable) {
                savedProperty.set(entity);
            }
            return List.of(savedProperty.get());
        });

        EmailGlobalConfigAccessor configurationAccessor = new EmailGlobalConfigAccessor(encryptionUtility, emailConfigurationRepository, emailConfigurationPropertiesRepository);
        EmailGlobalCrudActions crudActions = new EmailGlobalCrudActions(authorizationManager, configurationAccessor, validator);
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(converter, crudActions, configurationAccessor);
        ConfigurationModel configurationModel = createDefaultConfigurationModel();
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), "badport");
        saveActions.createConcreteModel(configurationModel);

        EmailConfigurationEntity actualEntity = savedEntity.get();
        EmailConfigurationsPropertyEntity emailProperty = savedProperty.get();
        assertNull(actualEntity);
        assertNull(emailProperty);
    }

    @Test
    void updateTest() {
        AtomicReference<EmailConfigurationEntity> savedEntity = new AtomicReference<>();
        AtomicReference<EmailConfigurationsPropertyEntity> savedProperty = new AtomicReference<>();
        EmailConfigurationRepository emailConfigurationRepository = Mockito.mock(EmailConfigurationRepository.class);
        EmailConfigurationPropertiesRepository emailConfigurationPropertiesRepository = Mockito.mock(EmailConfigurationPropertiesRepository.class);
        Mockito.when(emailConfigurationRepository.save(Mockito.any(EmailConfigurationEntity.class))).thenAnswer(invocation -> {
            savedEntity.set(invocation.getArgument(0));
            return savedEntity.get();
        });

        Mockito.when(emailConfigurationRepository.findByName(Mockito.anyString())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));
        Mockito.when(emailConfigurationRepository.findById(Mockito.any())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));
        Mockito.when(emailConfigurationRepository.existsByName(Mockito.any())).thenAnswer(invocation -> savedEntity.get() != null);
        //        Mockito.when(emailConfigurationRepository.existsByName(Mockito.anyString())).thenReturn(true);

        Mockito.when(emailConfigurationPropertiesRepository.saveAll(Mockito.any(List.class))).thenAnswer(invocation -> {
            Iterable<EmailConfigurationsPropertyEntity> iterable = invocation.getArgument(0);
            for (EmailConfigurationsPropertyEntity entity : iterable) {
                savedProperty.set(entity);
            }
            return List.of(savedProperty.get());
        });

        EmailGlobalConfigAccessor configurationAccessor = new EmailGlobalConfigAccessor(encryptionUtility, emailConfigurationRepository, emailConfigurationPropertiesRepository);
        EmailGlobalCrudActions crudActions = new EmailGlobalCrudActions(authorizationManager, configurationAccessor, validator);
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(converter, crudActions, configurationAccessor);
        String newPassword = "updatedPassword";
        String newHost = "updated." + TEST_SMTP_HOST;
        ConfigurationModel configurationModel = createDefaultConfigurationModel();
        saveActions.createConcreteModel(configurationModel);

        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_HOST_KEY.getPropertyKey(), newHost);
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_PASSWORD_KEY.getPropertyKey(), newPassword);
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), "false");

        saveActions.updateConcreteModel(configurationModel);

        EmailConfigurationEntity actualEntity = savedEntity.get();
        assertEquals(ChannelKeys.EMAIL, saveActions.getDescriptorKey());
        assertEquals(Boolean.TRUE, actualEntity.getAuthRequired());
        assertEquals(TEST_AUTH_USER, actualEntity.getAuthUsername());
        assertEquals(newPassword, encryptionUtility.decrypt(actualEntity.getAuthPassword()));
        assertEquals(newHost, actualEntity.getSmtpHost());
        assertEquals(Integer.valueOf(TEST_SMTP_PORT), actualEntity.getSmtpPort());
        assertEquals(TEST_FROM, actualEntity.getSmtpFrom());

        EmailConfigurationsPropertyEntity emailProperty = savedProperty.get();
        assertNotNull(emailProperty);
        assertEquals(EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), emailProperty.getPropertyKey());
        assertEquals("false", emailProperty.getPropertyValue());
    }

    @Test
    void updateInvalidConversionTest() {
        AtomicReference<EmailConfigurationEntity> savedEntity = new AtomicReference<>();
        AtomicReference<EmailConfigurationsPropertyEntity> savedProperty = new AtomicReference<>();
        EmailConfigurationRepository emailConfigurationRepository = Mockito.mock(EmailConfigurationRepository.class);
        EmailConfigurationPropertiesRepository emailConfigurationPropertiesRepository = Mockito.mock(EmailConfigurationPropertiesRepository.class);
        Mockito.when(emailConfigurationRepository.save(Mockito.any(EmailConfigurationEntity.class))).thenAnswer(invocation -> {
            savedEntity.set(invocation.getArgument(0));
            return savedEntity.get();
        });

        Mockito.when(emailConfigurationRepository.findByName(Mockito.anyString())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));
        Mockito.when(emailConfigurationRepository.findById(Mockito.any())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));

        Mockito.when(emailConfigurationPropertiesRepository.saveAll(Mockito.any(List.class))).thenAnswer(invocation -> {
            Iterable<EmailConfigurationsPropertyEntity> iterable = invocation.getArgument(0);
            for (EmailConfigurationsPropertyEntity entity : iterable) {
                savedProperty.set(entity);
            }
            return List.of(savedProperty.get());
        });

        EmailGlobalConfigAccessor configurationAccessor = new EmailGlobalConfigAccessor(encryptionUtility, emailConfigurationRepository, emailConfigurationPropertiesRepository);
        EmailGlobalCrudActions crudActions = new EmailGlobalCrudActions(authorizationManager, configurationAccessor, validator);
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(converter, crudActions, configurationAccessor);
        String newPassword = "updatedPassword";
        String newHost = "updated." + TEST_SMTP_HOST;
        ConfigurationModel configurationModel = createDefaultConfigurationModel();

        saveActions.createConcreteModel(configurationModel);

        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), "badport");
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_HOST_KEY.getPropertyKey(), newHost);
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), newPassword);
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), "false");

        saveActions.updateConcreteModel(configurationModel);

        // make sure the values are not the updated values
        EmailConfigurationEntity actualEntity = savedEntity.get();
        assertEquals(TEST_AUTH_PASSWORD, encryptionUtility.decrypt(actualEntity.getAuthPassword()));
        assertEquals(TEST_SMTP_HOST, actualEntity.getSmtpHost());
        assertEquals(Integer.valueOf(TEST_SMTP_PORT), actualEntity.getSmtpPort());
    }

    @Test
    void updateItemNotFoundTest() {
        AtomicReference<EmailConfigurationEntity> savedEntity = new AtomicReference<>();
        AtomicReference<EmailConfigurationsPropertyEntity> savedProperty = new AtomicReference<>();
        EmailConfigurationRepository emailConfigurationRepository = Mockito.mock(EmailConfigurationRepository.class);
        EmailConfigurationPropertiesRepository emailConfigurationPropertiesRepository = Mockito.mock(EmailConfigurationPropertiesRepository.class);
        Mockito.when(emailConfigurationRepository.save(Mockito.any(EmailConfigurationEntity.class))).thenAnswer(invocation -> {
            savedEntity.set(invocation.getArgument(0));
            return savedEntity.get();
        });

        Mockito.when(emailConfigurationRepository.findByName(Mockito.anyString())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));
        Mockito.when(emailConfigurationRepository.findById(Mockito.any())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));

        Mockito.when(emailConfigurationPropertiesRepository.saveAll(Mockito.any(List.class))).thenAnswer(invocation -> {
            Iterable<EmailConfigurationsPropertyEntity> iterable = invocation.getArgument(0);
            for (EmailConfigurationsPropertyEntity entity : iterable) {
                savedProperty.set(entity);
            }
            return List.of(savedProperty.get());
        });

        EmailGlobalConfigAccessor configurationAccessor = new EmailGlobalConfigAccessor(encryptionUtility, emailConfigurationRepository, emailConfigurationPropertiesRepository);
        EmailGlobalCrudActions crudActions = new EmailGlobalCrudActions(authorizationManager, configurationAccessor, validator);
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(converter, crudActions, configurationAccessor);
        String newPassword = "updatedPassword";
        String newHost = "updated." + TEST_SMTP_HOST;
        ConfigurationModel configurationModel = createDefaultConfigurationModel();
        saveActions.createConcreteModel(configurationModel);

        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), "badport");
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_HOST_KEY.getPropertyKey(), newHost);
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), newPassword);
        updateField(configurationModel, EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), "false");
        saveActions.updateConcreteModel(configurationModel);

        // make sure the values are not the updated values
        EmailConfigurationEntity actualEntity = savedEntity.get();
        assertEquals(TEST_AUTH_PASSWORD, encryptionUtility.decrypt(actualEntity.getAuthPassword()));
        assertEquals(TEST_SMTP_HOST, actualEntity.getSmtpHost());
        assertEquals(Integer.valueOf(TEST_SMTP_PORT), actualEntity.getSmtpPort());
    }

    @Test
    void deleteTest() {
        AtomicReference<EmailConfigurationEntity> savedEntity = new AtomicReference<>();
        AtomicReference<EmailConfigurationsPropertyEntity> savedProperty = new AtomicReference<>();
        EmailConfigurationRepository emailConfigurationRepository = Mockito.mock(EmailConfigurationRepository.class);
        EmailConfigurationPropertiesRepository emailConfigurationPropertiesRepository = Mockito.mock(EmailConfigurationPropertiesRepository.class);
        Mockito.when(emailConfigurationRepository.save(Mockito.any(EmailConfigurationEntity.class))).thenAnswer(invocation -> {
            savedEntity.set(invocation.getArgument(0));
            return savedEntity.get();
        });

        Mockito.when(emailConfigurationRepository.findByName(Mockito.anyString())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));
        Mockito.when(emailConfigurationRepository.findById(Mockito.any())).thenAnswer(invocation -> Optional.ofNullable(savedEntity.get()));
        Mockito.when(emailConfigurationRepository.existsByName(Mockito.anyString())).thenAnswer(invocation -> savedEntity.get() != null);
        Mockito.doAnswer(invocation -> {
            savedEntity.set(null);
            savedProperty.set(null);
            return null;
        }).when(emailConfigurationRepository).deleteByName(Mockito.anyString());

        Mockito.when(emailConfigurationPropertiesRepository.saveAll(Mockito.any(List.class))).thenAnswer(invocation -> {
            Iterable<EmailConfigurationsPropertyEntity> iterable = invocation.getArgument(0);
            for (EmailConfigurationsPropertyEntity entity : iterable) {
                savedProperty.set(entity);
            }
            return List.of(savedProperty.get());
        });

        EmailGlobalConfigAccessor configurationAccessor = new EmailGlobalConfigAccessor(encryptionUtility, emailConfigurationRepository, emailConfigurationPropertiesRepository);
        EmailGlobalCrudActions crudActions = new EmailGlobalCrudActions(authorizationManager, configurationAccessor, validator);
        EmailGlobalConfigurationModelSaveActions saveActions = new EmailGlobalConfigurationModelSaveActions(converter, crudActions, configurationAccessor);
        ConfigurationModel configurationModel = createDefaultConfigurationModel();
        saveActions.createConcreteModel(configurationModel);
        EmailConfigurationEntity actualEntity = savedEntity.get();
        EmailConfigurationsPropertyEntity actualPropertyEntity = savedProperty.get();
        assertNotNull(actualEntity);
        assertNotNull(actualPropertyEntity);
        saveActions.deleteConcreteModel(configurationModel);

        actualEntity = savedEntity.get();
        actualPropertyEntity = savedProperty.get();
        assertNull(actualEntity);
        assertNull(actualPropertyEntity);
    }

    private AuthorizationManager createAuthorizationManager() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        return authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
    }

    private ConfigurationModel createDefaultConfigurationModel() {
        Map<String, ConfigurationFieldModel> fieldValuesMap = new HashMap<>();

        ConfigurationFieldModel fromField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_FROM_KEY.getPropertyKey());
        ConfigurationFieldModel hostField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_HOST_KEY.getPropertyKey());
        ConfigurationFieldModel portField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey());
        ConfigurationFieldModel authField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_AUTH_KEY.getPropertyKey());
        ConfigurationFieldModel passwordField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_PASSWORD_KEY.getPropertyKey());
        ConfigurationFieldModel userField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_USER_KEY.getPropertyKey());

        ConfigurationFieldModel ehloField = ConfigurationFieldModel.create(EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey());
        fromField.setFieldValue(TEST_FROM);
        hostField.setFieldValue(TEST_SMTP_HOST);
        portField.setFieldValue(TEST_SMTP_PORT);
        authField.setFieldValue(TEST_AUTH_REQUIRED);
        passwordField.setFieldValue(TEST_AUTH_PASSWORD);
        userField.setFieldValue(TEST_AUTH_USER);
        ehloField.setFieldValue("true");
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_FROM_KEY.getPropertyKey(), fromField);
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_HOST_KEY.getPropertyKey(), hostField);
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), portField);
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_AUTH_KEY.getPropertyKey(), authField);
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_PASSWORD_KEY.getPropertyKey(), passwordField);
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_USER_KEY.getPropertyKey(), userField);
        fieldValuesMap.put(EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), ehloField);
        return new ConfigurationModel(1L, 1L, "", "", ConfigContextEnum.GLOBAL, fieldValuesMap);
    }

    private void updateField(ConfigurationModel configurationModel, String key, String value) {
        configurationModel.getField(key).ifPresent(field -> field.setFieldValue(value));
    }
}
