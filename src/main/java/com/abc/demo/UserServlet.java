package com.abc.demo;

import com.abc.tomDog.HttpServlet;
import com.abc.tomDog.annotation.WebServlet;

@WebServlet("/user")
public class UserServlet implements HttpServlet {

    public void doGet() {
        System.out.println("Get方法");
    }

    public void doPost() {
        System.out.println("Post方法");
    }
}
