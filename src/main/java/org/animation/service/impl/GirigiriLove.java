package org.animation.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.animation.entity.*;
import org.animation.service.HtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class GirigiriLove implements HtmlParser, Serializable {
  final static String NAME = "Girigiri爱动漫";
  final static String LOGOURL = "https://bgm.girigirilove.com/upload/site/20251010-1/b84e444374bcec3a20419e29e1070e1b.png";
  final static String BASEURL = "https://bgm.girigirilove.com";
  final static String BLANKREG = "[\\s\u3000]+";

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
  public List<Animation> fetchSearchSync(String keyword, Integer page, Integer size) throws Exception {
    String searchUrl = "/search/-------------/?wd=" + keyword;
    Element body = Jsoup.connect(BASEURL + searchUrl).get().body();
    ArrayList<Animation> animations = new ArrayList<>();
    Elements elements = body.select("div.search-list");
    for (Element element : elements) {
      String href = element.select("div.detail-info a").attr("href");
      String cover = element.select("img.gen-movie-img").attr("data-src");
      String title = element.select("img.gen-movie-img").attr("alt");
      String director = String.join("•", element.select("div.slide-info.hide.partition a").eachText());
      Elements a = element.select("div.slide-info.hide.this-wap.partition");
      String actor = String.join("•", a.get(0).select("a").eachText());
      String type = String.join("•", a.get(1).select("a").eachText());
      String description = element.select("span.cor5.thumb-blurb").text().replaceAll(BLANKREG, "");
      String status = String.join("", element.select("div.detail-info.rel.flex-auto.lightSpeedIn div.slide-info.hide.this-wap").get(0).text());
      Animation anime = new Animation();
      anime.setId(href);
      anime.setTitleCn(title);
      anime.setDescription(description);
      anime.setDirectors(List.of(director));
      anime.setActors(List.of(actor));
      anime.setGenres(List.of(type));
      anime.setStatus(status);
      anime.setCoverUrls(List.of(BASEURL + cover));
      animations.add(anime);
    }
    return animations;
  }

  @Override
  public AnimationDetail fetchDetailSync(String videoId) throws Exception {
    Element body = Jsoup.connect(BASEURL + videoId).get().body();
    Elements detailDiv = body.select("div.vod-detail.style-detail");
    String coverImg = body.select("div.detail-pic img").attr("data-src");
    String title = body.select("h3.slide-info-title").text();
    Element firstRemarksElement = detailDiv.select("div.slide-info span.slide-info-remarks").first();
    String part1 = firstRemarksElement != null ? firstRemarksElement.text() : "";
    String part2 = String.join("•", detailDiv.select("div.slide-info a").eachText());
    String status = part2 + part1;
    String director = String.join("•", detailDiv.select("div.slide-info").get(1).select("a").eachText());
    String actor = String.join("•", detailDiv.select("div.slide-info").get(2).select("a").eachText());
    String type = String.join("", detailDiv.select("a.deployment.none.cor5 span").eachText());
    String description = detailDiv.select("div#height_limit").text().replaceAll(BLANKREG, "");
    AnimationDetail animationDetail = new AnimationDetail();
    Elements listBoxDiv = body.select("div.anthology-list-box.none");
    List<Source> sources = new ArrayList<>();
    for (Element element : listBoxDiv) {
      Source source = new Source();
      List<Episode> episodes = new ArrayList<>();
      Elements links = element.select("a");
      for (Element item : links) {
        Episode episode = new Episode();
        episode.setId(item.attr("href"));
        episode.setTitle(item.text());
        episodes.add(episode);
      }
      source.setEpisodes(episodes);
      sources.add(source);
    }
    Animation anime = new Animation();
    anime.setId(videoId);
    anime.setTitleCn(title);
    anime.setCoverUrls(List.of(BASEURL + coverImg));
    anime.setDirectors(List.of(director));
    anime.setActors(List.of(actor));
    anime.setGenres(List.of(type));
    anime.setStatus(status);
    anime.setDescription(description);
    animationDetail.setAnimation(anime);
    animationDetail.setSources(sources);
    return animationDetail;
  }

  @Override
  public PlayInfo fetchPlayInfoSync(String episodeId) throws Exception {
    Element body = Jsoup.connect(BASEURL + episodeId).get().body();
    Elements scriptElements = body.getElementsByTag("script");
    String playerData = null;
    for (org.jsoup.nodes.Element script : scriptElements) {
      String scriptText = script.data();
      if (scriptText.contains("player_aaaa")) {
        playerData = scriptText;
        break;
      }
    }
    String jsonStr = extractJsonFromScript(playerData);
    if (jsonStr == null) return null;
    PlayerData player = JSON.parseObject(jsonStr, PlayerData.class);
    PlayInfo playInfo = new PlayInfo();
    playInfo.setId(episodeId);
    playInfo.setPlayUri(decodeUrl(player.getUrl()));
    return playInfo;
  }

  @Override
  public String fetchRecommendSync(String html) throws Exception {
    return "";
  }

  @Override
  public List<Schedule> fetchWeeklySync() throws Exception {
    return List.of();
  }

  private String decodeUrl(String encodedUrl) {
    if (encodedUrl.isEmpty()) return encodedUrl;

    String decodedUrl;
    try {
      // 首先尝试Base64解码
      try {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedUrl);
        decodedUrl = new String(decodedBytes, StandardCharsets.UTF_8);
      } catch (Exception base64Exception) {
        log.debug("Base64 decoding failed:{} ", base64Exception.getMessage());
        // 如果Base64解码失败，则尝试URL解码
        try {
          decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        } catch (Exception urlException) {
          log.debug("URL decoding failed:{} ", urlException.getMessage());
          decodedUrl = encodedUrl;
        }
      }

      // 再次尝试URL解码（以防是双重编码）
      try {
        String doubleDecodedUrl = URLDecoder.decode(decodedUrl, StandardCharsets.UTF_8);
        if (!doubleDecodedUrl.equals(decodedUrl)) {
          decodedUrl = doubleDecodedUrl;
        }
      } catch (Exception e) {
        // 保持第一次解码的结果
        log.debug("Double URL decoding failed:{} ", e.getMessage());
      }
      return decodedUrl;
    } catch (Exception e) {
      log.debug("Decoding failed:{} ", e.getMessage());
      return encodedUrl; // 解码失败时返回原始URL
    }
  }

  private String extractJsonFromScript(String scriptText) {
    if (scriptText == null) return null;

    Pattern pattern = Pattern.compile("var\\s+player_aaaa\\s*=\\s*(\\{.*?\\})\\s*;");
    java.util.regex.Matcher matcher = pattern.matcher(scriptText);
    if (matcher.find()) {
      return matcher.group(1);
    }
    // 如果上面的模式没匹配到，尝试另一种模式
    Pattern pattern2 = Pattern.compile("var\\s+player_aaaa\\s*=\\s*(\\{.*?\\})\\s*$");
    java.util.regex.Matcher matcher2 = pattern2.matcher(scriptText);
    if (matcher2.find()) {
      return matcher2.group(1);
    }
    return null;
  }

}
