package org.animation.service.impl;

import com.alibaba.fastjson.JSON;
import org.animation.entity.Animation;
import org.animation.entity.AnimationDetail;
import org.animation.entity.PlayInfo;
import org.junit.Test;

import java.util.List;

public class AAFunTest {
  private final AAFun aaFun = new AAFun();

  /**
   * [{"coverUrls":["https://img.pan.kg/images/503450_wS9yJ.webp"],"id":"/feng-n/ymCCCS.html","status":"已完结","titleCn":"租借女友 第四季"},{"coverUrls":["https://img.pan.kg/images/296076_6cP6Q.webp"],"id":"/feng-n/1vCCCS.html","status":"已完结","titleCn":"租借女友"},{"coverUrls":["https://img.pan.kg/images/401783_x6496.webp"],"id":"/feng-n/svCCCS.html","status":"已完结","titleCn":"租借女友 第三季"},{"coverUrls":["https://img.pan.kg/images/315745_n981m.webp"],"id":"/feng-n/USCCCS.html","status":"已完结","titleCn":"租借女友 第二季"}]
   *
   * @throws Exception
   */
  @Test
  public void fetchSearchSync() throws Exception {
    List<Animation> result = aaFun.fetchSearchSync("租借女友", 1, 10);
    System.out.println(JSON.toJSONString(result));
  }

  @Test
  public void fetchDetailSync() throws Exception {
    AnimationDetail animationDetail = aaFun.fetchDetailSync("/feng-n/ymCCCS.html");
    System.out.printf(JSON.toJSONString(animationDetail));
  }

  @Test
  public void fetchPlayInfoSync() throws Exception {
    PlayInfo playInfo = aaFun.fetchPlayInfoSync("/f/ymCCCS-1-1.html");
    System.out.println(JSON.toJSONString(playInfo));
  }
}