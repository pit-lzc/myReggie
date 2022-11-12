package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.Utils.SMSUtils;
import com.example.reggie.Utils.ValidateCodeUtils;
import com.example.reggie.common.R;
import com.example.reggie.pojo.User;
import com.example.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession) throws ExecutionException, InterruptedException {
        String phone=user.getPhone();
        if (!phone.isEmpty()) {
            String code= ValidateCodeUtils.generateValidateCode(6).toString();
            SMSUtils.sentMessage(phone,code);
            httpSession.setAttribute(phone,code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone= map.get("phone").toString();
        String code= map.get("code").toString();
        session.setAttribute(phone,"1234");
        if (code!=null&&code.equals(session.getAttribute(phone))) {
            LambdaQueryWrapper<User> qw=new LambdaQueryWrapper<>();
            qw.eq(User::getPhone,phone);
            User user = userService.getOne(qw);
            if (user==null) {
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("验证码错误");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.success("已退出登录");
    }
}
