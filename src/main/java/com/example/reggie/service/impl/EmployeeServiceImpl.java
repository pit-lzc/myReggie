package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.pojo.Employee;
import com.example.reggie.service.EmployeeService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

    @Autowired
    EmployeeMapper employeeMapper;

    @Override
    public R<Employee> login(HttpServletRequest request,Employee employee) {
        //将页面的密码进行md5加密
        String password=employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> qw=new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername,employee.getUsername()); //?????????????????????????????????????????
        Employee one = employeeMapper.selectOne(qw);
        if(one==null) return R.error("用户名不存在");
        if (!one.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if(one.getStatus()==0){
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("employee",one.getId());
        return R.success(employee);
    }

    /**
     * 分页查询,这个不用写，ipage已经自己实现了，在controller层直接传入service.(page,qw)就可以
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<Employee> page(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Employee> qw=new LambdaQueryWrapper<>();
        qw.like(Strings.isNotEmpty(name),Employee::getUsername,name);
        qw.orderByDesc(Employee::getUpdateTime);
        Page<Employee> iPage=new Page<>(page,pageSize);
        return employeeMapper.selectPage(iPage,qw);
    }


}
