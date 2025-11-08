package org.animation.service.impl;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

public class MwcyTest extends TestCase {
  private final Mwcy mwcy = new Mwcy();

  /**
   * [{"actors":["演员 :大塚剛央,伊駒ゆりえ,高橋李依,潘めぐみ,大久保瑠美,石見舞菜香,前田誠二,伊藤静,佐倉綾音,小野大輔,鈴村健一,内山昂輝,小林裕介,志村知幸,"],"coverUrls":["https://lain.bgm.tv/pic/cover/l/d1/1c/443428_FIhFu.jpg"],"directors":["导演 :平牧大輔,"],"genres":["类型 :悬疑,推理,日本动漫,日漫,漫画改,偶像,恋爱,漫改,2024年07月,TV番剧,日漫番剧,番剧,日漫番,番,音乐,TV番,日韩动漫,大塚刚央,情,校园,爱抖露,"],"id":"/bangumi/AIbCCS.html","status":"完结 2024 日本","titleCn":"我推的孩子第二季"},{"actors":["演员 :潘惠美,高桥李依,大塚刚央,伊东健人,内山夕实,高柳知叶,伊驹友里绘,"],"coverUrls":["https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2874939827.jpg"],"directors":["导演 :平牧大辅,"],"genres":["类型 :悬疑,推理,日本动漫,奇幻,剧情,漫画改,日漫番,2023年04月,TV番,情,日漫番剧,番剧,日漫,TV番剧,"],"id":"/bangumi/MFxCCS.html","status":"完结 2023 日本","titleCn":"我推的孩子"}]
   *
   * @throws Exception
   */
  public void testFetchSearchSync() throws Exception {
    System.out.println(JSON.toJSONString(mwcy.fetchSearchSync("我推的孩子", 1, 12)));
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(mwcy.fetchDetailSync("/bangumi/MFxCCS.html")));
  }

  public void testFetchPlayInfoSync() throws Exception {
    System.out.println(JSON.toJSONString(mwcy.fetchPlayInfoSync("/play/MFxCCS-1-1.html")));
  }
}