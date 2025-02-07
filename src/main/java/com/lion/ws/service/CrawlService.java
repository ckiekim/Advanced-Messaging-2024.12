package com.lion.ws.service;

import com.lion.ws.entity.OverseasMarket;
import com.lion.ws.entity.PopularStock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrawlService {

    public Map<String, Object> getNaverFinanceInfo() {
        String url = "https://finance.naver.com/";
        try {
            Document document = Jsoup.connect(url).get();

//            System.out.println("=== 해외 증시 정보 ===");
            Elements trs = document.select(".aside_stock > table > tbody > tr");
            List<OverseasMarket> overseasMarketList = new ArrayList<>();
            for (Element tr : trs) {
                String sign = tr.attr("class");
                String marketName = tr.select("a").text();
                Elements tds = tr.select("td");
                String price = tds.get(0).text();
                String change = tds.get(1).text().substring(3);
                OverseasMarket market = new OverseasMarket(marketName, sign, price, change);
                overseasMarketList.add(market);
//                System.out.printf("sign: %s, 시장: %s, 가격: %s, 변화량: %s%n", sign, marketName, price, change);
            }

//            System.out.println("=== 인기 검색 종목 ===");
            trs = document.select(".aside_popular > table > tbody > tr");
            List<PopularStock> popularStockList = new ArrayList<>();
            for (Element tr : trs) {
                String sign = tr.attr("class");
                String itemName = tr.select("a").text();
                Elements tds = tr.select("td");
                String price = tds.get(0).text();
                String change = tds.get(1).text().substring(3);
                PopularStock stock = new PopularStock(itemName, sign, price, change);
                popularStockList.add(stock);
//                System.out.printf("sign: %s, 종목: %s, 가격: %s, 변화량: %s%n", sign, itemName, price, change);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("overseas", overseasMarketList);
            map.put("popular", popularStockList);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
