package com.dd.nio.common.client.response;

import lombok.Data;

import java.util.List;

@Data
public class Category {

    private Boolean authed;

    private Boolean hasChildren;

    private Object hasQualificationRequirements;

    private Long id;

    private String name;

    private List<String> tagList;
}
