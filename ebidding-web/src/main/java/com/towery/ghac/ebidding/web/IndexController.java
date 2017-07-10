package com.towery.ghac.ebidding.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by User on 2017/6/27.
 */
@Slf4j
@Controller
@RequestMapping(value="/")
public class IndexController {
    @RequestMapping
    public String  main() {
        return "index";
    }
}
