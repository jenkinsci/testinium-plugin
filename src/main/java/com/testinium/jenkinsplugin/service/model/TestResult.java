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
    @SerializedName("screenshots_enabled")
    private Boolean screenshotEnabled;
    @SerializedName("video_enabled")
    private Boolean videoEnabled;
    @SerializedName("performance_data_enabled")
    private Boolean performanceDataEnabled;
    @SerializedName("issue_tracker_key")
    private String issueTrackerKey;
    @SerializedName("test_key")
    private String testKey;
    @SerializedName("fetched_files")
    private String fetchedFiles;
    @SerializedName("retry_test_result_id")
    private Integer retryTestResultId;
    @SerializedName("scenario_id")
    private String scenarioId;
    @SerializedName("plan_id")
    private Integer planId;
    @SerializedName("project_id")
    private Integer projectId;
    @SerializedName("environment_id")
    private String environmentId;
    @SerializedName("execution_id")
    private String executionId;
    @SerializedName("video_format")
    private String videoFormat;
    @SerializedName("node_log_path")
    private String nodeLogPath;
    @SerializedName("executor_log_path")
    private String executorLogPath;
    @SerializedName("video_file_path")
    private String videoFilePath;
    @SerializedName("environment_info")
    private ResultEnvironment environment;
    @SerializedName("test_result_commands")
    private List<TestCommands> commands;
    @SerializedName("screen_resolution")
    private String screenResolution;
}

