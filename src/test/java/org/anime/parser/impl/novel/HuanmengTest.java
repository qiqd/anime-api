package org.anime.parser.impl.novel;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

public class HuanmengTest extends TestCase {
  private final Huanmeng huanmeng = new Huanmeng();

  public void testFetchSearchSync() throws Exception {
    System.out.println(JSON.toJSONString(huanmeng.fetchSearchSync("无职转生", 1, 10, e -> System.out.println(e.getMessage()))));
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(huanmeng.fetchDetailSync("/index.php/book/info/8655", e -> System.out.println(e.getMessage()))));
  }

  public void testFetchViewSync() throws Exception {
    ///index.php/book_read_4809_3024.html image
    ///index.php/book_read_4809_1869.html not image
    System.out.println(JSON.toJSONString(huanmeng.fetchViewSync("/index.php/book_read_4809_3024.html", e -> System.out.println(e.getMessage()))));
  }
}