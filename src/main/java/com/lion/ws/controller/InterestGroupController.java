package com.lion.ws.controller;

import com.lion.ws.entity.InterestGroup;
import com.lion.ws.entity.StockCodeName;
import com.lion.ws.service.InterestGroupService;
import com.lion.ws.service.KisOAuthTokenService;
import com.lion.ws.service.StockCodeNameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/interest")
public class InterestGroupController {
    @Autowired private InterestGroupService interestGroupService;
    @Autowired private StockCodeNameService stockCodeNameService;
    @Autowired private KisOAuthTokenService kisOAuthTokenService;

    @GetMapping("/multi")
    public String multi(@RequestParam(name="n", defaultValue = "관심그룹") String groupName, HttpSession session, Model model) {
        String owner = (String) session.getAttribute("sessUid");
        List<InterestGroup> list = interestGroupService.findByOwner(owner);
        if (list == null || list.size() == 0)
            return "redirect:/interest/insert";

        InterestGroup interestGroup = null;
        if (list.size() == 1) {             // 관심그룹이 1개 있으면
            interestGroup = list.get(0);
        } else {                            // 관심그룹이 여러개 있으면
            for (InterestGroup group: list) {
                if (group.getName().equals(groupName))
                    interestGroup = group;
            }
            if (interestGroup == null)
                interestGroup = list.get(0);
        }

        session.setAttribute("menu", "interest");
        model.addAttribute("groupId", interestGroup.getId());
        model.addAttribute("codeList", interestGroup.getCodes());
        model.addAttribute("selectedGroup", interestGroup.getName());
        model.addAttribute("interestGroupList", list);
        return "interest/multi";
    }

    @PostMapping("/getMultiValue")
    public ResponseEntity<List<Map<String, String>>> getMultiValue(@RequestBody List<String> codes) {
        String oAuthToken = kisOAuthTokenService.getToken();
        List<Map<String, String>> output = interestGroupService.getMultiValue(codes, oAuthToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/getStockList")
    @ResponseBody
    public ResponseEntity<InterestGroup> getStockList(@RequestParam String groupName, HttpSession session) {
        String owner = (String) session.getAttribute("sessUid");
        InterestGroup interestGroup = interestGroupService.findByOwnerAndName(owner, groupName);
        return ResponseEntity.ok(interestGroup);
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

    @GetMapping("/getInterestGroupList")
    @ResponseBody
    public ResponseEntity<List<InterestGroup>> getInterestGroupList(HttpSession session) {
        String owner = (String) session.getAttribute("sessUid");
        List<InterestGroup> interestGroupList = interestGroupService.findByOwner(owner);
        return ResponseEntity.ok(interestGroupList);
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable long id, Model model) {
        InterestGroup interestGroup = interestGroupService.findById(id);
        List<Map<String, String>> itemList = new ArrayList<>();
        for (String code: interestGroup.getCodes()) {
            Map<String, String> map = new LinkedHashMap<>();
            StockCodeName stockCodeName = stockCodeNameService.findByCode(code);
            map.put("code", code);
            map.put("name", stockCodeName.getName());
            itemList.add(map);
        }
        for (int i = itemList.size(); i < 20; i++) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("code", "");
            map.put("name", "");
            itemList.add(map);
        }
        List<List<Map<String, String>>> pairedItems = new ArrayList<>();
        for (int i = 0; i < 20; i += 2) {
            pairedItems.add(itemList.subList(i, i + 2));
        }
        model.addAttribute("groupId", id);
        model.addAttribute("groupName", interestGroup.getName());
        model.addAttribute("pairedItems", pairedItems);
        return "interest/update";
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateInterestGroupProc(@RequestBody Map<String, Object> interestGroup, HttpSession session) {
        String owner = (String) session.getAttribute("sessUid");
        long id = ((Number) interestGroup.get("id")).longValue();
        String name = (String) interestGroup.get("name");
        List<String> itemCodes = (List<String>) interestGroup.get("codes");
        InterestGroup group = InterestGroup.builder()
                .id(id)
                .name(name)
                .owner(owner)
                .codes(itemCodes)
                .build();
        interestGroupService.updateInterestGroup(group);
        return ResponseEntity.ok("수정 성공");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        interestGroupService.deleteInterestGroup(id);
        return "redirect:/interest/multi";
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
