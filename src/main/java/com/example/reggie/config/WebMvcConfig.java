package com.example.reggie.config;

/*import com.example.reggie.common.JacksonObjectMapper;*/
//import com.example.reggie.common.JacksonObjectMapper;
import com.example.reggie.common.JacksonObjectMapper;
import com.example.reggie.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor interceptor=new LoginInterceptor();
        List<String> exclude=new ArrayList<>();
        exclude.add("/backend/**");
        exclude.add("/front/**");
        exclude.add("/employee/login");
        exclude.add("/employee/logout");
        exclude.add("/user/sendMsg");
        exclude.add("/user/login");
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(exclude);
    }

    /**
     * 消息转换器，将JSON数据转换成JAVA对象，或者将JAVA对象转换成JSON数据，SpringBoot中自带默认转换器，这是又添加了一个
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter); //index=0表示优先使用自己设置的转换器
    }
}

