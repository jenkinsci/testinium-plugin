package com.testinium.jenkinsplugin.service.client;

import com.testinium.jenkinsplugin.service.model.*;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface TestiniumRestClient {

    @RequestLine("GET /users/companies/")
    List<Company> getCompanies();

    @RequestLine("GET /companies/{companyId}")
    Company getCompany(@Param("companyId") Integer companyId);

    @RequestLine("GET /projects/company/{companyId}")
    List<Project> getProjects(@Param("companyId") Integer companyId);

    @RequestLine("GET /projects/{projectid}/active")
    Project getProject(@Param("projectid") Integer projectId);

    @RequestLine("GET /projects/{projectid}/plans/active")
    List<Plan> getProjectPlans(@Param("projectid") Integer projectId);

    @RequestLine("GET /plans/{planid}/active")
    Plan getPlan(@Param("planid") Integer planId);

    @RequestLine("GET /plans/{planid}/run/company/{companyid}")
    RunResult startPlanWithoutForce(@Param("planid") Integer planid, @Param("companyid") Integer companyId);

    @RequestLine("GET /plans/{planid}/run")
    RunResult startPlanWithForce(@Param("planid") Integer planid);

    @RequestLine("GET /plans/{planid}/testExecutions")
    List<Execution> getTestExecutionsForPlan(@Param("planid") Integer planid);

    @RequestLine("GET /testExecutions/{executionid}/company/{companyid}")
    Execution getTestExecution(@Param("executionid") Integer executionId, @Param("companyid") Integer companyId);

    @RequestLine("GET /testResults/{resultId}")
    TestResult getTestResult(@Param("resultId") Integer resultId);
}
