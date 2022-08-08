package com.aoya.learn.service;

import com.aoya.learn.spring.MyApplicationContext;

public class Main {

    public static void main(String[] args) {

        MyApplicationContext context = new MyApplicationContext(JavaConfig.class);

        System.out.println(context.getBean("userService"));
        System.out.println(context.getBean("orderService"));
    }
}
