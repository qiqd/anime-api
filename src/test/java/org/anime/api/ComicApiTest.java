package org.anime.api;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.meta.SourceWithDelay;
import org.anime.parser.AbstractComicParser;

import java.util.ArrayList;

public class ComicApiTest extends TestCase {

  public void testMoveToTop() {
  }

  public void testInitialization() {
    ComicApi.initialization();
    ArrayList<SourceWithDelay<AbstractComicParser>> sourcesWithDelay = ComicApi.SOURCES_WITH_DELAY;
    System.out.println(JSON.toJSONString(sourcesWithDelay));
  }
}