package com.testinium.jenkinsplugin.action;

import com.testinium.jenkinsplugin.model.ExecutionResult;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.Action;
import hudson.model.Run;
import lombok.*;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
@Setter
@Builder(toBuilder = true)
@ExportedBean(defaultVisibility = 999)
@NoArgsConstructor
@AllArgsConstructor
public class TestiniumResultAction extends BaseAction implements Action, Serializable, Cloneable {

    public static final String RESULT_URL = "testiniumResult";

    private Integer actionIndex = 0;
    private Integer executionId;
    private String status;
    private ExecutionResult execution;

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private Map<String, Integer> resultSummary = new HashMap<>();

    public TestiniumResultAction(TestiniumResultAction action) {
        this.actionIndex = new Random().nextInt(100);
        this.executionId = action.executionId;
        this.status = action.status;
        this.execution = action.execution;
        this.resultSummary = new HashMap<>();
        for (Map.Entry<String, Integer> entry : action.getResultSummary().entrySet()) {
            resultSummary.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getIconFileName() {
        return jenkins.model.Jenkins.RESOURCE_PATH + "/plugin/testinium/icons/testinium-icon.png";
    }

    @Override
    public String getDisplayName() {
        return String.format("%s, ExecID: %d", this.execution.getPlan().getPlanName(), executionId);
    }

    @Override
    public String getUrlName() {
        String resultUrl = RESULT_URL;
        if (actionIndex != 0) {
            resultUrl += "-" + actionIndex;
        }
        return resultUrl;
    }

    @Override
    @SuppressFBWarnings(justification = "copy constructor")
    @SuppressWarnings({"squid:S2975","squid:S1182"})
    public TestiniumResultAction clone() {
        return new TestiniumResultAction(this);
    }

    @Restricted(NoExternalUse.class) // only used from stapler/jelly
    public Run getOwningRun() {
        StaplerRequest req = Stapler.getCurrentRequest();
        if (req == null) {
            return null;
        }
        return req.findAncestorObject(Run.class);
    }
}
