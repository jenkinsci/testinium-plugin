package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ExecutionListWrapper implements Serializable {
    @SerializedName("pagination")
    private Pagination pagination;
    @SerializedName("execution_list")
    private List<Execution> executionList;
}

