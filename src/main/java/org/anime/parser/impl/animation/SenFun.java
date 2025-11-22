package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.base.*;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.AbstractAnimationParser;
import org.anime.util.HttpUtil;
import org.anime.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class SenFun extends AbstractAnimationParser implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(SenFun.class);

  public static final String NAME = "森之屋";
  public static final String LOGO_URL = "https://senfun.in/static/senfun/upload/dyxscms/20211201-1/41c4d0a159b80ec48e50a26de1374041.gif";
  public static final String BASE_URL = "https://senfun.in/";

  public static Source parseSource(Element element) {
    Source source = new Source();
    List<Episode> episodes = element.select("a").stream().map(link -> {
      Episode episode = new Episode();
      episode.setId(link.attr("href"));
      episode.setTitle(link.text());
      return episode;
    }).collect(Collectors.toList());
    source.setEpisodes(episodes);
    return source;
  }

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
  public List<Animation> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler) {
    try {
      //https://senfun.in/search.html?wd=JOJO
      Element doc = HttpUtil.createConnection(BASE_URL + "search.html?wd=" + StringUtil.removeBlank(keyword)).get().body();
      return doc.select("div.module-card-item.module-item").stream().map(item -> {
        String id = item.select("a.module-card-item-poster").attr("href");
        Elements platform = item.select("div.module-card-item-class");
        Elements coverImg = item.select("div.module-item-pic img");
        String coverUrl = coverImg.attr("data-original");
        String titleCn = coverImg.attr("alt");
        Elements infoBox = item.select("div.module-info-item-content");
        String genre = StringUtil.removeUnusedChar(infoBox.get(0).text());
        String actor = StringUtil.removeUnusedChar(infoBox.get(1).text());
        Animation animation = new Animation();
        animation.setSubId(id);
        animation.setPlatform(platform.get(0).text());
        animation.setCoverUrls(Collections.singletonList(BASE_URL + coverUrl));
        animation.setTitleCn(titleCn);
        animation.setGenre(StringUtil.removeUnusedChar(genre));
        animation.setActor(StringUtil.removeUnusedChar(actor));
        return animation;
      }).collect(Collectors.toList());

    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  @Nullable
  public Detail<Animation> fetchDetailSync(String videoId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASE_URL + videoId).get().body();
      //获取播放源
      List<Source> sources = doc.select("div.module-play-list-content.module-play-list-base").stream().map(SenFun::parseSource).collect(Collectors.toList());
      if (sources.isEmpty()) {
        log.error("未找到视频源, videoId: {}", videoId);
        return null;
      }
      //获取动画信息
      Elements infoBox = doc.select("div.module-main");
      Elements coverImg = infoBox.select("div.module-item-pic img");
      String cover = coverImg.attr("data-original");
      String titleCn = coverImg.attr("alt");
      String genre = infoBox.select("div.module-info-tag-link").text();
      Elements infoItem = infoBox.select("div.module-info-item");
      String introduction = infoItem.isEmpty() ? "" : infoItem.get(0).text();
      String director = infoItem.size() > 1 ? StringUtil.removeUnusedChar(infoItem.get(1).select("div.module-info-item-content").text()) : "";
      String screenwriter = infoItem.size() > 2 ? StringUtil.removeUnusedChar(infoItem.get(2).select("div.module-info-item-content").text()) : "";
      String actor = infoItem.size() > 3 ? StringUtil.removeUnusedChar(infoItem.get(3).select("div.module-info-item-content").text()) : "";
      String airDate = infoItem.size() > 4 ? infoItem.get(4).select("div.module-info-item-content").text() : "";
      String duration = infoItem.size() > 5 ? infoItem.get(5).select("div.module-info-item-content").text() : "";
      String totalEpisode = infoItem.size() > 7 ? infoItem.get(7).select("div.module-info-item-content").text() : "";
      Animation animation = new Animation();
      animation.setCoverUrls(Collections.singletonList(BASE_URL + cover));
      animation.setTitleCn(titleCn);
      animation.setGenre(StringUtil.removeUnusedChar(genre));
      animation.setDescription(introduction);
      animation.setDirector(director);
      animation.setScreenwriter(screenwriter);
      animation.setActor(actor);
      animation.setAriDate(airDate);
      animation.setDuration(duration);
      animation.setTotalEpisode(Integer.parseInt(totalEpisode));
      return new Detail<>(animation, null, sources);
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  @Nullable
  public ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASE_URL + episodeId).get().body();
      List<Element> script = doc.select("script[type='text/javascript']").stream().filter(item -> item.data().contains("$(document).ready (function (argument)")).collect(Collectors.toList());
      if (script.isEmpty()) {
        log.error("未找到播放信息脚本, episodeId: {}", episodeId);
        return null;
      }
      Pattern pattern = Pattern.compile("url\\s*:\\s*\"([^\"]+)\"");
      Matcher matcher = pattern.matcher(script.get(0).data());

      if (!matcher.find()) {
        log.error("未找到播放url, episodeId: {}", episodeId);
        return null;
      }
      String tempUrl = matcher.group(1);
      Connection connect = Jsoup.connect(BASE_URL + tempUrl);
      String body = connect.execute().body();
      Response response = JSON.parseObject(body, Response.class);
      String realPlayUrl = response.getVideo_plays().get(0).get("play_data");
      return new ViewInfo(null, episodeId, Collections.singletonList(realPlayUrl));
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  public String fetchRecommendSync(String html, ExceptionHandler exceptionHandler) {
    try {
      return "";
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  public List<Schedule> fetchWeeklySync(ExceptionHandler exceptionHandler) {
    try {
      return Collections.emptyList();
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Data
  private static class Response {
    List<Map<String, String>> video_plays;
    String html_content;
  }
}
