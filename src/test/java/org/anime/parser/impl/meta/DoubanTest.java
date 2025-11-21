package org.anime.parser.impl.meta;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.animation.Animation;
import org.anime.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoubanTest extends TestCase {
  private final Douban douban = new Douban();

  public void testFetchStaffSync() throws Exception {
    List<Animation> animations = douban.fetchSearchSync("JOJO的奇妙冒险", 1, 1);
    List<String> titleCNs = animations.stream().map(Animation::getTitleCn).collect(Collectors.toList());
    for (String titleCN : titleCNs) {
      Map<String, String> separatedTitles = StringUtil.separateTitles(titleCN);
      String titleCn = separatedTitles.get("titleCn");
      String title = separatedTitles.get("title");
      System.out.println(titleCn);
      System.out.println(title);
    }
  }

  public void testFetchSearchSync() throws Exception {
    System.out.println(JSON.toJSONString(douban.fetchDetailSync("11498785")));
  }

  public void testFetchDetailSync() throws Exception {
    System.out.println(JSON.toJSONString(douban.fetchStaffSync("11498785")));
  }
}