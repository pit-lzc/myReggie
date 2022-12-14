package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.mapper.SetmealDishMapper;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
