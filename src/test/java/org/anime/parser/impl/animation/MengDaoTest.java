package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.base.Detail;
import org.anime.entity.base.Media;
import org.anime.entity.base.ViewInfo;

import java.util.List;

public class MengDaoTest extends TestCase {
  private final MengDao mengDao = new MengDao();

  public void testFetchSearchSync() throws Exception {
    List<Media> animations = mengDao.fetchSearchSync("租借女友第三季", 1, 10, System.out::println);
    System.out.println(JSON.toJSONString(animations));
//    Detail animationDetail = mengDao.fetchDetailSync("/man/99232.html", System.out::println);
//    System.out.println(JSON.toJSONString(animationDetail));
//    ViewInfo playInfo = mengDao.fetchViewSync("/man_v/9232-0-0.html", System.out::println);
//    System.out.println(JSON.toJSONString(playInfo));
  }

  public void testFetchDetailSync() throws Exception {
    Detail animationDetail = mengDao.fetchDetailSync("/man/912778.html", System.out::println);
    System.out.println(JSON.toJSONString(animationDetail));
  }

  public void testFetchViewSync() throws Exception {
    ViewInfo playInfo = mengDao.fetchViewSync("/man_v/12778-0-11.html", System.out::println);
    System.out.println(JSON.toJSONString(playInfo));
  }
}