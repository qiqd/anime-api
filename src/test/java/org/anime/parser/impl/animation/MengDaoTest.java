package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.animation.Animation;
import org.anime.entity.base.Detail;
import org.anime.entity.base.ViewInfo;

import java.util.List;

public class MengDaoTest extends TestCase {
  private final MengDao mengDao = new MengDao();

  public void testFetchSearchSync() throws Exception {
    List<Animation> animations = mengDao.fetchSearchSync("JOJO的奇妙冒险", 1, 10, System.out::println);
    System.out.println(JSON.toJSONString(animations));
  }

  public void testFetchDetailSync() throws Exception {
    Detail<Animation> animationDetail = mengDao.fetchDetailSync("/man/99232.html", System.out::println);
    System.out.println(JSON.toJSONString(animationDetail));
  }

  public void testFetchViewSync() throws Exception {
    ViewInfo playInfo = mengDao.fetchViewSync("/man_v/9232-0-0.html", System.out::println);
    System.out.println(JSON.toJSONString(playInfo));
  }
}