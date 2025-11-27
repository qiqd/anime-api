package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.base.Detail;
import org.anime.entity.base.Media;
import org.anime.entity.base.ViewInfo;

import java.util.List;

public class GirigiriLoveTest extends TestCase {
  private final GirigiriLove girigiriLove = new GirigiriLove();

  public void testFetchSearchSync() throws Exception {
    List<Media> animations = girigiriLove.fetchSearchSync("JOJO的奇妙冒险", 10, 1, System.out::println);
    System.out.println(JSON.toJSONString(animations));
    Detail animationDetail = girigiriLove.fetchDetailSync("/GV765/", System.out::println);
    System.out.println(JSON.toJSONString(animationDetail));
    ViewInfo playInfo = girigiriLove.fetchViewSync("/playGV765-1-1/", System.out::println);
    System.out.println(JSON.toJSONString(playInfo));
  }

  /**
   * {"animation":{"actors":["小野大辅•石冢运升•三宅健太•平川大辅•小松史法•子安武人•福圆美里"],"coverUrls":[""],"description":"1989年，日本。乔纳森·乔斯达与DIO决战后的一百年，DIO复活了。同时，乔瑟夫的孙子，空条承太郎发现自己有幽波纹（替身）能力，DIO的复活影响了没有替身抵抗能力的母亲，陷入病危情况；为了拯救命在旦夕的母亲，承太郎与乔瑟夫出发前往DIO的所在地——埃及。","directors":["津田尚克•加藤敏幸•铃木健一•副岛惠文•町谷俊辅•高村雄太•藤本次朗•江副仁美•吉川志我津"],"genres":["2015·一月·剧情 动作 热血 奇幻 冒险 悬疑·2015-01-09(日本)上映·日语·"],"id":"/GV765/","status":"2015•一月•津田尚克•加藤敏幸•铃木健一•副岛惠文•町谷俊辅•高村雄太•藤本次朗•江副仁美•吉川志我津•小野大辅•石冢运升•三宅健太•平川大辅•小松史法•子安武人•福圆美里•剧情•动作•热血•奇幻•冒险•悬疑已完结","titleCn":""},"sources":[{"episodes":[{"id":"/playGV765-1-1/","title":"01"},{"id":"/playGV765-1-2/","title":"02"},{"id":"/playGV765-1-3/","title":"03"},{"id":"/playGV765-1-4/","title":"04"},{"id":"/playGV765-1-5/","title":"05"},{"id":"/playGV765-1-6/","title":"06"},{"id":"/playGV765-1-7/","title":"07"},{"id":"/playGV765-1-8/","title":"08"},{"id":"/playGV765-1-9/","title":"09"},{"id":"/playGV765-1-10/","title":"10"},{"id":"/playGV765-1-11/","title":"11"},{"id":"/playGV765-1-12/","title":"12"},{"id":"/playGV765-1-13/","title":"13"},{"id":"/playGV765-1-14/","title":"14"},{"id":"/playGV765-1-15/","title":"15"},{"id":"/playGV765-1-16/","title":"16"},{"id":"/playGV765-1-17/","title":"17"},{"id":"/playGV765-1-18/","title":"18"},{"id":"/playGV765-1-19/","title":"19"},{"id":"/playGV765-1-20/","title":"20"},{"id":"/playGV765-1-21/","title":"21"},{"id":"/playGV765-1-22/","title":"22"},{"id":"/playGV765-1-23/","title":"23"},{"id":"/playGV765-1-24/","title":"24"}]}]}
   *
   * @throws Exception
   */
  public void testFetchDetailSync() throws Exception {
    Detail animationDetail = girigiriLove.fetchDetailSync("/GV765/", System.out::println);
    System.out.println(JSON.toJSONString(animationDetail));
  }

  public void testFetchViewSync() throws Exception {
    ViewInfo playInfo = girigiriLove.fetchViewSync("/playGV765-1-1/", System.out::println);
    System.out.println(JSON.toJSONString(playInfo));
  }
}