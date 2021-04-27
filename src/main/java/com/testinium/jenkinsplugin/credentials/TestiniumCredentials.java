

package com.testinium.jenkinsplugin.credentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.CheckForNull;

@Getter
@Setter
public class TestiniumCredentials extends BaseStandardCredentials implements StandardUsernamePasswordCredentials {

    @NonNull
    @Setter(onMethod = @__({@DataBoundSetter}))
    private String username;

    @NonNull
    @Setter(onMethod = @__({@DataBoundSetter}))
    private Secret accessKey;

    private final String authKey = "dGVzdGluaXVtQWNjZXNzS2V5Q2xpZW50OnRlc3Rpbml1bUFjY2Vzc0tleUNsaWVudFNlY3JldA==";

    @DataBoundConstructor
    public TestiniumCredentials(@CheckForNull CredentialsScope scope, @CheckForNull String id, @NonNull String userName, @NonNull String accessKey, @CheckForNull String description) {
        super(scope, id, description);
        this.username = userName;
        this.accessKey = Secret.fromString(accessKey);
    }

    @Override
    public Secret getPassword() {
        return this.getAccessKey();
    }


    @Extension
    public static class TestiniumCredentialDescriptor extends CredentialsDescriptor {

        public TestiniumCredentialDescriptor(Class<? extends Credentials> clazz) {
            super(clazz);
            load();
        }

        public TestiniumCredentialDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Testinium";
        }


        @SuppressWarnings("unused") // used by stapler
        public FormValidation doCheckAccessKey(@QueryParameter("accessKey") String accessKey, @QueryParameter("userName") String userName) {
            // If unauthorized getUser returns an empty string.
            if (accessKey == null || accessKey.isEmpty() || userName == null || userName.isEmpty()) {
                return FormValidation.error("Username and Access key should be non empty");
            }
            return FormValidation.ok();
        }


        @Override
        public String getIconClassName() {
            return "testinium-icon";
        }

        static {
            IconSet.icons.addIcon(new Icon("testinium-icon icon-sm", "testinium/icons/size/16x16.png", Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon("testinium-icon icon-md", "testinium/icons/size/24x24.png", Icon.ICON_MEDIUM_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon("testinium-icon icon-lg", "testinium/icons/size/32x32.png", Icon.ICON_LARGE_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon("testinium-icon icon-xlg", "testinium/icons/size/48x48.png", Icon.ICON_XLARGE_STYLE, IconType.PLUGIN));

        }

    }
}
