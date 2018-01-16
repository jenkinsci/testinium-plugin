package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class TestBrowser implements Serializable {

    @SerializedName("code")
    private String code;
    @SerializedName("browser_name")
    private String browserName;
    @SerializedName("versions")
    private List<TestBrowserVersion> versions;
}

