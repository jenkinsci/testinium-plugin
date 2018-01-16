package com.testinium.jenkinsplugin.service.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Pagination implements Serializable {
    @SerializedName("current_page")
    private Integer currentPage;
    @SerializedName("total_count")
    private Integer totalCount;
    @SerializedName("page_limit")
    private Integer pageLimit;
    @SerializedName("next_page")
    private String nextPage;
    @SerializedName("previous_page")
    private String previousPage;
}