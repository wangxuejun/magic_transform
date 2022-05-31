package com.dd.nio.service;


import com.dd.nio.common.response.ResultData;

public interface MagicService {

    ResultData fillingGoods(Long userId);

    ResultData getWrite();

}
