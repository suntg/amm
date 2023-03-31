package com.example.amm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class IndexController {

    @Resource
    private HttpServletRequest request;


    /**
     * 去account_list页面
     *
     * @return
     */
    @GetMapping(value = {"/", "/account_list"})
    public String accountList(HttpSession session, Model model) {

        return "table/account_list";
    }

    /**
     * 去bank_list页面
     *
     * @return
     */
    @GetMapping("/bank_list")
    public String bankList(HttpSession session, Model model) {
        return "table/bank_list";
    }


    /**
     * 从Cookie中获取频道编码channelCode
     *
     * @param cookies
     * @return
     */
    protected String getValue(Cookie[] cookies, String key) {
        String value = null;
        if (null != cookies && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (key.equals(c.getName())) {
                    value = c.getValue();
                    break;
                }
            }
        }

        return value;
    }

}
