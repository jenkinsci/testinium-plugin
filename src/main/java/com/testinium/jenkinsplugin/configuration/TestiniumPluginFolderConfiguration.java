package com.testinium.jenkinsplugin.configuration;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;
import com.testinium.jenkinsplugin.Messages;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

@Getter
@Setter
public class TestiniumPluginFolderConfiguration extends AbstractFolderProperty<AbstractFolder<?>> {

    private String testiniumHost;
    private String testiniumClientID;
    private String credentialsId;
    private String datetimeFormat = "yyyy-MM-dd\'T\'HH:mm:ssZ";


    @DataBoundConstructor
    public TestiniumPluginFolderConfiguration(String testiniumHost, String testiniumClientID, String credentialsId, String datetimeFormat) {
        this.testiniumHost = testiniumHost;
        this.datetimeFormat = datetimeFormat;
        this.credentialsId = credentialsId;
        this.testiniumClientID = testiniumClientID;
    }

    public TestiniumPluginConfiguration getConfiguration() {
        return new TestiniumPluginConfiguration(testiniumHost, testiniumClientID, credentialsId, datetimeFormat);
    }

    @Override
    public AbstractFolderProperty<?> reconfigure(StaplerRequest request, JSONObject json)
            throws Descriptor.FormException {
        request.bindJSON(this, json);
        return this;
    }

    @Extension
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.TestiniumPluginProperty_DisplayName();
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item project,
                                                     @QueryParameter String credentialsId) {
            return TestiniumPluginGlobalConfiguration.get().doFillCredentialsIdItems(project, credentialsId);
        }

        public FormValidation doCheckDatetimeFormat(@QueryParameter String datetimeFormat) {
            return TestiniumPluginGlobalConfiguration.get().doCheckDatetimeFormat(datetimeFormat);
        }

        public FormValidation doCheckTestiniumClientID(@QueryParameter String testiniumClientID) {
            return TestiniumPluginGlobalConfiguration.get().doCheckTestiniumClientID(testiniumClientID);
        }
    }
}
