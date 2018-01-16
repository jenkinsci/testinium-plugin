package com.testinium.jenkinsplugin.service.model;


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class TestCommands implements Serializable {

    @SerializedName("method_type")
    private String methodType;
    @SerializedName("request_path")
    private String requestPath;
    @SerializedName("request_data")
    private String requestData;
    @SerializedName("response_data")
    private String responseData;
    @SerializedName("level")
    private String level;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("end_date")
    private Date endDate;
    @SerializedName("runtime")
    private Long runtime;
    @SerializedName("screenshot_file_path")
    private String screenshotURL;
    @SerializedName("screenshot_thumb_file_path")
    private String screenshotThumbnailURL;
    @SerializedName("runtime_as_string")
    private String runtimeAsString;
}
