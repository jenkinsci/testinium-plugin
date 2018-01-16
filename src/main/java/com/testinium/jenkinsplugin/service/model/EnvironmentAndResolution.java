package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class EnvironmentAndResolution implements Serializable {
    @SerializedName("environment")
    private Environment environment;

    @SerializedName("resolution")
    private Map<String, String> resolution;
}