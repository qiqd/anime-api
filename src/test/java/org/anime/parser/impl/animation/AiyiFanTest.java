package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.base.Media;

import java.util.List;

public class AiyiFanTest extends TestCase {
  private final AiyiFan aiyiFan = new AiyiFan();

  public void testFetchSearchSync() throws Exception {
    List<Media> animations = aiyiFan.fetchSearchSync("反叛的鲁路修", 1, 10, System.out::println);
    System.out.println(JSON.toJSONString(animations));
    System.out.println(JSON.toJSONString(aiyiFan.fetchDetailSync("/ayf.sbs-vod/54456.html", System.out::println)));
    System.out.println(JSON.toJSONString(aiyiFan.fetchViewSync("/ayf.sbs-play/54456-1-1.html", System.out::println)));
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(aiyiFan.fetchDetailSync("/ayf.sbs-vod/54456.html", System.out::println)));
  }

  public void testFetchViewSync() throws Exception {
    System.out.println(JSON.toJSONString(aiyiFan.fetchViewSync("/ayf.sbs-play/54456-1-1.html", System.out::println)));
  }
}