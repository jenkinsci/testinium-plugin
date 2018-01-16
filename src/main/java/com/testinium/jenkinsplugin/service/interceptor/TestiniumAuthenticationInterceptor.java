package com.testinium.jenkinsplugin.service.interceptor;

import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;

public class TestiniumAuthenticationInterceptor extends TestiniumRequestInterceptor {

    @Getter
    @Setter
    private String personalAuthToken;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        super.apply(requestTemplate);
        requestTemplate.header("Authorization", "Basic " + personalAuthToken);

    }

}
