package com.example.amm;

import java.io.IOException;
import java.util.List;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;

import com.example.amm.domain.entity.AccountDO;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // CircularLinkedList<String> list = new CircularLinkedList<>();
        // list.insertWithOrder("d");
        // list.insertWithOrder("b");
        // list.insertWithOrder("a");
        // list.insertWithOrder("c");
        // // list.display(); // 输出：1 2 4 5
        // List<String> s = new ArrayList<>();
        // for (int i = 0; i < list.getSize(); i++) {
        // if (i == list.getSize() - 1) {
        // s.add(list.getNode(i) + list.getNode(0));
        // System.out.println(list.getNode(i) + list.getNode(0));
        // } else {
        // s.add(list.getNode(i) + list.getNode(i + 1));
        // System.out.println(list.getNode(i) + list.getNode(i + 1));
        // }
        // }
        // // b
        // for (String s1 : s) {
        // if (s1.startsWith("b")) {
        // System.out.println(s1);
        // }
        // }
        //
        //
        // // 增加一天
        // // "2020-01-24T12:23:56"
        // LocalDateTime offset = LocalDateTimeUtil.offset(LocalDateTimeUtil.now(), -1, ChronoUnit.DAYS);
        //
        // System.out.println(offset);
        //
        // Integer intervalDay = 100000;
        // System.out.println(-intervalDay);

        // String str = " [2023-05-05 13:44:37] =>
        // [B->C]，从：orazowtamkou0@hotmail.com，向：bamhsio@hotmail.com，通过账号余额，接收：$2.18成功";
        // Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
        // Matcher matcher = pattern.matcher(str);
        //
        // /*while (matcher.find()) {
        // String innerStr = matcher.group(1);
        // System.out.println(innerStr);
        // }*/
        // if (matcher.find()) {
        // System.out.println(matcher.group(1));
        // }
        //
        // System.out.println(LocalDateTimeUtil.of(new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT)));

        // 获取页面内容
        String url = "http://w2.ad123.win:58025/pp25/account/lookall";
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage(url);
        List<Object> rows = page.getByXPath("//table[1]/tbody/tr");

        for (Object row : rows) {

            List<HtmlTableCell> cells = ((HtmlTableRow)row).getCells();
            AccountDO accountDO = new AccountDO();
            accountDO.setId(Long.valueOf(cells.get(0).asNormalizedText()));
            accountDO.setEmail(cells.get(1).asNormalizedText());
            accountDO.setPayme(cells.get(2).asNormalizedText());
            accountDO.setGroup(Integer.valueOf(cells.get(3).asNormalizedText()));
            accountDO.setTitle(cells.get(4).asNormalizedText());
            accountDO.setBalance(cells.get(5).asNormalizedText());
            accountDO.setUpdateTime(
                LocalDateTimeUtil.of(new DateTime(cells.get(7).asNormalizedText(), DatePattern.NORM_DATETIME_FORMAT)));
            accountDO.setSuccTimes(Integer.parseInt(cells.get(8).asNormalizedText()));
            accountDO.setSuccMoney(cells.get(9).asNormalizedText());
            accountDO.setRecentLog(cells.get(10).asNormalizedText());
            accountDO.setStatus(Integer.parseInt(cells.get(11).asNormalizedText()));
            accountDO.setSwitchIp(Integer.parseInt(cells.get(12).asNormalizedText()));
            accountDO.setPackageNum(Integer.parseInt(cells.get(13).asNormalizedText()));

            accountDO.setAffiliation(cells.get(17).asNormalizedText());
            accountDO.setBatch(cells.get(18).asNormalizedText());
            accountDO.setFirstTime(
                LocalDateTimeUtil.of(new DateTime(cells.get(19).asNormalizedText(), DatePattern.NORM_DATETIME_FORMAT)));
            accountDO.setRemark(cells.get(20).asNormalizedText());
            System.out.println(JSONUtil.toJsonStr(accountDO));
            System.out.println("------------------------------------------------");
        }

    }

}
