package com.dd.nio.common.client.response;

import lombok.Data;

@Data
public class BidShop {

    private String bidName;

    private String bidNo;

    private Object dealerNumCity;

    private Object dealerNumCounty;

    private Object dealerNumProvince;

    private Object discountRate;

    private Long id;

    private Object isNumLimited;

    private Object limitedNum;

    private Object paramOffset;

    private Object projectId;

    private Object purCatNodeCode;

    private Object purCatTypeName;

    private Object remark;

    private Object selectionModeName;
}
