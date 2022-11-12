package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.pojo.AddressBook;
import com.example.reggie.pojo.User;
import com.example.reggie.service.AddressBookService;
import com.example.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    AddressBookService addressBookService;

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(HttpSession session){
        Long id= (Long) session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> addWrapper=new LambdaQueryWrapper<>();
        addWrapper.eq(AddressBook::getUserId,id);
        List<AddressBook> list = addressBookService.list(addWrapper);
        return R.success(list);
    }

    @PutMapping("/default")
    public R<AddressBook> setDefaultAddress(HttpSession session,@RequestBody AddressBook addressBook){
        Long userId = (Long) session.getAttribute("user");
        LambdaUpdateWrapper<AddressBook> qw=new LambdaUpdateWrapper<>();
        qw.eq(AddressBook::getUserId,userId);
        qw.set(AddressBook::getIsDefault,0);
        addressBookService.update(qw);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpSession session){
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> qw=new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId,userId);
        qw.eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(qw);
        return R.success(one);
    }

    @PostMapping
    public R<String> savaAddress(@RequestBody AddressBook addressBook,HttpSession session){
        addressBook.setUserId((Long) session.getAttribute("user"));
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }
}
