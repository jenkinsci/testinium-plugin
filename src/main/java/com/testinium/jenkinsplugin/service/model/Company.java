package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Company implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("short_name")
    private String shortName;
}
