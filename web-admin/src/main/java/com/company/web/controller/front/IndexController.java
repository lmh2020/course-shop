package com.company.web.controller.front;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Description
 * @Date 2021/4/4
 */
@RestController
@RequestMapping("front")
public class IndexController {

    private final static String PREF = "/backend/";

    @GetMapping(value = "page")
    public ModelAndView page(HttpServletRequest request, @RequestParam String path) {
        ModelAndView modelAndView = new ModelAndView(PREF + path);
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames != null){
            while (parameterNames.hasMoreElements()){
                String paramKey = parameterNames.nextElement();
                if (!paramKey.equals("path") && !paramKey.equals("Authorization")){
                    modelAndView.addObject(paramKey, request.getParameter(paramKey));
                }
            }
        }
        return modelAndView;
    }

    @GetMapping(value = "loginPage")
    public ModelAndView loginPage() {
        return new ModelAndView(PREF + "login");
    }

    @GetMapping(value = "registerPage")
    public ModelAndView registerPage() {
        return new ModelAndView(PREF + "register");
    }

}
