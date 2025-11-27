package org.anime.api;

import com.alibaba.fastjson.JSON;
import org.anime.entity.base.Detail;
import org.anime.entity.base.Episode;
import org.anime.entity.base.Media;
import org.anime.entity.base.ViewInfo;
import org.anime.entity.meta.SourceWithDelay;
import org.anime.parser.HtmlParser;
import org.junit.Test;

import java.util.List;

public class AnimationApiTest {

  @Test
  public void testMoveToTop() throws Exception {
    System.out.println(AnimationApi.SOURCES_WITH_DELAY);
    AnimationApi.initialization();
    System.out.println(AnimationApi.SOURCES_WITH_DELAY);
    List<SourceWithDelay> delays = AnimationApi.SOURCES_WITH_DELAY;
    SourceWithDelay source = delays.get(0);
    List<Media> jojo = source.getHtmlParser().fetchSearchSync("JOJO", 1, 10, System.out::println);
    System.out.println(JSON.toJSONString(jojo));
  }

  @Test
  public void testViewInfo() {
    AnimationApi.initialization();
    List<SourceWithDelay> delays = AnimationApi.SOURCES_WITH_DELAY;
    SourceWithDelay source = delays.get(0);
    HtmlParser htmlParser = source.getHtmlParser();
    List<Media> animations = htmlParser.fetchSearchSync("JOJO的奇妙冒险", 1, 10, System.out::println);
    String subId = animations.get(0).getId();
    Detail detail = htmlParser.fetchDetailSync(subId, System.out::println);
    assert detail != null;
    Episode episode = detail.getSources().get(0).getEpisodes().get(2);
    ViewInfo viewInfo = htmlParser.fetchViewSync(episode.getId(), System.out::println);
    System.out.println(JSON.toJSONString(viewInfo));
  }
}