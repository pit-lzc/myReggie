package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Orders;
import com.example.reggie.service.OrderService;
import com.example.reggie.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session){
        orderService.submit(orders,session);
        return R.success("完成");
    }

    @GetMapping("/userPage")
    public R<Page> list(Integer page,Integer pageSize){
        LambdaQueryWrapper<Orders> qw=new LambdaQueryWrapper<>();
        qw.orderByDesc(Orders::getOrderTime);
        Page pageInfo=new Page(page,pageSize);
        orderService.page(pageInfo,qw);
        return R.success(pageInfo);
    }
}
