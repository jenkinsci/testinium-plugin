package com.testinium.jenkinsplugin.configuration;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.testinium.jenkinsplugin.Messages;
import com.testinium.jenkinsplugin.credentials.TestiniumCredentials;
import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Extension
public class TestiniumPluginGlobalConfiguration extends GlobalConfiguration {


    private String credentialsId;
    private String datetimeFormat = "yyyy-MM-dd\'T\'HH:mm:ssZ";
    private String personalToken;

    public TestiniumPluginGlobalConfiguration() {
        load();
    }

    public TestiniumPluginGlobalConfiguration(String credentialsId, String datetimeFormat, String personalToken) {
        this.datetimeFormat = datetimeFormat;
        this.credentialsId = credentialsId;
        this.personalToken = personalToken;
        load();
    }

    public static TestiniumPluginGlobalConfiguration get() {
        return GlobalConfiguration.all().get(TestiniumPluginGlobalConfiguration.class);
    }

    public TestiniumPluginConfiguration getConfiguration() {
        return new TestiniumPluginConfiguration(credentialsId, datetimeFormat, personalToken);
    }


    @Override
    public String getDisplayName() {
        return Messages.TestiniumPluginProperty_DisplayName();
    }

    @Override
    public boolean configure(final StaplerRequest request, final JSONObject json)
            throws FormException {
        request.bindJSON(this, json);
        save();
        return true;
    }

    public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item project,
                                                 @QueryParameter String credentialsId) {
        if (project == null && !Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER) ||
                project != null && !project.hasPermission(Item.EXTENDED_READ)) {
            return new StandardListBoxModel().includeCurrentValue(credentialsId);
        }

        Item currentProject = project;

        if (project == null) {
            //Construct a fake project
            currentProject = new FreeStyleProject((ItemGroup) Jenkins.getInstance(), "fake-" + UUID.randomUUID().toString());
        }

        return new StandardListBoxModel()
                .includeEmptyValue()
                .includeMatchingAs(
                        currentProject instanceof Queue.Task
                                ? Tasks.getAuthenticationOf((Queue.Task) currentProject)
                                : ACL.SYSTEM,
                        currentProject,
                        TestiniumCredentials.class,
                        Collections.<DomainRequirement>emptyList(),
                        CredentialsMatchers.instanceOf(TestiniumCredentials.class))
                .includeCurrentValue(credentialsId);
    }

    public FormValidation doCheckDatetimeFormat(@QueryParameter String datetimeFormat) {
        try {
            new SimpleDateFormat(datetimeFormat).format(new Date());
        } catch (Exception ex) {
            return FormValidation.error("Invalid format");
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckPersonalToken(@QueryParameter String personalToken) {

        if(personalToken == null || personalToken.isEmpty()){
            return FormValidation.error("Personal token cannot be empty");
        }
        return FormValidation.ok();
    }
}
