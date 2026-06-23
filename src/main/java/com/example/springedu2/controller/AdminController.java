package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    @Transactional
    public String addCreate(@Valid @ModelAttribute("memberForm") MemberCreateForm memberCreateForm,
                            BindingResult bindingResult
                            ){
        if(bindingResult.hasErrors()){
            return "memberAdminForm"; // 다시 입력 받기
        }

        // 새 회원 추가 관리자
        try {
            memberService.create(memberCreateForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("CreateFail",e.getMessage());
            return "memberAdminForm";
        }

        return "redirect:/admin/members"; // 목록 조회
    }

    // 회원 추가를 위해 입력 받는 화면
    @GetMapping("/admin/members/new")
    private  String adminCreateForm(Model model){
        model.addAttribute("memberForm",new MemberCreateForm());

        return "memberAdminForm"; // memberAdminForm.html
    }

}
