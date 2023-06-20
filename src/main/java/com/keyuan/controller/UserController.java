package com.keyuan.controller;

import com.keyuan.dto.Result;
import com.keyuan.entity.User;
import com.keyuan.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/25
 **/
@RequestMapping("/user")
@RestController
public class UserController {
    @Resource
    private IUserService userService;

    @PostMapping("/createUser")
    public Result createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @GetMapping("/getUser")
    public Result getUserByToken(@RequestParam("token") String token){
        return userService.getUserByToken(token);
    }

    @PostMapping("/updateArea")
    public Result updateAreaById(
            @RequestParam("x") Double x,
            @RequestParam("x")Double y,
            @RequestParam("id")Long id){
        return userService.updateAreaById(x,y,id);
    }

}
