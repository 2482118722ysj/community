package com.company.community.controller;

import com.company.community.mapper.PublishMapper;
import com.company.community.models.Publish;
import com.company.community.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private PublishMapper publishMapper;

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    @PostMapping("/publish")
    public String postPublish(Publish publish, Model model,
                              HttpServletRequest request
                              ){
        model.addAttribute("publish",publish);
        User user = (User)request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        if(publish.getTitle().trim().equals("")){
            model.addAttribute("error","问题标题不能为为空！");
            return "publish";
        }
        if(publish.getTag().trim().equals("")){
            model.addAttribute("error","问题描述不能为空！");
            return "publish";
        }
        if(publish.getDescription().trim().equals("")){
            model.addAttribute("error","标签不能为空！");
            return "publish";
        }
        publish.setCreator(user.getId());
        publish.setGmtCreate(System.currentTimeMillis());
        publish.setGmtModified(publish.getGmtCreate());
        publishMapper.insertProblem(publish);
        return "redirect:/";
    }

}