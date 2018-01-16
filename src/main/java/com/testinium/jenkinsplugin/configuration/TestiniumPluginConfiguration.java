package com.testinium.jenkinsplugin.configuration;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import hudson.model.Item;
import hudson.model.ItemGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestiniumPluginConfiguration {
    private String credentialsId;
    private String dateTimeFormat;
    private String personalToken;

    public static TestiniumPluginConfiguration get(Item item) {
        TestiniumPluginConfiguration configuration = getTestiniumPluginFolderConfiguration(item);

        TestiniumPluginConfiguration testiniumGlobalConfig = TestiniumPluginGlobalConfiguration.get().getConfiguration();

        configuration.fillEmptyValues(testiniumGlobalConfig);


        String credentialsId = configuration.getCredentialsId();
        String dateFormat = configuration.getDateTimeFormat();
        String personalToken = configuration.getPersonalToken();

        if (credentialsId != null && credentialsId.isEmpty()) {
            configuration.setCredentialsId(null);
        }

        if (dateFormat != null && dateFormat.isEmpty()) {
            configuration.setDateTimeFormat(null);
        }

        if (personalToken != null && personalToken.isEmpty()) {
            configuration.setPersonalToken(null);
        }

        return configuration;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private static TestiniumPluginConfiguration getTestiniumPluginFolderConfiguration(Item item) {
        TestiniumPluginConfiguration configuration = null;
        ItemGroup parent = item.getParent();
        while (parent != null) {
            if (parent instanceof AbstractFolder) {
                configuration = getTestiniumFolderConfiguration(configuration, (AbstractFolder) parent);
            }

            if (configuration != null && !isEmpty(configuration.getCredentialsId()) && !isEmpty(configuration.getDateTimeFormat()) && !isEmpty(configuration.getPersonalToken())) {
                break;
            }

            if (parent instanceof Item) {
                parent = ((Item) parent).getParent();
            } else {
                parent = null;
            }
        }

        if (configuration == null) {
            configuration = new TestiniumPluginConfiguration();
        }
        return configuration;
    }

    private static TestiniumPluginConfiguration getTestiniumFolderConfiguration(TestiniumPluginConfiguration configuration, AbstractFolder folder) {
        TestiniumPluginFolderConfiguration testiniumFolderConfig = (TestiniumPluginFolderConfiguration) folder.getProperties().get(TestiniumPluginFolderConfiguration.class);
        if (testiniumFolderConfig != null) {
            if (configuration == null) {
                configuration = testiniumFolderConfig.getConfiguration();
            } else {
                configuration.fillEmptyValues(testiniumFolderConfig.getConfiguration());
            }
        }
        return configuration;
    }

    private void fillEmptyValues(TestiniumPluginConfiguration testiniumConfig) {
        if (testiniumConfig != null && (isEmpty(credentialsId) || isEmpty(dateTimeFormat))) {
            if (isEmpty(credentialsId) && testiniumConfig.getCredentialsId() != null && !testiniumConfig.getCredentialsId().isEmpty()) {
                this.setCredentialsId(testiniumConfig.getCredentialsId());
            }
            if (isEmpty(dateTimeFormat) && testiniumConfig.getDateTimeFormat() != null && !testiniumConfig.getDateTimeFormat().isEmpty()) {
                this.setDateTimeFormat(testiniumConfig.getDateTimeFormat());
            }
            if (isEmpty(personalToken) && testiniumConfig.getPersonalToken() != null && !testiniumConfig.getPersonalToken().isEmpty()) {
                this.setPersonalToken(testiniumConfig.getPersonalToken());
            }
        }
    }
}
