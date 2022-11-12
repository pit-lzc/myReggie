package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        return R.success(setmealService.page(page,pageSize,name));
    }

    @PostMapping
    @Transactional
    public R<String> save(@RequestBody SetmealDto setmealDto){
        BigDecimal price=new BigDecimal(0);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDto.setPrice(price);
        setmealService.save(setmealDto);
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
        return R.success("保存成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getOne(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> qw=new LambdaQueryWrapper();
        qw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(qw);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> qw=new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(qw);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });
        setmealDishService.saveBatch(setmealDishes);
        return R.success("修改完成");
    }

    @PostMapping("/status/{state}")
    @Transactional
    public R<String> updateStatus(@PathVariable Integer state,Long[] ids){
        setmealService.updateStatus(state,ids);
        return R.success("修改成功");
    }

    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam List<Long> ids){
        if(setmealService.deleteMyself(ids)) return R.success("删除成功");
        return R.error("部分套餐正在售卖，删除失败");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> qw=new LambdaQueryWrapper<>();
        qw.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        qw.eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(qw);
        return R.success(list);
    }
}
