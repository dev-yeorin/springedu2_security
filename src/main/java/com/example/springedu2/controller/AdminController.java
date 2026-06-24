package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.dto.MemberUpdateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    // 회원목록
    @GetMapping("/admin/members")
    public String memberList(Model model) {

        List<Member> memberList = memberService.findAll();
        model.addAttribute("memberList", memberList);
        return  "memberList";  // memberList.html

    }

    //회원 추가(관리자가)
    @PostMapping("/admin/members")
    @Transactional
    public String addCreate(
            @Valid @ModelAttribute("memberForm") MemberCreateForm memberCreateForm,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()){
            return "memberAdminForm";  // 다시 입력받아라
        }

        // 새 회원 추가 관리자가
        try {
            memberService.create(memberCreateForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("createFail", e.getMessage());
            return "memberAdminForm";  // 회원추가 실패 -> 다시 추가화면으로 이동
        }

        return "redirect:/admin/members"; // 목록조회
    }

    // 회원추가를 위해 입력받는 화면
    @GetMapping("/admin/members/new")
    public String adminCreateForm(Model model) {
        model.addAttribute("memberForm", new MemberCreateForm());
        return  "memberAdminForm";  // memberAdminForm.html
    }

    // 회원정보 수정 - 입력받는 화면으로 이동
    // /admin/members/1/edit
    @GetMapping("/admin/members/{id}/edit")
    public  String   adminEditForm(
            @PathVariable Long id, Model model
    ) {
        // 수정을 위한 db 데이터를 Entity 조회
        Member           member     = memberService.findById(id);
        // db 에서 조회한 member -> memberAdminEditForm 에서 사용할
        // 객체인 MemberUpdateForm  구조로 변경
        MemberUpdateForm memberForm = memberService.toUpdateForm(member);
        model.addAttribute("member",     member); // 조회한 정보
        model.addAttribute("memberForm", memberForm);
        return "memberAdminEditForm"; // memberAdminEditForm.html
    }

    // 넘어온 수정정보를 가지고 member 정보를 수정
    @PostMapping("/admin/members/{id}/edit")
    public String  adminEdit(
            @PathVariable   Long    id,
            @Valid  @ModelAttribute("memberForm") MemberUpdateForm form,
            BindingResult bindingResult,
            Model model
    ) {
        // 넘어온 정보롤 수정한다
        Member   member     = memberService.findById(id);
        if(bindingResult.hasErrors()){
            model.addAttribute("memberForm", member);
            return "memberAdminEditForm";
        }

        try {
            memberService.update(id, form, true);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("updateFail", e.getMessage());
            model.addAttribute("meber", member );
            return "memberAdminEditForm";
        }

        return "redirect:/admin/members";
    }

    // 회원 삭제 관리자
    @PostMapping("/admin/members/{id}/delete")
    public String  adminDelete(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes  redirectAttributes,
            Model model
    ) {
        try {
            memberService.delete(id, authentication.getName());
        } catch ( IllegalArgumentException e ) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }

        return "redirect:/admin/members";
    }

}