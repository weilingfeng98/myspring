package com.aoya.learn.service;

import com.aoya.learn.spring.Autowired;
import com.aoya.learn.spring.Component;
import com.aoya.learn.spring.Scope;

/**
 * @Auther: lingfeng.wei
 * @Date: 2022/8/8 11:49
 * @Description:
 */
@Component("orderService")
@Scope("singleton")
public class OrderService {

    @Autowired
    private UserService userService;

}
