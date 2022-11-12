package com.example.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.R;
import com.example.reggie.Utils.ThreadLocalUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute("employee") != null) {
            ThreadLocalUtils.setThreadLocal((Long) request.getSession().getAttribute("employee"));
            return true;
        }
        if (request.getSession().getAttribute("user") != null) {
            ThreadLocalUtils.setThreadLocal((Long) request.getSession().getAttribute("user"));
            return true;
        }
        //每一次http请求是同一个线程，因此共享同一个线程池,把当前登录的账号的id传过去
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }
}
