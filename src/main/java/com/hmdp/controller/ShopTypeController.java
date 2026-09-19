package com.hmdp.controller;


import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理商铺类型查询相关的 HTTP 请求。
 * @author wdk
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;



    /**
     * 查询商铺类型列表。
     */
    @GetMapping("list")
    public Result queryTypeList() {
        List<ShopType> typeList = typeService.getTypeList();
        return Result.ok(typeList);
    }
}
