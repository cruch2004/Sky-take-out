package com.sky.service;


import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * 数据统计相关接口服务
 */
public interface ReportService {
    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
