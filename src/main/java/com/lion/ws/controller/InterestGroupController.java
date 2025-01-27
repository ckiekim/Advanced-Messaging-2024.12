package com.lion.ws.controller;

import com.lion.ws.entity.InterestGroup;
import com.lion.ws.service.InterestGroupService;
import com.lion.ws.service.KisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/interest")
public class InterestGroupController {
    @Autowired private InterestGroupService interestGroupService;
    @Autowired private KisService kisService;

    @GetMapping("/multi")
    public String multi(@RequestParam(name="n", defaultValue = "관심그룹") String groupName, HttpSession session, Model model) {
        String owner = (String) session.getAttribute("sessUid");
        List<InterestGroup> list = interestGroupService.findByOwner(owner);
        InterestGroup interestGroup = null;
        for (InterestGroup group: list) {
            if (group.getName().equals(groupName))
                interestGroup = group;
        }

        session.setAttribute("menu", "interest");
        model.addAttribute("codeList", interestGroup.getCodes());
        model.addAttribute("interestGroupList", list);
        return "interest/multi";
    }

    @PostMapping("/getMultiValue")
    public ResponseEntity<List<Map<String, String>>> getMultiValue(@RequestBody List<String> codes, HttpSession session) {
        String oAuthToken = kisService.handleOAuthToken(session);
        List<Map<String, String>> output = interestGroupService.getMultiValue(codes, oAuthToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/insert")
    public String insertInterestGroupForm(Model model) {
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= 10; i++)
            numbers.add(String.valueOf(i));
        model.addAttribute("numbers", numbers);
        return "interest/insert";
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertInterestGroupProc(@RequestBody Map<String, Object> interestGroup, HttpSession session) {
        String owner = (String) session.getAttribute("sessUid");
        String name = (String) interestGroup.get("name");
        List<String> itemCodes = (List<String>) interestGroup.get("codes");
        InterestGroup group = InterestGroup.builder()
                .name(name)
                .owner(owner)
                .codes(itemCodes)
                .build();
        interestGroupService.insertInterestGroup(group);
        return ResponseEntity.ok("등록 성공");
    }

    @GetMapping("/initData")
    @ResponseBody
    public String initData() {
        List<String> codes = new ArrayList<>();
        codes.add("005930"); codes.add("277810");
        InterestGroup group = InterestGroup.builder()
                .name("관심그룹")
                .owner("james")
                .codes(codes)
                .build();
        interestGroupService.insertInterestGroup(group);
        List<InterestGroup> list = interestGroupService.findByOwner("james");
        String html = "";
        for (InterestGroup ig: list)
            html += ig + "<br>";
        return html;
    }
}
