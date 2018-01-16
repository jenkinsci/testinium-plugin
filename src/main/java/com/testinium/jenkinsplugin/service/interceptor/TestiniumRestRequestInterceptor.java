package com.testinium.jenkinsplugin.service.interceptor;

import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;


public class TestiniumRestRequestInterceptor extends TestiniumRequestInterceptor {

    @Setter
    @Getter
    String bearerToken;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        super.apply(requestTemplate);
        requestTemplate.header("Authorization", "Bearer " + bearerToken);

    }
}


