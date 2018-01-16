package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ResultEnvironment implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("operating_system")
    private OperatingSystem operatingSystem;
    @SerializedName("devices")
    private List<TestDevice> devices;
    @SerializedName("browsers")
    private List<TestBrowser> browsers;

}
