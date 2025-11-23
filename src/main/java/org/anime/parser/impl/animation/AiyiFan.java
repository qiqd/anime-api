package org.anime.parser.impl.animation;

import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.base.*;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.AbstractAnimationParser;
import org.anime.util.HttpUtil;
import org.anime.util.StringUtil;
import org.anime.util.UnicodeUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AiyiFan extends AbstractAnimationParser implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(AiyiFan.class);
  public static final String NAME = "爱壹番";
  public static final String LOGOURL = "https://www.aiyifan.sbs/static/images/logo.jpg";
  public static final String BASEURL = "https://www.aiyifan.sbs";

  private Animation fillAnimation(String videoId, String titleCN, String cover, String status, String genre, String actor, String director, String country) {
    Animation animation = new Animation();
    animation.setId(videoId);
    animation.setTitleCn(titleCN);
    animation.setCoverUrls(Collections.singletonList(cover));
    animation.setStatus(status);
    animation.setGenre(StringUtil.removeUnusedChar(genre));
    animation.setActor(StringUtil.removeUnusedChar(actor));
    animation.setDirector(StringUtil.removeUnusedChar(director));
    animation.setCountry(country);
    return animation;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getLogoUrl() {
    return LOGOURL;
  }

  @Override
  public String getBaseUrl() {
    return BASEURL;
  }

  @Override
  public List<Animation> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler) {
    try {
      String searchUrl = "/ayf.sbssearch/-------------.html?wd=" + StringUtil.removeBlank(keyword);
      Element document = HttpUtil.createConnection(BASEURL + searchUrl).get().body();
      Elements animeItems = document.select("div.details-info-min");
      List<Animation> animeList = new ArrayList<>();
      for (Element it : animeItems) {
        Element imageElement = it.select("a.video-pic").get(0);
        String id = imageElement.attr("href");
        String title = imageElement.attr("title");
        String cover = imageElement.attr("data-original");
        Elements li = it.select("ul.info li");
        String status = li.size() > 2 ? li.get(2).text() : "";
        String type = li.size() > 3 ? li.get(3).select("a").text() : "";
        StringBuilder actorBuilder = new StringBuilder();
        Elements actorElements = li.size() > 4 ? li.get(4).select("a") : new Elements();
        for (int i = 0; i < actorElements.size(); i++) {
          if (i > 0) actorBuilder.append(",");
          actorBuilder.append(actorElements.get(i).text());
        }
        String actor = actorBuilder.toString();
        StringBuilder directorBuilder = new StringBuilder();
        Elements directorElements = li.size() > 5 ? li.get(5).select("a") : new Elements();
        for (int i = 0; i < directorElements.size(); i++) {
          if (i > 0) directorBuilder.append(",");
          directorBuilder.append(directorElements.get(i).text());
        }
        String director = directorBuilder.toString();
        String country = li.size() > 6 ? li.get(6).text() : "";
        String year = li.size() > 8 ? li.get(8).text() : "";
        String updateTime = li.size() > 7 ? li.get(7).text() : "";
        String description = li.size() > 10 ? li.get(10).text() : "";
        Animation animation = fillAnimation(id, title, cover, status, type, actor, director, country);
        animation.setAriDate(year);
        animation.setDescription(description);
        animeList.add(animation);
      }
      return animeList;
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return Collections.emptyList();
    }
  }

  @Override
  @Nullable
  public Detail<Animation> fetchDetailSync(String videoId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASEURL + videoId).get().body();
      Elements playSource = doc.select("div.playlist-mobile ul.clearfix");
      List<Source> sources = playSource.stream().map(item -> {
        Source source = new Source();
        List<Episode> episodes = item.select("li a").stream().map(i -> {
          Episode episode = new Episode();
          episode.setId(i.attr("href"));
          episode.setTitle(i.text());
          return episode;
        }).collect(Collectors.toList());
        source.setEpisodes(episodes);
        return source;
      }).collect(Collectors.toList());
      if (sources.isEmpty()) {
        log.error("No source found for videoId: {}", videoId);
        return null;
      }
      Elements coverImg = doc.select("div.details-pic a");
      String titleCN = coverImg.attr("title");
      String cover = coverImg.attr("style");
      cover = cover.substring(cover.indexOf("(") + 1, cover.indexOf(")"));
      Elements infoBox = doc.select("ul.info.clearfix li");
      String status = infoBox.size() > 1 ? infoBox.get(1).ownText() : "";
      String genre = infoBox.size() > 2 ? String.join(",", infoBox.get(2).select("a").eachText()) : "";
      String actor = infoBox.size() > 3 ? String.join(",", infoBox.get(3).select("a").eachText()) : "";
      String director = infoBox.size() > 4 ? String.join(",", infoBox.get(4).select("a").eachText()) : "";
      String country = infoBox.size() > 5 ? infoBox.get(5).ownText() : "";
      String language = infoBox.size() > 6 ? infoBox.get(6).ownText() : "";
      String ariDate = infoBox.size() > 7 ? infoBox.get(7).ownText() : "";
      String description = doc.select("span.details-content-all").text();
      Animation animation = fillAnimation(videoId, titleCN, cover, status, genre, actor, director, country);
      animation.setId(videoId);
      animation.setLanguage(language);
      animation.setAriDate(ariDate);
      animation.setDescription(description);
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
      Element doc = HttpUtil.createConnection(BASEURL + episodeId).get().body();
      List<Element> playerAaaa = doc.select("script[type='text/javascript']").stream().filter(item -> item.data().contains("player_aaaa")).collect(Collectors.toList());
      String playData = playerAaaa.get(0).data();
      Pattern pattern = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]*)\"");
      Matcher matcher = pattern.matcher(playData);
      if (matcher.find()) {
        String playUri = matcher.group(1).replaceAll("\\\\", "");
        return new ViewInfo(null, episodeId, Collections.singletonList(UnicodeUtils.decodeUnicode(playUri)));
      }
      return null;
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  public String fetchRecommendSync(String html, ExceptionHandler exceptionHandler) {
    return "";
  }

  @Override
  public List<Schedule> fetchWeeklySync(ExceptionHandler exceptionHandler) {
    return Collections.emptyList();
  }
}
