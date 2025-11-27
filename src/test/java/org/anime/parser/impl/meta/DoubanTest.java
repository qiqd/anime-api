package org.anime.parser.impl.meta;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.base.Media;

import java.util.List;

public class DoubanTest extends TestCase {
  private final Douban douban = new Douban();

  public void testFetchStaffSync() throws Exception {
    List<Media> animations = douban.fetchSearchSync("JOJO的奇妙冒险", 1, 1, System.out::println);
    System.out.println(JSON.toJSONString(animations));
  }

  public void testFetchSearchSync() throws Exception {
    long start = System.currentTimeMillis();
    System.out.println(JSON.toJSONString(douban.fetchDetailSync("3141506", System.out::println)));
    System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(douban.fetchStaffSync("23774708", System.out::println)));
  }
}