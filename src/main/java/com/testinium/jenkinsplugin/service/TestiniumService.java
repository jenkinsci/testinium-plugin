package com.testinium.jenkinsplugin.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.testinium.jenkinsplugin.Messages;
import com.testinium.jenkinsplugin.service.client.TestiniumAuthenticationClient;
import com.testinium.jenkinsplugin.service.client.TestiniumRestClient;
import com.testinium.jenkinsplugin.service.deserializer.DateDeserializer;
import com.testinium.jenkinsplugin.service.interceptor.TestiniumAuthenticationInterceptor;
import com.testinium.jenkinsplugin.service.interceptor.TestiniumRestRequestInterceptor;
import com.testinium.jenkinsplugin.service.model.*;
import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class TestiniumService {

    private static final Logger LOGGER = Logger.getLogger(TestiniumService.class.getName());

    private static final String AUTHENTICATION_URL = "https://account.testinium.com";
    private static final String TESTINIUM_API_URL = "https://testinium.io/Testinium.RestApi/api";
    private static final String AUTH_SCOPE = "openid";
    private static final String AUTH_GRANT_TYPE = "password";

    private TestiniumRestClient restClient;
    private TestiniumAuthenticationClient authenticationClient;
    private TestiniumRestRequestInterceptor requestInterceptor;
    private TestiniumAuthenticationInterceptor authInterceptor;

    public TestiniumService() {
        Gson gson = buildGson();

        this.requestInterceptor = new TestiniumRestRequestInterceptor();
        this.restClient = createClient(gson, this.requestInterceptor);

        this.authInterceptor =  new TestiniumAuthenticationInterceptor();
        this.authenticationClient = createAuthenticationClient(gson, this.authInterceptor);
    }

    private TestiniumAuthenticationClient createAuthenticationClient(Gson gson, TestiniumAuthenticationInterceptor interceptor) {
        return Feign.builder()
                .requestInterceptor(interceptor)
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder(gson))
                .target(TestiniumAuthenticationClient.class, AUTHENTICATION_URL);

    }

    private TestiniumRestClient createClient(Gson gson, TestiniumRestRequestInterceptor requestInterceptor) {
        return Feign.builder()
                .requestInterceptor(requestInterceptor)
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder(gson))
                .target(TestiniumRestClient.class, TESTINIUM_API_URL);


    }

    private Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new DateDeserializer());

        return builder.create();
    }


    public void authorize(String authPersonalToken,String userName, String password) throws InvalidCredentialsException {
        try {
            authInterceptor.setPersonalAuthToken(authPersonalToken);
            AuthResult authResult = authenticationClient.login(userName, password, AUTH_SCOPE, AUTH_GRANT_TYPE);
            String accessToken = authResult.getAccessToken();
            requestInterceptor.setBearerToken(accessToken);
        } catch (Exception ex) {
            throw new InvalidCredentialsException(Messages.TestiniumPlugin_CredentialsError());
        }
    }

    public List<Project> getProjects() {
        return restClient.getProjects();
    }

    public Project getProject(Integer projectId) {
        try {
            return restClient.getProject(projectId);
        } catch (Exception ex) {
            LOGGER.fine("Unable to get project with ID: " + projectId + " - " + ex.getMessage());
            return null;
        }
    }

    public RunResult startPlan(Integer planId) {
        return startPlan(planId, false);
    }

    public RunResult startPlan(Integer planId, Boolean force) {
        if (force) {
            return restClient.startPlanWithForce(planId);
        }
        return restClient.startPlanWithoutForce(planId);
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

    public Execution getExecution(Integer executionId) {
        return restClient.getTestExecution(executionId);
    }

    public TestResult getTestResults(Integer testResultId) {
        return restClient.getTestResult(testResultId);
    }
}


