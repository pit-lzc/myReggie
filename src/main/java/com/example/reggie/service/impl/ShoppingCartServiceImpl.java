package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.pojo.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public void clean(HttpSession session) {
        LambdaQueryWrapper<ShoppingCart> qw=new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,session.getAttribute("user"));
        this.remove(qw);
    }
}
