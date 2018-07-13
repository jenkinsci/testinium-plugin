package com.testinium.jenkinsplugin;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.testinium.jenkinsplugin.action.TestiniumResultAction;
import com.testinium.jenkinsplugin.configuration.TestiniumPluginConfiguration;
import com.testinium.jenkinsplugin.exception.TimeoutException;
import com.testinium.jenkinsplugin.model.ExecutionResult;
import com.testinium.jenkinsplugin.pipeline.TestiniumStep;
import com.testinium.jenkinsplugin.service.TestiniumService;
import com.testinium.jenkinsplugin.service.model.*;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
@Setter
public class TestiniumPlugin extends Builder implements SimpleBuildStep {

    public static final int MAX_EXECUTION_RETRY_COUNT = 3;
    public static final String PROJECT = "project";
    private static final Object ADD_ACTION_LOCK = new Object() {
    };
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Map<String, String> titleTextStore = new HashMap<>();

    @Setter(onMethod = @__({@DataBoundSetter}))
    Integer projectId;
    @Setter(onMethod = @__({@DataBoundSetter}))
    Integer planId;
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
    public TestiniumPlugin(@Nonnull  Integer projectId, @Nonnull  Integer planId) {
        this.projectId = projectId;
        this.planId = planId;
    }

    public TestiniumPlugin(TestiniumStep step) {
        this.projectId = step.getProjectId();
        this.planId = step.getPlanId();
        this.timeoutSeconds = step.getTimeoutSeconds();
        this.failOnTimeout = step.getFailOnTimeout();
        this.ignoreInactive = step.getIgnoreInactive();
        this.abortOnError = step.getAbortOnError();
        this.abortOnFailed = step.getAbortOnFailed();
    }

    private static TestiniumService prepareService(Run run) throws AbortException {
        TestiniumPluginConfiguration properties = TestiniumPluginConfiguration.get(run.getParent());
        String credentialsId = properties.getCredentialsId();
        if (credentialsId == null) {
            throw new AbortException(Messages.TestiniumPlugin_InvalidCredentials());
        }
        return prepareService(properties.getPersonalToken(), getCredential(run, credentialsId));
    }

    private static TestiniumService prepareService(String personalToken, UsernamePasswordCredentials credentials) throws AbortException {
        try {
            if (credentials == null) {
                throw new InvalidCredentialsException(Messages.TestiniumPlugin_CredentialsError());
            }

            String username = credentials.getUsername();
            String password = credentials.getPassword().getPlainText();

            TestiniumService testiniumService = new TestiniumService();
            testiniumService.authorize(personalToken, username, password);

            return testiniumService;

        } catch (InvalidCredentialsException ex) {
            throw new AbortException(Messages.TestiniumPlugin_CredentialsException());
        }
    }

    private static UsernamePasswordCredentials getCredential(Run run, String credentialsId) {
        return CredentialsProvider.findCredentialById(
                credentialsId,
                StandardUsernamePasswordCredentials.class,
                run,
                Collections.<DomainRequirement>emptyList()
        );
    }

    private static synchronized String getStatusTitle(String statusText) {

        if (titleTextStore == null) {
            titleTextStore = new HashMap<>();
        }

        if (titleTextStore.containsKey(statusText)) {
            return titleTextStore.get(statusText);
        }

        String title;
        try {
            String methodName = String.format("ResultStatus_%s", statusText);
            Method method = Messages.class.getMethod(methodName);
            title = (String) method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            title = statusText;
        }

        titleTextStore.put(statusText, title);
        return title;

    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {

        PrintStream logger = listener.getLogger();

        listener.getLogger().println("Started");
        TestiniumService testiniumService = prepareService(run);

        Project project = testiniumService.getProject(projectId);
        if (!isValidProject(logger, project)) {
            return;
        }

        Plan plan = testiniumService.getPlan(planId);
        if (!isValidPlan(logger, plan)) {
            return;
        }

        logger.println(Messages.TestiniumPlugin_StartingMessage(project.getProjectName(), plan.getPlanName()));

        RunResult runResult = startExecution(testiniumService, plan);
        logger.println(Messages.TestiniumPlugin_StartedMessage(runResult.getExecutionId().toString()));

        TestiniumResultAction action = createTestiniumResultAction(runResult);
        addAction(run, action);

        ExecutionResult execResult = null;
        try {
            execResult = waitForExecutionToComplete(run, logger, testiniumService, action);
        } catch (TimeoutException e) {
            handleTimeout(logger, testiniumService, plan, action);
            run.save();
            return;
        }

        initializeActionResultSummary(action, execResult);
        printResultSummary(action, logger, execResult.getResultSummary());
        checkFailureConditions(logger, execResult.getResultSummary());

        action.setStatus("");

        run.save();
    }

    private boolean isValidProject(PrintStream logger, Project project) throws AbortException {
        if (project == null) {
            if (projectId == null) {
                projectId = 0;
            }
            throw new AbortException(Messages.TestiniumPlugin_UnableToFind(PROJECT, projectId.toString()));
        }
        if (!project.getEnabled()) {
            logger.println(Messages.TestiniumPlugin_SelectedNotActive(PROJECT));
        }

        return project.getEnabled();
    }

    private boolean isValidPlan(PrintStream logger, Plan plan) throws AbortException {
        if (plan == null) {
            if (planId == null) {
                planId = 0;
            }
            throw new AbortException(Messages.TestiniumPlugin_UnableToFind("plan", planId.toString()));
        }

        if (!plan.getProjectId().equals(projectId)) {
            throw new AbortException(Messages.TestiniumPlugin_PlanProjectDoesntMatch(planId.toString(), projectId.toString()));
        }

        if (!plan.getEnabled()) {
            String message = Messages.TestiniumPlugin_SelectedNotActive("plan");
            if (ignoreInactive) {
                logger.print(Messages.TestiniumPlugin_Ignoring());
                logger.println(message);
                return false;
            } else {
                throw new AbortException(message);
            }
        }
        return true;
    }

    private void addAction(Run run, TestiniumResultAction newAction) throws IOException {
        synchronized (ADD_ACTION_LOCK) {
            List<TestiniumResultAction> runActions = run.getActions(TestiniumResultAction.class);
            int newIndex = 0;
            for (TestiniumResultAction action : runActions) {
                if (action.getActionIndex() >= newIndex) {
                    newIndex = action.getActionIndex() + 1;
                }
            }

            newAction.setActionIndex(newIndex);

            run.addAction(newAction);
            run.save();
        }
    }

    private RunResult startExecution(TestiniumService testiniumService, Plan plan) throws AbortException {

        RunResult runResult = null;
        for (int i = 0; i < MAX_EXECUTION_RETRY_COUNT; i++) {
            runResult = testiniumService.startPlan(planId);
            if (runResult != null && runResult.getSuccessful()) {
                break;
            }
        }

        if (runResult == null || !runResult.getSuccessful()) {
            throw new AbortException(Messages.TestiniumPlugin_UnableToStartExecution(plan.getPlanName()));
        }
        return runResult;
    }

    private ExecutionResult waitForExecutionToComplete(
            @Nonnull Run<?, ?> run,
            PrintStream logger,
            TestiniumService testiniumService,
            TestiniumResultAction action) throws InterruptedException, IOException, TimeoutException {

        Integer executionId = action.getExecutionId();
        logger.println(Messages.TestiniumPlugin_WaitingToComplete(executionId.toString()));

        ExecutionResult executionResult = initializeExecutionResult(testiniumService);
        action.setExecution(executionResult);


        Boolean finished = false;

        while (!finished) {
            Execution execution = testiniumService.getExecution(executionId);

            if (execution.getEndDate() != null) {
                finished = true;
            }

            if (isTimeout(execution.getStartDate())) {
                throw new TimeoutException();
            }

            setExecutionResultValues(testiniumService, executionResult, execution);

            run.save();

            if (!finished) {
                Thread.sleep(2000);
            }
        }

        return executionResult;
    }

    private void handleTimeout(PrintStream logger, TestiniumService testiniumService, Plan plan, TestiniumResultAction action) throws InterruptedException, AbortException {
        //TODO: cancel execution

        Thread.sleep(1000);

        Execution execution = testiniumService.getExecution(action.getExecutionId());
        ExecutionResult results = action.getExecution();
        setExecutionResultValues(testiniumService, results, execution);

        String message = String.format("Timeout reached with plan '%s' and Execution ID: %d  ", plan.getPlanName(), action.getExecutionId());
        if (failOnTimeout) {
            throw new AbortException(message);
        } else {
            logger.println(message);
        }
    }

    private TestiniumResultAction createTestiniumResultAction(RunResult runResult) {
        return TestiniumResultAction.builder()
                .executionId(runResult.getExecutionId())
                .status("Running")
                .build();
    }

    private ExecutionResult initializeExecutionResult(TestiniumService testiniumService) {
        ExecutionResult executionResult = new ExecutionResult();
        Plan plan = testiniumService.getPlan(planId);
        executionResult.setPlan(plan);
        return executionResult;
    }

    private void setExecutionResultValues(TestiniumService testiniumService, ExecutionResult executionResult, Execution execution) {
        executionResult.setId(execution.getId());
        executionResult.setStartDate(execution.getStartDate());
        executionResult.setEndDate(execution.getEndDate());
        executionResult.setResultSummary(execution.getResultSummary());

        List<TestResult> currentResults = new ArrayList<>();
        if (execution.getTestResultIds() != null) {
            for (Integer resultId : execution.getTestResultIds()) {
                TestResult result = testiniumService.getTestResults(resultId);
                currentResults.add(result);
            }
        }
        executionResult.setTestResults(currentResults);
    }

    private void checkFailureConditions(PrintStream logger, Map<String, Integer> resultSummary) throws AbortException {
        if (resultSummary.containsKey("FAILURE") && resultSummary.get("FAILURE") > 0) {
            String failedMessage = Messages.TestiniumPlugin_HasFailedTestsMessage();
            if (abortOnFailed) {
                throw new AbortException(failedMessage + Messages.TestiniumPlugin_RaisingError());
            } else {
                logger.println("ERROR: " + failedMessage);
            }
        }

        if (resultSummary.containsKey("ERROR") && resultSummary.get("ERROR") > 0) {
            String hasErrorMessage = Messages.TestiniumPlugin_HasErrorTestsMessage();
            if (abortOnError) {
                throw new AbortException(hasErrorMessage + Messages.TestiniumPlugin_RaisingError());
            } else {
                logger.println("ERROR: " + hasErrorMessage);
            }
        }
    }

    private void printResultSummary(TestiniumResultAction action, PrintStream logger, Map<String, Integer> resultSummary) {
        logger.println(Messages.TestiniumPlugin_StatusMessageTitle(action.getExecutionId().toString()));

        for (Map.Entry<String, Integer> entry : resultSummary.entrySet()) {
            String title = getStatusTitle(entry.getKey());
            Integer count = entry.getValue();
            logger.println(Messages.TestiniumPlugin_StatusMessage(title, count));
        }
    }

    private void initializeActionResultSummary(TestiniumResultAction action, ExecutionResult execResult) {
        HashMap<String, Integer> resultSummary = execResult.getResultSummary();
        for (Map.Entry<String, Integer> entry : resultSummary.entrySet()) {
            String title = getStatusTitle(entry.getKey());
            Integer count = entry.getValue();
            action.getResultSummary().put(title, count);
        }
    }

    boolean isTimeout(Date startDate) {
        if (timeoutSeconds == null || timeoutSeconds == 0) {
            return false;
        }
        long elapsedTime = (new Date().getTime() - startDate.getTime()) / 1000;
        return elapsedTime > timeoutSeconds;
    }

    @Override
    public TestiniumPluginDescriptor getDescriptor() {
        return (TestiniumPluginDescriptor) super.getDescriptor();
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class TestiniumPluginDescriptor extends BuildStepDescriptor<Builder> {

        public TestiniumPluginDescriptor() {
            load();
        }

        private static TestiniumService prepareService(Item project) {
            TestiniumPluginConfiguration properties = TestiniumPluginConfiguration.get(project);
            String credentialsId = properties.getCredentialsId();
            if (credentialsId == null) {
                return null;
            }
            UsernamePasswordCredentials credentials = getCredential(project, credentialsId);
            TestiniumService testiniumService;
            try {
                testiniumService = TestiniumPlugin.prepareService(properties.getPersonalToken(), credentials);
            } catch (Exception ex) {
                return null;
            }
            return testiniumService;
        }

        private static UsernamePasswordCredentials getCredential(Item project, String credentialsId) {
            List<UsernamePasswordCredentialsImpl> credentialsList = CredentialsProvider.lookupCredentials(
                    UsernamePasswordCredentialsImpl.class,
                    project,
                    ACL.SYSTEM,
                    Collections.<DomainRequirement>emptyList());

            for (UsernamePasswordCredentialsImpl credentials : credentialsList) {
                if (credentials.getId().equals(credentialsId))
                    return credentials;
            }
            return null;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.TestiniumPlugin_DisplayName();
        }

        public ListBoxModel doFillProjectIdItems(@AncestorInPath Item project,
                                                 @QueryParameter Integer projectId) {

            ListBoxModel m = new ListBoxModel();

            TestiniumService testiniumService = prepareService(project);
            if (testiniumService != null) {
                addEmptyValue(m);
                List<Project> projectList = testiniumService.getProjects();

                for (Project p : projectList) {

                    if (!p.getEnabled()) {
                        continue;
                    }
                    m.add(new ListBoxModel.Option(p.getProjectName(), p.getId().toString(), p.getId().equals(projectId)));
                }
            }

            return m;
        }

        public ListBoxModel doFillPlanIdItems(@AncestorInPath Item project,
                                              @QueryParameter Integer projectId,
                                              @QueryParameter Integer planId) {
            ListBoxModel m = new ListBoxModel();

            TestiniumService testiniumService = prepareService(project);
            if (testiniumService != null) {

                addEmptyValue(m);

                if (projectId != null) {
                    List<Plan> planList = testiniumService.getPlans(projectId);

                    for (Plan p : planList) {
                        String planName = p.getPlanName();
                        if (!p.getEnabled()) {
                            planName += " - " + Messages.TestiniumPlugin_Disabled();
                        }

                        m.add(new ListBoxModel.Option(planName, p.getId().toString(), p.getId().equals(planId)));

                    }
                }

            }
            return m;
        }

        public FormValidation doCheckProjectId(@AncestorInPath Item project,
                                               @QueryParameter Integer value) {
            TestiniumService testiniumService = prepareService(project);
            if (testiniumService == null) {
                return FormValidation.error(Messages.TestiniumPluginValidation_AuthError());
            }
            if (value == null) {
                return FormValidation.error(Messages.TestiniumPluginValidation_MustSelectProject());
            }

            Project testiniumProject = testiniumService.getProject(value);

            if (testiniumProject == null) {
                return FormValidation.error(Messages.TestiniumPlugin_UnableToFind(PROJECT, value.toString()));
            }

            return FormValidation.ok();
        }


        public FormValidation doCheckPlanId(@AncestorInPath Item project,
                                            @QueryParameter Integer value) {
            TestiniumService testiniumService = prepareService(project);
            if (testiniumService == null) {
                return FormValidation.error(Messages.TestiniumPluginValidation_AuthError());
            }

            if (value == null) {
                return FormValidation.error(Messages.TestiniumPluginValidation_MustSelectPlan());
            }

            Plan testiniumPlan = testiniumService.getPlan(value);
            if (testiniumPlan == null) {
                return FormValidation.error(Messages.TestiniumPlugin_UnableToFind("plan", value.toString()));
            }

            return FormValidation.ok();
        }


        private void addEmptyValue(ListBoxModel m) {
            m.add("-", null);
        }
    }

}
