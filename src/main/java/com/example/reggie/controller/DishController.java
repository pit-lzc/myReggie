package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        Page dishDtoPage=dishService.page(page,pageSize,name);
        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> getOne(@PathVariable Long id){
        LambdaQueryWrapper<DishFlavor> qw=new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,id);
        Dish dish = dishService.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        List<DishFlavor> list = dishFlavorService.list(qw);
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateById(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        LambdaQueryWrapper<DishFlavor> qw=new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dishDto.getId());
        //得先把原来的删掉，然后再重新添加，不然的话新加的无法进入数据库，因为原来数据库中没有这条数据，update相当于没有执行。
        dishFlavorService.remove(qw);
        //新加进入的数据，网页没有响应给flavorsdish的Id字段，需要手动添加
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(flavors);
        return R.success("操作成功");
    }

    @PostMapping("/status/{statu}")
    public R<String> updateStatus(@PathVariable Integer statu,Long[] ids){
        List<Dish> list=new ArrayList<>();
        for(Long id:ids){
            Dish dish=new Dish();
            dish.setStatus(statu);
            dish.setId(id);
            list.add(dish);
        }
        dishService.updateBatchById(list);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids){
        for(Long id:ids){
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId){
        LambdaQueryWrapper<Dish> qw1=new LambdaQueryWrapper<>();
        qw1.eq(Dish::getCategoryId,categoryId);
        List<Dish> list = dishService.list(qw1);
        List<DishDto> dtoList = new ArrayList<>();
        for(Dish dish:list){
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Long dishId = (Long) dish.getId();
            LambdaQueryWrapper<DishFlavor> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(qw2);
            dishDto.setFlavors(flavors);
            dtoList.add(dishDto);
        }
        return R.success(dtoList);
    }
}
