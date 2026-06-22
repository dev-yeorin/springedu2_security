package com.example.springedu2.controller;

import com.example.springedu2.MemberService;
import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.repository.MemberRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {


    private MemberService memberService;
    // 생성자 주입
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    // 회원가입 페이지로 이동
    @GetMapping("/members/register")
    public String registerForm(Model model) {
        model.addAttribute("memberForm", new MemberCreateForm());

        return "memberRegister"; // memberRegister.html
    }

    // 회원가입
    @PostMapping("/members/register")
    public String registerMember(@Valid @ModelAttribute("memberForm") MemberCreateForm memberForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        // 입력에 오류가 있다면 다시 입력 화면으로 돌아가
        if(bindingResult.hasErrors()) {
            return "memberRegister";// memberRegister.html
        }

        // 회원가입 : db에  저장
        memberService.register( memberForm );

        redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다");

        return "redirect:/members/list";
    }

}
