package com.testinium.jenkinsplugin.pipeline;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.testinium.jenkinsplugin.Messages;
import com.testinium.jenkinsplugin.TestiniumPlugin;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import lombok.Getter;
import lombok.Setter;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Set;

@Getter
public class TestiniumStep extends Step {
    @Setter(onMethod = @__({@DataBoundSetter}))
    final Integer projectId;
    @Setter(onMethod = @__({@DataBoundSetter}))
    final Integer planId;
    @Setter(onMethod = @__({@DataBoundSetter}))
    Integer timeoutSeconds = null;
    @Setter(onMethod = @__({@DataBoundSetter}))
    Boolean failOnTimeout = false;
    @Setter(onMethod = @__({@DataBoundSetter}))
    Boolean ignoreInactive = false;
    @Setter(onMethod = @__({@DataBoundSetter}))
    Boolean abortOnError = true;
    @Setter(onMethod = @__({@DataBoundSetter}))
    Boolean abortOnFailed = true;

    @DataBoundConstructor
    public TestiniumStep(@Nonnull Integer projectId, @Nonnull Integer planId) {
        this.projectId = projectId;
        this.planId = planId;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new TestiniumPluginStepExecution(this, context);
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends StepDescriptor {

        @Inject
        private TestiniumPlugin.TestiniumPluginDescriptor delegate;

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class);
        }

        @Override
        public String getFunctionName() {
            return "testiniumExecution";
        }

        @Override
        public String getDisplayName() {
            return Messages.TestiniumStep_StartExecution();
        }


        public ListBoxModel doFillPlanIdItems(@AncestorInPath Item project,
                                              @QueryParameter Integer projectId, @QueryParameter Integer planId) {
            return delegate.doFillPlanIdItems(project, projectId, planId);
        }

        public ListBoxModel doFillProjectIdItems(@AncestorInPath Item project,
                                                 @QueryParameter Integer projectId) {
            return delegate.doFillProjectIdItems(project, projectId);
        }

        public FormValidation doCheckPlanId(@AncestorInPath Item project,
                                            @QueryParameter Integer value) {
            return delegate.doCheckPlanId(project, value);
        }


        public FormValidation doCheckProjectId(@AncestorInPath Item project,
                                               @QueryParameter Integer value) {
            return delegate.doCheckProjectId(project, value);
        }

    }

    public static class TestiniumPluginStepExecution extends SynchronousNonBlockingStepExecution {

        private static final long serialVersionUID = 1L;

        private transient TestiniumStep step;


        protected TestiniumPluginStepExecution(@Nonnull TestiniumStep step, @Nonnull StepContext context) {
            super(context);
            this.step = step;
        }

        protected Object run() throws Exception {
            Run run = getContext().get(Run.class);
            TaskListener listener = getContext().get(TaskListener.class);
            FilePath filePath = getContext().get(FilePath.class);
            Launcher launcher = getContext().get(Launcher.class);

            TestiniumPlugin plugin = new TestiniumPlugin(step);
            plugin.perform(run, filePath, launcher, listener);

            return null;
        }

        @Override
        public void onResume() { //TODO: Add resume functionality
            getContext().onFailure(new AbortException("Unable to resume execution"));
        }

        //TODO: Add ability to stop testinium executions
    }
}
