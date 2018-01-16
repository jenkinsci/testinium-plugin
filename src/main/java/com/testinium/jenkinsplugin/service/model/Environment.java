package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Environment implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("operating_system")
    private String operatingSystem;
    @SerializedName("browser_type")
    private String browserType;
    @SerializedName("environment_type")
    private String environmentType;
    @SerializedName("environment_version")
    private String environmentVersion;
    @SerializedName("enabled")
    private Boolean enabled;
    @SerializedName("mobile")
    private Boolean mobile;
}
