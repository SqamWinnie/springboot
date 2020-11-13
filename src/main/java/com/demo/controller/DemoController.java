package com.demo.controller;

import com.demo.dto.Demo;
import com.sample.conn.mail.SendMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author winnie
 * @date 2020/10/30
 */
@Slf4j
@RestController
public class DemoController {

    @GetMapping("/get/param")
    public String getParam(@RequestParam("param") String param) {
        log.info("====================/get/param==================================");
        log.info("get: " + param);
        return "{persons:[{name:'艾米一',id:1},{name:'Bob',id:2}], cars:[{name:'car1',id:1},{name:'car2',id:2}]}";
    }

    @PostMapping("/post/param")
    public String postParam(@RequestParam("username") String username, @RequestParam("password") String password) {
        log.info("=====================/post/param=================================");
        log.info("username:" + username + ", password: " + password);
        return "{persons:[{name:'艾米一',id:1},{name:'Bob',id:2}], cars:[{name:'car1',id:1},{name:'car2',id:2}]}";
    }

    @PostMapping("/post/demo")
    public String post(@RequestBody Demo demo) {
        log.info("=====================/post/demo=================================");
        log.info("username:" + demo.getUsername() + ", password: " + demo.getPassword());
        return "{persons:[{name:'Amy',id:1},{name:'Bob',id:2}], cars:[{name:'car1',id:1},{name:'car2',id:2}]}";
    }

    @RequestMapping("/test")
    public String test(HttpSession session, HttpServletRequest request) {
        request.setAttribute("id", session.getId());
        return "我的 session id：" + session.getId();
    }
}
