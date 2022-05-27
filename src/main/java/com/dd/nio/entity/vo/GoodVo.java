package com.dd.nio.entity.vo;

import com.dd.nio.entity.Good;
import com.dd.nio.entity.GoodAttribute;
import lombok.Data;

import java.util.List;

@Data
public class GoodVo {

    private Good good;

    private List<GoodAttribute> attributes;
}
