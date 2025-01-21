package com.lion.ws.controller;

import com.lion.ws.service.KisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/kis")
public class KisController {
    @Autowired private KisService kisService;

    @GetMapping("/current")
    public String kisCurrent() {
        return "kis/current";
    }

    @GetMapping("/getStockInfo")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStockInfo(@RequestParam String itemCode) {
        Map<String, String> output = kisService.getCurrentValue(itemCode);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/real")
    public String kisReal(HttpSession session, Model model) {
        String kisApprovalKey = (String) session.getAttribute("KIS_APPROVAL_KEY");
//        kisApprovalKey = "ebf3784f-14a8-4179-a66b-d155078c73c0";
        if (kisApprovalKey == null || kisApprovalKey.isEmpty()) {
            kisApprovalKey = kisService.getKisApprovalKey();
            System.out.println("KIS_APPROVAL_KEY=" + kisApprovalKey);
            session.setAttribute("KIS_APPROVAL_KEY", kisApprovalKey);
            session.setMaxInactiveInterval(24 * 60 * 60);
        }
        model.addAttribute("approvalKey", kisApprovalKey);
        return "kis/real-market-price";
    }
}
