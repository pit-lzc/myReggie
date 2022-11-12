package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomerException;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void remove(Long id){
        //查询分类是否关联菜品，如果已经关联，则异常
        LambdaQueryWrapper<Dish> qw1=new LambdaQueryWrapper<>();
        qw1.eq(Dish::getCategoryId,id);
        long count1 = dishService.count(qw1);
        if(count1>0){
            throw new CustomerException("该分类已关联菜品，删除失败");
        }
        //是否关联套餐
        LambdaQueryWrapper<Setmeal> qw2=new LambdaQueryWrapper<>();
        qw2.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(qw2);
        if(count2>0){
            throw new CustomerException("该分类已关联套餐，删除失败");
        }
        //正常删除
        super.removeById(id);
    }
}
