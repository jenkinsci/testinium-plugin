package com.testinium.jenkinsplugin.model;

import com.testinium.jenkinsplugin.service.model.Plan;
import com.testinium.jenkinsplugin.service.model.TestResult;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class ExecutionResult implements Serializable {

    private Integer id;
    private Plan plan;
    private Date startDate;
    private Date endDate;
    private List<TestResult> testResults;
    private HashMap<String, Integer> resultSummary;

    public ExecutionResult() {
        this.testResults = new ArrayList<>();
        this.resultSummary = new HashMap<>();
    }
}
