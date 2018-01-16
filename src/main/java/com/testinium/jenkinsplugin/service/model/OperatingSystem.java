package com.testinium.jenkinsplugin.service.model;


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OperatingSystem implements Serializable {

    @SerializedName("code")
    private String code;
    @SerializedName("platform")
    private String platform;
    @SerializedName("mobile")
    private Boolean mobile;

}
