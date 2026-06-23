package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    // 회원 목록
    @GetMapping("/admin/members")
    public String memberList(Model model){

        List<Member> memberList = memberService.findAll();
        model.addAttribute("memberList",memberList);
        return "memberList";
    }

    @PostMapping("/admin/members")
    public String addCreate(MemberCreateForm memberCreateForm){

    }

    // 회원 추가
    @GetMapping("/admin/members/new")
    private  String adminCreateForm(Model model){

        return "memberAdminForm"; // memberAdminForm.html
    }

}
