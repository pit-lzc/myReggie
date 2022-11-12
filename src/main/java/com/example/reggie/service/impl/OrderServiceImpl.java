package com.example.reggie.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.mapper.OrderMapper;
import com.example.reggie.pojo.*;
import com.example.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    UserService userService;

    @Autowired
    AddressBookService addressBookService;

    @Autowired
    OrderDetailService orderDetailService;

    @Override
    public void submit(Orders orders, HttpSession session) {
        LocalDateTime now = LocalDateTime.now();
        orders.setOrderTime(now);
        orders.setCheckoutTime(now);

        //查询当前用户id
        Long userId = (Long) session.getAttribute("user");
        User user = userService.getById(userId);
        orders.setUserId(userId);

        //查询地址中填的地址,以及该地址关联的手机号
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartWrapper);

        //向订单表插入数据
        Long id = IdWorker.getId();
        orders.setNumber(id.toString()); //订单号
        orders.setUserName(user.getName());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getCityName());
        orders.setConsignee(addressBook.getConsignee());
        BigDecimal amount = new BigDecimal(0); //总价格
        for(ShoppingCart shoppingCart : shoppingCarts) {
            amount = amount.add(shoppingCart.getAmount());
        }
        orders.setAmount(amount);
        super.save(orders);

        //向订单明细表插入数据
        for(ShoppingCart shoppingCart:shoppingCarts){
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetailService.save(orderDetail);
        }

        //清空购物车
        shoppingCartService.clean(session);

    }
}
