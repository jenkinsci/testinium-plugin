package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TestResult implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("level")
    private String level;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("end_date")
    private Date endDate;
    @SerializedName("runtime")
    private Long runtime;
    @SerializedName("session_id")
    private String sessionId;
    @SerializedName("message")
    private String message;
    @SerializedName("video_enabled")
    private Boolean videoEnabled;
    @SerializedName("performance_data_enabled")
    private Boolean performanceDataEnabled;
    @SerializedName("scenario_id")
    private String scenarioId;
    @SerializedName(value = "plan_id", alternate = "test_plan.id")
    private Integer planId;
    @SerializedName(value = "project_id", alternate = "project.id")
    private Integer projectId;
    @SerializedName(value = "environment_id", alternate = "environment.id")
    private String environmentId;
    @SerializedName(value = "execution_id", alternate = "test_execution.id")
    private String executionId;
    @SerializedName("video_format")
    private String videoFormat;
    @SerializedName("node_log_path")
    private String nodeLogPath;
    @SerializedName("executor_log_path")
    private String executorLogPath;
}

