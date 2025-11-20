package org.anime.meta.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.meta.EpisodeResult;
import org.anime.entity.meta.Item;
import org.anime.meta.MetaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Douban implements MetaService, Serializable {
  private static final String NAME = "豆瓣";
  private static final String BASE_URL = "https://search.douban.com";
  private static final String LOGO_URL = "https://movie.douban.com/";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getLogoUrl() {
    return LOGO_URL;
  }

  @Override
  public String getBaseUrl() {
    return BASE_URL;
  }

  @Override
  public List<Animation> fetchSubjectSearchSync(String keyword, Integer page, Integer size) throws Exception {
    //https://search.douban.com/movie/subject_search?search_text=%E3%80%90%E6%88%91%E6%8E%A8%E7%9A%84%E5%AD%A9%E5%AD%90%E3%80%91
    String fullUrl = BASE_URL + "/movie/subject_search?search_text=" + keyword;
    Element body = Jsoup.connect(fullUrl).get().body();
    List<Element> data = body.getElementsByTag("script").stream().filter(script -> script.data().contains("window.__DATA__")).collect(Collectors.toList());
    String script = data.get(0).data();
    script = script.substring(script.indexOf("["), script.lastIndexOf("]") + 1);
    List<Item> items = JSON.parseObject(script, new TypeReference<List<Item>>() {
    });
    return items.stream().map(item -> Animation.builder()
            .id(item.getId().toString())
            .coverUrls(Collections.singletonList(item.getUrl()))
            .rating(item.getRating().getValue().toString())
            .ratingCount(item.getRating().getCount().toString())
            .genre(item.getAbstract_())
            .actor(item.getAbstract_2())
            .build()).collect(Collectors.toList());

  }

  @Override
  public String fetchDailyRecommendSync() throws Exception {
    return "";
  }

  @Override
  public List<Schedule> fetchWeeklyUpdateSync() throws Exception {
    return Collections.emptyList();
  }

  @Override
  public EpisodeResult fetchEpisodeSync(String subjectId) throws Exception {
    return null;
  }

  @Override
  public Animation fetchSubjectSync(Integer subjectId) throws Exception {
    return null;
  }
}
