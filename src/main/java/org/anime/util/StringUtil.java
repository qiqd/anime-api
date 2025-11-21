package org.anime.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StringUtil implements Serializable {
  public static String removeUnusedChar(String str) {
    if (str == null) {
      return "";
    }
    return str.trim().replaceAll("\\s+", ",").replaceAll("/", ",").replaceAll("\\\\", ",");
  }

  public static String removeBlank(String str) {
    if (str == null) {
      return "";
    }
    return str.trim();
  }

  /**
   * 分离中日文标题
   *
   * @param title 完整标题字符串
   * @return Map包含"titleCn"(中文标题)和"title"(日文标题)
   */
  public static Map<String, String> separateTitles(String title) {
    Map<String, String> result = new HashMap<>();
    if (title == null || title.trim().isEmpty()) {
      result.put("titleCn", "");
      result.put("title", "");
      return result;
    }
    // 按空格和特殊符号分割标题
    String[] parts = title.split("[\\s:：・]+");

    StringBuilder chineseTitle = new StringBuilder();
    StringBuilder japaneseTitle = new StringBuilder();

    for (String part : parts) {
      if (containsJapaneseCharacters(part)) {
        // 包含日文字符（假名等）
        if (japaneseTitle.length() > 0) {
          japaneseTitle.append(" ");
        }
        japaneseTitle.append(part);
      } else if (containsChineseCharacters(part)) {
        // 包含中文字符
        if (chineseTitle.length() > 0) {
          chineseTitle.append(" ");
        }
        chineseTitle.append(part);
      } else {
        // 英文或其他字符，根据位置和上下文判断
        if (japaneseTitle.length() > 0) {
          if (japaneseTitle.length() > 0) {
            japaneseTitle.append(" ");
          }
          japaneseTitle.append(part);
        } else {
          if (chineseTitle.length() > 0) {
            chineseTitle.append(" ");
          }
          chineseTitle.append(part);
        }
      }
    }

    // 后处理：如果某一边为空，尝试重新分配
    if (chineseTitle.length() == 0 && japaneseTitle.length() > 0) {
      // 可能是纯日文标题，保持原样
    } else if (japaneseTitle.length() == 0 && chineseTitle.length() > 0) {
      // 可能是纯中文标题，保持原样
    }

    result.put("titleCn", chineseTitle.toString().trim());
    result.put("title", japaneseTitle.toString().trim());

    return result;
  }

  /**
   * 检查字符串是否包含日文字符（平假名、片假名）
   */
  private static boolean containsJapaneseCharacters(String text) {
    // 平假名范围：\u3040-\u309F
    // 片假名范围：\u30A0-\u30FF
    // 日文汉字范围：\u4E00-\u9FFF（部分）
    return text.matches(".*[\\u3040-\\u309F\\u30A0-\\u30FF].*");
  }

  /**
   * 检查字符串是否包含中文字符
   */
  private static boolean containsChineseCharacters(String text) {
    return text.matches(".*[\\u4E00-\\u9FFF].*");
  }

}
