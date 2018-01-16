package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Getter
@Setter
public class Execution implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("plan_id")
    private Integer planId;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("end_date")
    private Date endDate;
    @SerializedName("test_results")
    private List<Integer> testResultIds;
    @SerializedName("result_summary")
    private HashMap<String, Integer> resultSummary;
}
