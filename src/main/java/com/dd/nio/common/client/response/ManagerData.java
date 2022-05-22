package com.dd.nio.common.client.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ManagerData {

    private Long bidId;

    private Integer bidState;

    private String bidStateName;

    private List<BidShop> bids;

    private Object bizType;

    private Object bizTypeName;

    private Object comment;

    private Boolean disabled;

    private Object discountRate;

    private String districtId;

    private Object districtName;

    private Long id;

    private String implDateDefaultLabel;

    private Object implDateShowLabel;

    private Date implEndDate;

    private Date implStartDate;

    private String industryAndInstanceName;

    private String industryCode;

    private String industryName;

    private String instanceCode;

    private String instanceName;

    private Boolean isContainGoods;

    private Object mode;

    private Boolean mustSelectGoods;

    private Object netId;

    private String orgName;

    private Long projectId;

    private String protocolName;

    private String protocolNo;

    private Integer protocolType;

    private Object protocolTypeName;

    private Boolean shared;

    private Integer source;

    private Object sourceComment;

    private Integer state;

    private String stateDesc;

    private String stateIcon;

    private Long supplierCode;

    private Object supportBatchCreateItem;

    private Object supportCreateItem;

}
