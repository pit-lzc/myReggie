package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.ShoppingCart;
import com.example.reggie.pojo.User;
import com.example.reggie.service.ShoppingCartService;
import org.apache.ibatis.annotations.Delete;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session){
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> qw=new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(qw);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        //先查表内购物车内是否已经有该商品，如果已经有该商品，就将该商品的数量加一，如果没有，就加入新商品
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> qw=new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);
        qw.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        qw.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        //上面的几个qw条件：按照userId查询，保证每个人购物车不冲突  ；  如果传过来的是菜，就走第二个eq，如果传过来的是套餐，就走第三个eq
        ShoppingCart one = shoppingCartService.getOne(qw);
        if(one==null){
            shoppingCartService.save(shoppingCart);
        } else {
            one.setAmount(one.getAmount().add(one.getAmount().divide(new BigDecimal(one.getNumber()))));
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        }
        return R.success("添加成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> qw=new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);
        qw.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        qw.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(qw);
        if (one.getNumber() == 1) {
            shoppingCartService.removeById(one);
            return R.success("成功");
        }
        one.setAmount(one.getAmount().subtract(one.getAmount().divide(new BigDecimal(one.getNumber()))));
        one.setNumber(one.getNumber()-1);
        shoppingCartService.updateById(one);
        return R.success("成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session){
        shoppingCartService.clean(session);
        return R.success("成功");
    }
}
