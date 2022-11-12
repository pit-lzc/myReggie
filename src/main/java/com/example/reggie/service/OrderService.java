package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.pojo.Orders;

import javax.servlet.http.HttpSession;


public interface OrderService extends IService<Orders> {
    public void submit(Orders orders, HttpSession session);
}
