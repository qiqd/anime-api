package org.anime.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class DoubanJsoupBypass {

  // SHA-512计算
  public static String sha512(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("SHA-512计算失败", e);
    }
  }

  // 寻找nonce值
  public static long findNonce(String challenge, int difficulty) {
    StringBuilder targetBuilder = new StringBuilder();
    for (int i = 0; i < difficulty; i++) {
      targetBuilder.append('0');
    }
    String target = targetBuilder.toString();
    long nonce = 0;

    System.out.println("开始计算工作量证明 (难度: " + difficulty + ")...");

    while (true) {
      nonce++;
      String hash = sha512(challenge + nonce);
      if (hash.startsWith(target)) {
        System.out.println("✅ 找到nonce: " + nonce);
        System.out.println("✅ 对应hash: " + hash);
        return nonce;
      }
      if (nonce % 50000 == 0) {
        System.out.println("已尝试 " + nonce + " 次...");
      }
    }
  }
}