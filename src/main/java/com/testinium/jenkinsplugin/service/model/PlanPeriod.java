package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class PlanPeriod implements Serializable {
    @SerializedName("period_type")
    private String periodType;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("end_date")
    private Date endDate;
    @SerializedName("days_of_week")
    private String daysOfWeek;
    @SerializedName("repeat_period")
    private Integer repeatPeriod;
}
