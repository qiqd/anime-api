package org.anime.util;

import org.junit.Test;

import java.util.Map;

public class DoubanJsoupClientTest {
  @Test
  public void test() {
    try {
      System.out.println("🚀 === Jsoup豆瓣反爬虫绕过工具 === 🚀");

      // 目标URL
      String targetUrl = "https://search.douban.com/movie/subject_search?search_text=金色时光&cat=1002";

      // 创建客户端
      DoubanJsoupClient client = new DoubanJsoupClient();

      // 步骤1: 获取验证数据
      Map<String, String> formData = client.getVerificationData(targetUrl);

      // 步骤2: 计算nonce值
      String cha = formData.get("cha");
      if (cha == null || cha.isEmpty()) {
        System.err.println("❌ 未找到挑战值(cha)，可能页面结构已变化");
        return;
      }

      long startTime = System.currentTimeMillis();
      long nonce = DoubanJsoupBypass.findNonce(cha, 4);
      long endTime = System.currentTimeMillis();

      System.out.println("⏱️  计算耗时: " + (endTime - startTime) + "ms");

      // 步骤3: 更新表单数据
      formData.put("sol", String.valueOf(nonce));
      Thread.sleep(1000);
      // 步骤4: 发送验证请求
      String result = client.sendVerification(formData);

      // 步骤5: 验证结果
      System.out.println("\n📊 验证结果分析:");
      System.out.println("📄 响应内容预览: " + result);
      if (result.contains("subject_search") ||
              result.contains("搜索结果") ||
              result.contains("金色时光") ||
              result.contains("douban.com")) {

        System.out.println("✅ 验证成功！可以访问目标页面了");

        // 提取一些关键信息
        if (result.contains("<title>")) {
          String title = result.substring(result.indexOf("<title>") + 7, result.indexOf("</title>"));
          System.out.println("📄 页面标题: " + title.trim());
        }

      } else {
        System.out.println("❌ 验证失败，可能需要重新尝试");
        System.out.println("📄 响应内容预览: " + result.substring(0, Math.min(300, result.length())));
      }

    } catch (Exception e) {
      System.err.println("❌ 执行失败: " + e.getMessage());
      e.printStackTrace();
    }
  }
}