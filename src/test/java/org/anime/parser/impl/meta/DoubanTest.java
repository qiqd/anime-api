package org.anime.parser.impl.meta;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

public class DoubanTest extends TestCase {
  private final Douban douban = new Douban();

  public void testFetchStaffSync() throws Exception {
    System.out.println(JSON.toJSONString(douban.fetchSearchSync("JOJO的奇妙冒险", 1, 1)));
  }

  public void testFetchSearchSync() throws Exception {
    System.out.println(JSON.toJSONString(douban.fetchDetailSync("11498785")));
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(douban.fetchStaffSync("11498785")));
  }
}