package com.streetconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Root controller that redirects the app root "/" to the index.html frontend page.
 */
@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index.html";
    }
}
