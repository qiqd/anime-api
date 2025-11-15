package org.anime.util;

import org.anime.api.AnimationApi;
import org.anime.parser.AbstractAnimationParser;
import org.junit.Test;

import java.util.Map;

public class AnimeApiTest {
  @Test
  public void testDecryptVideoUrl() throws Exception {
    Map<String, AbstractAnimationParser> htmlParserMap = AnimationApi.SOURCE_MAP;
    for (String s : htmlParserMap.keySet()) {
      long start = System.currentTimeMillis();
      System.out.println(s);
      System.out.println(htmlParserMap.get(s).fetchSearchSync("租借女友", 1, 10));
      System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
      System.out.println("\r\n");
    }
  }

}