package com.example.reggie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.ShoppingCart;

import javax.servlet.http.HttpSession;

public interface ShoppingCartService extends IService<ShoppingCart> {
    public void clean(HttpSession session);

}
