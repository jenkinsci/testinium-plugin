package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Project implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("project_name")
    private String projectName;
    @SerializedName("enabled")
    private Boolean enabled;
    @SerializedName("repository_path")
    private String repositoryPath;
    @SerializedName("test_framework")
    private String testFramework;
    @SerializedName("test_file_type")
    private String testFileType;
    @SerializedName("test_runner_tool")
    private String testRunnerTool;

}
