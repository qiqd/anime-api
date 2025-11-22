package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.base.ViewInfo;

public class SenFunTest extends TestCase {
  private final static SenFun senFun = new SenFun();

  public void testFetchSearchSync() throws Exception {
    System.out.println(JSON.toJSONString(senFun.fetchSearchSync("jojo", 1, 10, System.out::println)));
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(senFun.fetchDetailSync("/voddetail/1993743391.html", System.out::println)));
  }

  public void testFetchViewSync() throws Exception {
    ViewInfo playInfo = senFun.fetchViewSync("/vodwatch/1993743391/ep4.html", System.out::println);
    System.out.println(JSON.toJSONString(playInfo));
  }
}