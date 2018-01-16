package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Alert implements Serializable {
    @SerializedName("alert_type")
    private String type;
    @SerializedName("alert_sending_status")
    private String sendingStatus;
    @SerializedName("alert_sending_status_result")
    private String sendingStatusResult;
    @SerializedName("target")
    private String target;
    @SerializedName("plan_id")
    private String planId;
}