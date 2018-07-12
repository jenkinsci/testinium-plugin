package com.testinium.jenkinsplugin.service.client;

import com.testinium.jenkinsplugin.service.model.AuthResult;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface TestiniumAuthenticationClient {

    @RequestLine("POST /uaa/oauth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    AuthResult login(@Param("username") String email, @Param("access_key") String password,
                     @Param("scope") String scope, @Param("grant_type") String grantType);


}
