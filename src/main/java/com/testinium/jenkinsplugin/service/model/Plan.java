package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Plan implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("plan_name")
    private String planName;
    @SerializedName("group_plan")
    private Boolean groupPlan;
    @SerializedName("description")
    private String description;
    @SerializedName("enabled")
    private Boolean enabled;
    @SerializedName("plan_parallel_test_limit")
    private Integer parallelTestLimit;
    @SerializedName("scenarios")
    private List<Integer> scenarioIds;
    @SerializedName("period")
    private PlanPeriod period;
    @SerializedName("alerts")
    private List<Alert> alerts;
    @SerializedName("project_id")
    private Integer projectId;
    @SerializedName("failed_test_retry_count")
    private Integer failedRetryCount;
    @SerializedName("screen_shot_type")
    private Boolean screenshotEnabled;
    @SerializedName("video_enabled")
    private Boolean videoEnabled;
    @SerializedName("environment_resolutions")
    private List<EnvironmentAndResolution> environmentAndResolutions;
    @SerializedName("max_step_duration")
    private Integer maxStepDuration;
    @SerializedName("performance_data_enabled")
    private Boolean performanceDataEnabled;


}
