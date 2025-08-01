package com.sky.vo;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class OrderStatisticsVO implements Serializable {
    //待接单数量
    private Integer toBeConfirmed;
    //待派送数量
    private Integer confirmed;
    //派送中数量
    private Integer deliveryInProgress;
}
