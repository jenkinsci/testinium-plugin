package com.testinium.jenkinsplugin.service.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TestiniumRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Accept", "application/json");
    }
}
