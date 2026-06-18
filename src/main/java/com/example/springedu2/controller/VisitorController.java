package com.example.springedu2.controller;

import com.example.springedu2.entity.Visitor;
import com.example.springedu2.repository.VisitorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VisitorController {


    // 1번 방식: @Autowired 대신 생성자 주임
    // @Autowired
    // private VisitorRepository visitorRepository;

    // 2번 방식: 생성자 주임: 요즘 방식
    /*
    private VisitorRepository visitorRepository;
    public VisitorController(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }*/

    // 3. 생성자 주입  다른 방법
    // @RequiredArgsConstructor  최상단에 필수 :  Lombok 필수 사용
    private final VisitorRepository visitorRepository;

    // 방명록 조회
    @GetMapping("/vlist")
    public ModelAndView vlist(){
        
        List<Visitor> visitors = visitorRepository.findAll();
        return visitorView(visitors, null);
    }

    private ModelAndView visitorView(List<Visitor> visitors, String buttonText) {
        ModelAndView mv = new ModelAndView("visitorView");
        //mv.setViewName("visitorView"); // visitorview.html(모델 사용) - thymeleaf
        if (visitors.isEmpty()) {
            mv.addObject("msg", "조회된 결과가 없습니다");

        } else {
            mv.addObject("vList", visitors);
        }
        if (buttonText != null) {
            mv.addObject("buttonText", buttonText);

        }
        mv.addObject("vList", visitors);
        return mv;
    }

    // 방명록 추가
    // @Valid: form에서 넘어온 자료를 @Entity에 있는 설정과 비교해서
    // 입력 데이터를 검증함
    @PostMapping("/vinsert")
    @Transactional
    public String vinsert(@Valid Visitor visitor,
            BindingResult bindingResult,
            Model model){

        System.out.print("visitor:" + visitor);
        System.out.print("bindingResult:" + bindingResult);
        if(bindingResult.hasErrors()){
            model.addAttribute("msg","이름과 내용을 모두 입력하세요");
            return "visitorView"; // visitorview.html
        }
        visitorRepository.save(visitor); // entity 객체를 사용해야 한다

        return "redirect:/vlist";
    }

    // /one 방명록 id 조회 : Rest 호출 결과 : json
    // return 값이 Visitor 객체인데 이것은 json으로 변경되어 다운로드됨
    // Return 값이
    @GetMapping(value="/one", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Visitor> one(@RequestParam Integer id) {
        return visitorRepository.findById(Long.valueOf(id))// data를 id로 조회, 있으면 visitor 리턴
                .map(ResponseEntity::ok) // 상태 코드 ok 200 추가해서 리턴
                .orElseGet(()-> ResponseEntity.notFound().build());
                // 못 찾으면 null 대신 404 코드를 객체로 바꿔서 (.bulid()) 리턴
    }

    /*
    // /vupdate 수정
    @PostMapping("/vupdate")
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 입력하세요");

            return "redirect:/vlist";
        }

        // 수정
        visitorRepository.save(visitor);
        return "redirect:/vlist";
    }
    */

    // /vupdate 수정
    @PostMapping("/vupdate")
    @Transactional
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 입력하세요");

            return "redirect:/vlist";
        }

        Visitor entity = visitorRepository.findById(Long.valueOf(visitor.getId()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방명록입니다."));
        entity.setName(visitor.getName());
        entity.setMemo(visitor.getMemo());

        return "redirect:/vlist";
    }

    // /vdelete
    @PostMapping("/vdelete")
    @Transactional
    public String delete(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        if (!visitorRepository.existsById(Long.valueOf(id))) {
                redirectAttributes.addFlashAttribute("msg",
                        "삭제할 방명록이 없습니다.");
                return "redirect:/vlist";
        }
        visitorRepository.deleteById(Long.valueOf(id));
        return "redirect:/vlist";
    }

    // /vsearch
    // findByMemoContainingIgnoreCaseOrderByIdDesc(key)
    // 검색: 모두 대문자로 검색어를 포함한 data
    // 단    정렬은 id를 내림차순으로 출력
    @GetMapping("/vsearch")
    public  ModelAndView vsearch(@RequestParam(defaultValue = "") String key) {
        List<Visitor> visitors = key.isBlank()
                ?   visitorRepository.findAll()
                :   visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
        System.out.print( visitors );

        return visitorView(visitors, "메인으로 돌아가기");
    }

} // Controller End
