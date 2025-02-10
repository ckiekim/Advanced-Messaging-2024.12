package com.lion.ws.controller;

import com.lion.ws.entity.StockCodeName;
import com.lion.ws.service.CrawlService;
import com.lion.ws.service.KisOAuthTokenService;
import com.lion.ws.service.KisService;
import com.lion.ws.service.StockCodeNameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kis")
public class KisController {
    @Autowired private KisService kisService;
    @Autowired private StockCodeNameService stockCodeNameService;
    @Autowired private KisOAuthTokenService kisOAuthTokenService;
    @Autowired private CrawlService crawlService;

    @GetMapping("/index")
    public String index(HttpSession session) {
        session.setAttribute("menu", "index");
        return "kis/index";
    }

    @GetMapping("/getStockIndex")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getStockIndex() {
        String oAuthToken = kisOAuthTokenService.getToken();
        List<Map<String, String>> stockIndexList = new ArrayList<>();
        stockIndexList.add(kisService.getStockIndex("0001", oAuthToken));   // KOSPI
        stockIndexList.add(kisService.getStockIndex("1001", oAuthToken));   // KOSDAQ
        stockIndexList.add(kisService.getStockIndex("2001", oAuthToken));   // KOSPI200
        return ResponseEntity.ok(stockIndexList);
    }

    @GetMapping("/getFinanceInfo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFinanceInfo() {
        Map<String, Object> financeInfo = crawlService.getNaverFinanceInfo();
        return ResponseEntity.ok(financeInfo);
    }

    @GetMapping("/current")
    public String kisCurrent() {
        return "kis/current-price";
    }

    @GetMapping("/getCurrentPrice")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getCurrentPrice(@RequestParam String itemCode) {
        String oAuthToken = kisOAuthTokenService.getToken();
        Map<String, String> output = kisService.getCurrentPrice(itemCode, oAuthToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/info")
    public String kisInfo() {
        return "kis/stock-info";
    }

    @GetMapping("/getStockInfo")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStockInfo(@RequestParam String itemCode) {
        String oAuthToken = kisOAuthTokenService.getToken();
        Map<String, String> output = kisService.getStockInfo(itemCode, oAuthToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/realtime")
    public String kisReal(@RequestParam(required = false) String itemCode, HttpSession session, Model model) {
        String kisApprovalKey = (String) session.getAttribute("KIS_APPROVAL_KEY");
        kisApprovalKey = "221ad40f-58aa-4687-b0cd-a380f614026d";
        if (kisApprovalKey == null || kisApprovalKey.isEmpty()) {
            kisApprovalKey = kisService.getKisApprovalKey();
            System.out.println("KIS_APPROVAL_KEY=" + kisApprovalKey);
            session.setAttribute("KIS_APPROVAL_KEY", kisApprovalKey);
            session.setMaxInactiveInterval(24 * 60 * 60);
        }
        session.setAttribute("menu", "kis");
        model.addAttribute("approvalKey", kisApprovalKey);
//        itemCode = itemCode == null ? "" : itemCode;      // 이렇게 하지 않아도 동작함
        model.addAttribute("itemCode", itemCode);
        return "kis/realtime";
    }

    @GetMapping("/getCodeList")
    @ResponseBody
    public ResponseEntity<List<StockCodeName>> getCodeList(@RequestParam String stockName) {
        List<StockCodeName> codeList = stockCodeNameService.findByNameContaining(stockName);
        return ResponseEntity.ok(codeList);
    }

    @GetMapping("/minute")
    public String minute() {
        return "kis/minute-candle";
    }

    @GetMapping("/getMinuteCandle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMinuteCandle(@RequestParam String itemCode) {
        String oAuthToken = kisOAuthTokenService.getToken();
        Map<String, Object> output = kisService.getMinuteCandleTillNow(itemCode, oAuthToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/daily")
    public String daily() {
        return "kis/daily-candle";
    }

    @GetMapping("/getDailyCandle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDailyCandle(@RequestParam String itemCode) {
        String oAuthToken = kisOAuthTokenService.getToken();
        Map<String, Object> output = kisService.getDailyCandle(itemCode, oAuthToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/initData")
    public String initData() {
        List<StockCodeName> list = stockCodeNameService.findByNameContaining("삼성전자");
        if (list == null || list.isEmpty()) {
            stockCodeNameService.initData("kosdaq_code.txt");
            stockCodeNameService.initData("kospi_code.txt");
        }
        return "redirect:/kis/index";
    }
}
