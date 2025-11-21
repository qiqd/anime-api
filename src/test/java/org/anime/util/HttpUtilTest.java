package org.anime.util;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HttpUtilTest {

  @Test
  public void test() throws Exception {
    List<String> keyword = Arrays.asList("欢迎来到实力至上主义的教室", "未来日记", "租借女友", "赛马娘", "明日方舟");

    for (int i = 0; i < 150; i++) {
      String url = "https://baike.baidu.com/lemma/api/entry?word=" + keyword.get((int) (Math.random() * keyword.size()));
      Connection connection = HttpUtil.createConnection(url);
      Document body = connection.execute().parse();
      System.out.println(i + body.title());
    }
  }
}