package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Setmeal;

import java.util.List;
import java.util.Set;

public interface SetmealService extends IService<Setmeal> {
    public Page page(Integer page,Integer pageSize,String name);
    public void updateStatus(Integer state, Long[] ids);
    public boolean deleteMyself(List<Long> ids);
}
