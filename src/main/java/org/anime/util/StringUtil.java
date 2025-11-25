package org.anime.util;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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
          japaneseTitle.append(" ");
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
    // 平假名范围：\u3040-ゟ
    // 片假名范围：゠-ヿ
    // 日文汉字范围：一-鿿（部分）
    return text.matches(".*[\\u3040-\\u309F\\u30A0-\\u30FF].*");
  }

  /**
   * 检查字符串是否包含中文字符
   */
  private static boolean containsChineseCharacters(String text) {
    return text.matches(".*[\\u4E00-\\u9FFF].*");
  }

  /**
   * 使用JaroWinkler算法寻找最佳匹配项
   *
   * @param candidates 候选字符串列表
   * @param target     目标字符串
   * @return 最佳匹配的字符串，如果没有匹配项则返回null
   */
  public static String findBestMatchWithJaroWinkler(List<String> candidates, String target) {
    if (candidates == null || candidates.isEmpty() || target == null) {
      return null;
    }

    JaroWinklerSimilarity similarityChecker = new JaroWinklerSimilarity();
    String bestMatch = null;
    double highestSimilarity = -1.0;

    for (String candidate : candidates) {
      if (candidate == null) {
        continue;
      }

      double similarity = similarityChecker.apply(target, candidate);
      if (similarity > highestSimilarity) {
        highestSimilarity = similarity;
        bestMatch = candidate;
      }
    }

    return bestMatch;
  }

  /**
   * 使用Levenshtein算法寻找最佳匹配项
   *
   * @param candidates 候选字符串列表
   * @param target     目标字符串
   * @return 最佳匹配的字符串，如果没有匹配项则返回null
   */
  public static String findBestMatchWithLevenshtein(List<String> candidates, String target) {
    if (candidates == null || candidates.isEmpty() || target == null) {
      return null;
    }

    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    String bestMatch = null;
    int lowestDistance = Integer.MAX_VALUE;

    for (String candidate : candidates) {
      if (candidate == null) {
        continue;
      }

      // Levenshtein距离越小表示越相似
      int distance = levenshteinDistance.apply(target, candidate);
      if (distance < lowestDistance) {
        lowestDistance = distance;
        bestMatch = candidate;
      }
    }

    return bestMatch;
  }

  /**
   * 使用模糊匹配寻找最佳匹配项（综合考虑相似度和距离）
   *
   * @param candidates 候选字符串列表
   * @param target     目标字符串
   * @return 最佳匹配的字符串，如果没有匹配项则返回null
   */
  public static String findBestFuzzyMatch(List<String> candidates, String target) {
    if (candidates == null || candidates.isEmpty() || target == null) {
      return null;
    }

    JaroWinklerSimilarity similarityChecker = new JaroWinklerSimilarity();
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    String bestMatch = null;
    double bestScore = -1.0;

    for (String candidate : candidates) {
      if (candidate == null) {
        continue;
      }

      // 计算Jaro-Winkler相似度（值越大越相似）
      double similarity = similarityChecker.apply(target, candidate);

      // 计算Levenshtein距离（值越小越相似）
      int distance = levenshteinDistance.apply(target, candidate);

      // 标准化距离分数（转换为0-1范围，1表示完全匹配）
      double distanceScore = 1.0 - (double) distance / Math.max(target.length(), candidate.length());

      // 综合评分（可以调整权重）
      double score = 0.7 * similarity + 0.3 * distanceScore;

      if (score > bestScore) {
        bestScore = score;
        bestMatch = candidate;
      }
    }

    return bestMatch;
  }
}
