package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RunResult implements Serializable {
    @SerializedName("execution_id")
    private Integer executionId;
    @SerializedName("already_running")
    private Boolean alreadyRunning;
    @SerializedName("succesful")
    private Boolean successful;
}
