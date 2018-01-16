package com.testinium.jenkinsplugin.action;

import jenkins.model.Jenkins;
import jenkins.model.JenkinsLocationConfiguration;

public abstract class BaseAction {
    public String getResource(String fileName) {
        @SuppressWarnings("squid:S2259")

        JenkinsLocationConfiguration jenkinsConfig = JenkinsLocationConfiguration.get();
        if (jenkinsConfig != null) {
            return jenkinsConfig.getUrl() + (Jenkins.RESOURCE_PATH + "/plugin/testinium/" + fileName).replaceFirst("/", "");
        } else {
            return "";
        }
    }
}
