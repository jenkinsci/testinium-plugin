package com.testinium.jenkinsplugin.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.testinium.jenkinsplugin.Messages;
import com.testinium.jenkinsplugin.service.client.TestiniumAuthenticationClient;
import com.testinium.jenkinsplugin.service.client.TestiniumRestClient;
import com.testinium.jenkinsplugin.service.deserializer.DateDeserializer;
import com.testinium.jenkinsplugin.service.interceptor.TestiniumAuthenticationInterceptor;
import com.testinium.jenkinsplugin.service.interceptor.TestiniumRestRequestInterceptor;
import com.testinium.jenkinsplugin.service.model.AuthResult;
import com.testinium.jenkinsplugin.service.model.Company;
import com.testinium.jenkinsplugin.service.model.Execution;
import com.testinium.jenkinsplugin.service.model.Plan;
import com.testinium.jenkinsplugin.service.model.Project;
import com.testinium.jenkinsplugin.service.model.RunResult;
import com.testinium.jenkinsplugin.service.model.TestResult;
import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;

public class TestiniumService {

    private static final Logger LOGGER = Logger.getLogger(TestiniumService.class.getName());


    private static final String AUTHENTICATION_URL = "/Testinium.RestApi/oauth";
    private static final String TESTINIUM_API_URL = "/Testinium.RestApi/api";
    private static final String AUTH_SCOPE = "api";
    private static final String AUTH_GRANT_TYPE = "password";

    private final String testiniumHost;

    private TestiniumRestClient restClient;
    private TestiniumAuthenticationClient authenticationClient;
    private TestiniumRestRequestInterceptor requestInterceptor;
    private TestiniumAuthenticationInterceptor authInterceptor;

    public TestiniumService(String testiniumHost) {
        Gson gson = buildGson();

        this.testiniumHost = testiniumHost;

        this.requestInterceptor = new TestiniumRestRequestInterceptor();
        this.restClient = createClient(testiniumHost, gson, this.requestInterceptor);

        this.authInterceptor = new TestiniumAuthenticationInterceptor();
        this.authenticationClient = createAuthenticationClient(testiniumHost, gson, this.authInterceptor);
    }

    private TestiniumAuthenticationClient createAuthenticationClient(String testiniumHost, Gson gson, TestiniumAuthenticationInterceptor interceptor) {
        String authenticationURL = testiniumHost + AUTHENTICATION_URL;
        return Feign.builder()
                .requestInterceptor(interceptor)
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder(gson))
                .target(TestiniumAuthenticationClient.class, authenticationURL);

    }

    private TestiniumRestClient createClient(String testiniumHost, Gson gson, TestiniumRestRequestInterceptor requestInterceptor) {
        String apiURL = testiniumHost + TESTINIUM_API_URL;
        return Feign.builder()
                .requestInterceptor(requestInterceptor)
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder(gson))
                .target(TestiniumRestClient.class, apiURL);
    }

    private Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateDeserializer());
        return builder.create();
    }


    public void authorize(String authPersonalToken, String userName, String password) throws InvalidCredentialsException {
        try {
            authInterceptor.setPersonalAuthToken(authPersonalToken);
            AuthResult authResult = authenticationClient.login(userName, password, AUTH_SCOPE, AUTH_GRANT_TYPE);
            String accessToken = authResult.getAccessToken();
            requestInterceptor.setBearerToken(accessToken);
        } catch (Exception ex) {
            throw new InvalidCredentialsException(Messages.TestiniumPlugin_CredentialsError());
        }
    }

    public List<Company> getCompanies() {
        return restClient.getCompanies();
    }

    public Company getCompany(Integer companyId) {
        try {
            return restClient.getCompany(companyId);
        } catch (Exception ex) {
            LOGGER.fine("Unable to get company with ID: " + companyId + " - " + ex.getMessage());
            return null;
        }
    }

    public List<Project> getProjects(Integer companyId) {
        return restClient.getProjects(companyId);
    }

    public Project getProject(Integer projectId) {
        try {
            return restClient.getProject(projectId);
        } catch (Exception ex) {
            LOGGER.fine("Unable to get project with ID: " + projectId + " - " + ex.getMessage());
            return null;
        }
    }

    public RunResult startPlan(Integer planId, Integer companyId) {
        return restClient.startPlanWithoutForce(planId, companyId);
    }

    public List<Plan> getPlans(Integer projectId) {
        try {
            return restClient.getProjectPlans(projectId);
        } catch (Exception ex) {
            LOGGER.fine("Unable to get plan with ID: " + projectId + " - " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    public Plan getPlan(Integer planId) {
        try {
            return restClient.getPlan(planId);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Execution> getTestExecutions(Integer planId) {
        return restClient.getTestExecutionsForPlan(planId);
    }

    public Execution getExecution(Integer executionId, Integer companyId) {
        return restClient.getTestExecution(executionId, companyId);
    }

    public TestResult getTestResults(Integer testResultId) {
        return restClient.getTestResult(testResultId);
    }
}


