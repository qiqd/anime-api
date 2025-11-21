package org.anime.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class DoubanJsoupClient {
  private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

  // 获取验证页面并解析表单数据
  public Map<String, String> getVerificationData(String targetUrl) {
    try {
      System.out.println("📥 获取验证页面: " + targetUrl);

      Connection.Response response = Jsoup.connect(targetUrl)
              .userAgent(userAgent)
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
              .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
              .header("Accept-Encoding", "gzip, deflate, br")
              .header("Connection", "keep-alive")
              .header("Upgrade-Insecure-Requests", "1")
              .followRedirects(true)
              .execute();

      Document doc = response.parse();

      // 提取表单数据
      Map<String, String> formData = new HashMap<>();

      // 提取所有隐藏字段
      for (Element input : doc.select("input[type=hidden]")) {
        String name = input.attr("name");
        String value = input.attr("value");
        if (!name.isEmpty()) {
          formData.put(name, value);
          System.out.println("📋 提取字段: " + name + " = " + (value.length() > 30 ? value.substring(0, 30) + "..." : value));
        }
      }

      // 添加按钮字段
      formData.put("btnsubmit", "点我继续浏览");

      return formData;

    } catch (Exception e) {
      throw new RuntimeException("获取验证页面失败: " + e.getMessage(), e);
    }
  }

  // 发送验证请求
  public String sendVerification(Map<String, String> formData) {
    try {
      System.out.println("📤 发送验证请求...");

      Connection.Response response = Jsoup.connect("https://www.douban.com/c")
              .userAgent(userAgent)
              .header("Cookie", "bid=ZcM8rXyZi5g")
              .header("Content-Type", "application/x-www-form-urlencoded")
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
              .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
              .header("Origin", "https://sec.douban.com")
              .header("Referer",
                      "https://sec.douban.com/c?r=https%3A%2F%2Fsearch.douban.com%2Fmovie%2Fsubject_search%3Fsearch_text%3D%25E9%2587%2591%25E8%2589%25B2%25E6%2597%25B6%25E5%2585%2589%26cat%3D1002&_s=4ceef59cc5c36f6c368a93def0f76e17906050bd619c259b3a3a2d2c36aa3635")
              .header("Upgrade-Insecure-Requests", "1")
              .data(formData)
              .method(Connection.Method.POST)
              .followRedirects(true)
              .execute();

      return response.body();

    } catch (Exception e) {
      throw new RuntimeException("发送验证请求失败: " + e.getMessage(), e);
    }
  }
}