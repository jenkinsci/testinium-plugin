package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class TestDeviceModel implements Serializable {
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;

    @SerializedName("versions")
    private List<DeviceVersion> versions;

}

