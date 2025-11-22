package org.anime.api;

import com.alibaba.fastjson.JSON;
import org.anime.entity.animation.Animation;
import org.anime.entity.meta.SourceWithDelay;
import org.anime.parser.AbstractAnimationParser;
import org.junit.Test;

import java.util.List;

public class AnimationApiTest {

  @Test
  public void testMoveToTop() throws Exception {
    System.out.println(AnimationApi.SOURCES_WITH_DELAY);
    AnimationApi.initialization();
    System.out.println(AnimationApi.SOURCES_WITH_DELAY);
    List<SourceWithDelay<AbstractAnimationParser>> delays = AnimationApi.SOURCES_WITH_DELAY;
    SourceWithDelay<AbstractAnimationParser> source = delays.get(0);
    List<Animation> jojo = source.getHtmlParser().fetchSearchSync("JOJO", 1, 10, System.out::println);
    System.out.println(JSON.toJSONString(jojo));
  }

  @Test
  public void testInitialization() {
  }
}