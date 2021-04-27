package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


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
    private List<TestResult> testResults;
}
