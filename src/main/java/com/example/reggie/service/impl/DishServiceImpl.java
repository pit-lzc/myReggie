package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.DishDto;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 多表操作需要添加事务支持，Transactional，并且在启动类加注解
 */
@Transactional
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    @Lazy
    private CategoryService categoryService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto); //由于dishDto继承了dish，这句直接保存到dish表中
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId()); //由于从前端得到的数据不全，而缺少对应的dish的id，加入一句
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public Page page(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Dish> qw=new LambdaQueryWrapper<>();
        qw.like(!Strings.isEmpty(name),Dish::getName,name);

        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        super.page(pageInfo,qw);
        //这里已经查询完了,但是泛型是Dish的查询的结果集中少数据，因此要用到DishDto,缺少一个category.name，菜品分类具体名称

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //把原来的数据复制到泛型是DishDto的结果集，但是不要records,records实际上是一条条具体的数据，最终要返回一个DishDto的结果集且加上category.name属性

        List<Dish> records=pageInfo.getRecords(); //把Dish里的一条条数据分别拿出来，为加入新属性做准备
        List<DishDto> list=new ArrayList<>(); //DishDto里的records，最后注入
        for (int i = 0; i < records.size(); i++) {
            Dish dish = records.get(i); //当前的dish具体数据
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(dish,dishDto); //复制过去
            Category category = categoryService.getById(dish.getCategoryId()); //通过菜品的category.id来查询得到对应的分类对象，以便将该对象名称赋给List<DishDto>
            dishDto.setCategoryName(category.getName());
            list.add(dishDto);
        }
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }
}
