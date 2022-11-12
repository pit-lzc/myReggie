package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Category;
import com.example.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize){
        Page pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> qw=new LambdaQueryWrapper<>();
        qw.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,qw);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        /*categoryService.removeById(ids);*/
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> getType(Category category){
        LambdaQueryWrapper<Category> qw=new LambdaQueryWrapper<>();
        qw.eq(category.getType()!=null,Category::getType,category.getType());
        List<Category> list = categoryService.list(qw);
        return R.success(list);
    }
}
