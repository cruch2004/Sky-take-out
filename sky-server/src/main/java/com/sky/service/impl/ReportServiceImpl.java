package com.sky.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        // 每天的营业额
        List<Double> turnoverList = new ArrayList();
        dateList.forEach(date -> {
            Map todayMap = encapsulationMap(date, date, Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(todayMap);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户接口统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> totalUserList = new ArrayList();
        List<Integer> newUserList = new ArrayList<>();
        dateList.forEach(date -> {
            Map intervalMap = encapsulationMap(null, date, null);
            // 获取用户总量
            Integer totalUser = userMapper.countByMap(intervalMap);
            totalUser = totalUser == null ? 0 : totalUser;
            // 获取当天新用户数
            intervalMap.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            Integer newUser = userMapper.countByMap(intervalMap);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        });
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 订单统计接口
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList();// 每日订单数
        List<Integer> validOrderCountList = new ArrayList(); // 每日有效订单数
        dateList.forEach(date -> {
            Map todayMap = encapsulationMap(date, date, null);
            // 每日订单数
            Integer orderCount = orderMapper.countByMap(todayMap);
            orderCountList.add(orderCount);
            // 每日有效订单数
            todayMap.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(todayMap);
            validOrderCountList.add(validOrderCount);
        });

//        Map intervalMap = encapsulationMap(begin, end, null);
//        // 订单总数
//        Integer totalOrderCount = orderMapper.countByMap(intervalMap);
//        // 有效订单数
//        intervalMap.put("status", Orders.COMPLETED);
//        Integer validOrderCount = orderMapper.countByMap(intervalMap);

        // 订单完成率
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 封装日期列表
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 封装Map
     *
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Map encapsulationMap(LocalDate begin, LocalDate end, Integer status) {
        Map map = new HashMap();
        if (begin != null){
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            map.put("begin", beginTime);
        }
        if (end != null){
            LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
            map.put("end", endTime);
        }
        if (status != null) {
            map.put("status", status);
        }
        return map;
    }
}
