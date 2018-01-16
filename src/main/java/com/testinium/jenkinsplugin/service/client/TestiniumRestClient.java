package com.testinium.jenkinsplugin.service.client;

import com.testinium.jenkinsplugin.service.model.*;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface TestiniumRestClient {

    @RequestLine("GET /projects/")
    List<Project> getProjects();

    @RequestLine("GET /projects/{projectid}")
    Project getProject(@Param("projectid") Integer projectId);

    @RequestLine("GET /projects/{projectid}/plans")
    List<Plan> getProjectPlans(@Param("projectid") Integer projectId);

    @RequestLine("GET /plans/{planid}")
    Plan getPlan(@Param("planid") Integer planId);

    @RequestLine("GET /plans/{planid}/run/false")
    RunResult startPlanWithoutForce(@Param("planid") Integer planid);

    @RequestLine("GET /plans/{planid}/run")
    RunResult startPlanWithForce(@Param("planid") Integer planid);

    @RequestLine("GET /plans/{planid}/executions")
    List<Execution> getTestExecutionsForPlan(@Param("planid") Integer planid);

    @RequestLine("GET /executions/{executionid}")
    Execution getTestExecution(@Param("executionid") Integer executionId);

    @RequestLine("GET /results/{resultId}")
    TestResult getTestResult(@Param("resultId") Integer resultId);

}
