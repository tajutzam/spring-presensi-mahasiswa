package com.absensi.absensi.controller;

import com.absensi.absensi.model.Student;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthController {

    @GetMapping("login")
    public String loginPage(Model model) {
        model
                .addAttribute("mhs" , new Student());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            if(authentication.getName().equalsIgnoreCase("anonymousUser")){
                return "student/login";
            }else{
                return "redirect:dashboard";
            }
        }else{
            return "student/login";
        }
    }

    @GetMapping("403")
    public String accessDenied(){
        return "403";
    }
}
