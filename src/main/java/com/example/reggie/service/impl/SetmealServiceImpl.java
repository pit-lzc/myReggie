package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    @Lazy
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public Page page(Integer page, Integer pageSize, String name) {
        Page<Setmeal> pageSetmeal=new Page<>(page,pageSize);
        Page<SetmealDto> pageDto=new Page<>();
        LambdaQueryWrapper<Setmeal> qw=new LambdaQueryWrapper<>();
        qw.like(Strings.isNotEmpty(name),Setmeal::getName,name);
        super.page(pageSetmeal,qw);
        List<Setmeal> records = pageSetmeal.getRecords();
        List<SetmealDto> dtoRecords = new ArrayList<>();
        for(Setmeal setmeal:records){
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Category category=categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            dtoRecords.add(setmealDto);
        }
        pageDto.setRecords(dtoRecords);
        return pageDto;
    }

    @Override
    public void updateStatus(Integer state, Long[] ids) {
        List<Setmeal> setmeals=new ArrayList<>();
        for (Long id:ids){
            Setmeal setmeal=new Setmeal();
            setmeal.setStatus(state);
            setmeal.setId(id);
            setmeals.add(setmeal);
        }
        this.updateBatchById(setmeals);
    }

    @Override
    public boolean deleteMyself(List<Long> ids) {
        int count=0;
        for(Long id:ids){
            Setmeal setmeal = this.getById(id);
            if (setmeal.getStatus() == 1) {
                count++;
                continue;
            }
            this.removeById(id);
            LambdaQueryWrapper<SetmealDish> qw=new LambdaQueryWrapper<>();
            qw.eq(SetmealDish::getSetmealId,id);
            setmealDishService.remove(qw);
        }
        if(count!=0) return false;
        return true;
    }
}
