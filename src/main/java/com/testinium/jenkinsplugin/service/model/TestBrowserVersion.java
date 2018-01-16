package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestBrowserVersion implements Serializable {
    @SerializedName("environment_id")
    private Integer environmentId;
    @SerializedName("version")
    private String version;
}
