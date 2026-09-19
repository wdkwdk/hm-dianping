package com.hmdp.dto;

import lombok.Data;

import java.util.List;

/**
 * 封装滚动分页查询的记录和游标信息。
 * @author wdk
 */
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
