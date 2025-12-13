package org.anime.parser.impl.animation;

import com.alibaba.fastjson.JSON;
import org.anime.entity.animation.Animation;
import org.anime.entity.animation.PlayerData;
import org.anime.entity.animation.Schedule;
import org.anime.entity.base.*;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.HtmlParser;
import org.anime.util.HttpUtil;
import org.anime.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class AAFun implements HtmlParser, Serializable {
  private static final Logger log = LoggerFactory.getLogger(AAFun.class);
  public static final String NAME = "风铃动漫";
  public static final String LOGOURL = "https://p.upyun.com/demo/tmp/Hds66ovM.png";
  public static final String BASEURL = "https://www.aafun.cc";

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
  public List<Media> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler) {
    try {
      String searchUrl = "/feng-s.html?wd=" + StringUtil.removeBlank(keyword);
      Element document = HttpUtil.createConnection(BASEURL + searchUrl).get().body();
      Elements li = document.select("div.hl-list-wrap li.hl-list-item");
      return li.stream().map(item -> {
        String genre = item.select("p.hl-item-sub.hl-lc-1").text();
        String actor = item.select("p.hl-item-sub.hl-text-muted.hl-lc-1.hl-hidden-xs").text();
        String introduction = item.select("p.hl-item-sub.hl-text-muted.hl-lc-2").text();
        Elements a = item.select("div.hl-item-div a");
        String status = item.select("span.hl-lc-1.remarks").text();
        Animation animation = new Animation();
        animation.setId(a.attr("href"));
        animation.setTitleCn(a.attr("title"));
        animation.setCoverUrls(Collections.singletonList(a.attr("data-original")));
        animation.setStatus(status);
        animation.setDescription(introduction);
        animation.setGenre(StringUtil.removeUnusedChar(genre));
        animation.setActor(StringUtil.removeUnusedChar(actor));
        return animation;
      }).collect(Collectors.toList());
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return Collections.emptyList();
    }
  }

  @Override
  @Nullable
  public Detail fetchDetailSync(String videoId, ExceptionHandler exceptionHandler) {
    try {
      Element document = HttpUtil.createConnection(BASEURL + videoId).get().body();
      Elements div = document.select("div.hl-tabs-box");
      List<Source> sources = div.stream().map(item -> {
        Elements a = item.select("li.hl-col-xs-4 a");
        List<Episode> episodes = a.stream().map(i -> {
          Episode episode = new Episode();
          episode.setId(i.attr("href"));
          episode.setTitle(i.text());
          return episode;
        }).collect(Collectors.toList());
        return new Source(null, null, episodes);
      }).collect(Collectors.toList());
      Animation animation = new Animation();
      animation.setId(videoId);
      // 解析封面图片
      Elements picElements = document.select(".hl-dc-pic .hl-item-thumb");
      if (!picElements.isEmpty()) {
        Element picElement = picElements.get(0);
        animation.setCoverUrls(Collections.singletonList(Optional.of(picElement.attr("data-original")).orElse("")));
      }

// 解析标题
      Elements titleElements = document.select("span.hl-crumb-item");
      if (!titleElements.isEmpty()) {
        animation.setTitleCn(titleElements.get(0).text());
      }
      Elements subTitleElements = document.select("div.hl-dc-sub");
      if (!subTitleElements.isEmpty()) {
        animation.setTitle(subTitleElements.get(0).text());
      }
      Elements ratingCount = document.select("span.hl-score-data.hl-text-muted.hl-pull-right");
      if (!ratingCount.isEmpty()) {
        animation.setRatingCount(ratingCount.get(0).text());
      }
// 解析状态
      Elements statusElements = document.select(".hl-vod-data .hl-col-xs-12 span.hl-text-conch");
      if (!statusElements.isEmpty()) {
        animation.setStatus(statusElements.get(0).text());
      }

// 解析主演
      Elements actorElements = document.select(".hl-vod-data .hl-col-xs-12").eq(2);
      if (!actorElements.isEmpty()) {
        Elements actor = actorElements.select("a");
        String actors = actor.text();
        animation.setActor(StringUtil.removeUnusedChar(actors));
      }

// 解析导演
      Elements directorElements = document.select(".hl-vod-data .hl-col-xs-12").eq(3);
      if (!directorElements.isEmpty()) {
        Elements directorLinks = directorElements.select("a");
        String directors = directorLinks.text();
        animation.setDirector(StringUtil.removeUnusedChar(directors));
      }

// 解析年份
      Elements infoBox = document.select(".hl-vod-data .hl-col-xs-12.hl-col-sm-4");
      if (!infoBox.isEmpty()) {
        Element yearElement = infoBox.get(0);
        String yearText = yearElement.text();
        String year = yearText.replaceAll("\\D+", "");
        animation.setAriDate(year);
      }

// 解析类型
      if (infoBox.size() > 2) {
        Element typeElement = infoBox.get(2);
        Elements typeLinks = typeElement.select("a");
        String genres = typeLinks.text();
        animation.setGenre(StringUtil.removeUnusedChar(genres));
      }

// 解析上映时间
      if (infoBox.size() > 4) {
        Element releaseElement = infoBox.get(4);
        String releaseText = releaseElement.text();
        animation.setAriDate(releaseText);
      }
// 解析上映时间
      if (infoBox.size() > 5) {
        Element releaseElement = infoBox.get(5);
        String language = releaseElement.text();
        animation.setLanguage(language);
      }

// 解析简介
      Elements descriptionElements = document.select(".hl-vod-data .hl-col-xs-12.blurb");
      if (!descriptionElements.isEmpty()) {
        String description = descriptionElements.get(0).text();
        animation.setDescription(description);
      }

// 解析评分
      Elements ratingElements = document.select(".hl-score-nums span");
      if (!ratingElements.isEmpty()) {
        String rating = ratingElements.get(0).text();
        animation.setRating(rating);
      }
      return new Detail(animation, null, sources);
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  @Nullable
  public ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler) {
    try {
      Element body = HttpUtil.createConnection(BASEURL + episodeId).get().body();
      Elements script = body.select("script[type='text/javascript']");
      if (script.isEmpty()) {
        log.error("No script found for episodeId: {}", episodeId);
        return null;
      }
      Element varPlayerAaaa = script.stream().filter(item -> item.data().contains("var player_aaaa")).collect(Collectors.toList()).get(0);
      String objectString = varPlayerAaaa.data().substring(varPlayerAaaa.data().indexOf("{"));
      PlayerData playerData = JSON.parseObject(objectString, PlayerData.class);
      String decodeUrl = URLDecoder.decode(playerData.getUrl(), StandardCharsets.UTF_8.name());
      String fullFullUrl = BASEURL + "/player/?url=" + decodeUrl;
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Host", BASEURL.substring(BASEURL.lastIndexOf("/") + 1));
      headers.put("Referer", BASEURL + episodeId);
      headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
      Element document = Jsoup.connect(fullFullUrl).headers(headers).get().body();
      Element element = null;
      for (Element el : document.select("script")) {
        if (el.data().contains("const encryptedUrl")) {
          element = el;
          break;
        }
      }
      if (element == null) {
        element = new org.jsoup.nodes.Element("no");
      }
      String scriptContent = element.data();
      String encryptedUrl = null;
      String sessionKey = null;
      if (scriptContent.contains("const encryptedUrl")) {
        java.util.regex.Pattern encryptedUrlPattern = java.util.regex.Pattern.compile("const\\s+encryptedUrl\\s*=\\s*\"([^\"]+)\"");
        java.util.regex.Matcher encryptedUrlMatcher = encryptedUrlPattern.matcher(scriptContent);
        if (encryptedUrlMatcher.find()) {
          encryptedUrl = encryptedUrlMatcher.group(1);
        }
      }
      if (scriptContent.contains("const sessionKey")) {
        java.util.regex.Pattern sessionKeyPattern = java.util.regex.Pattern.compile("const\\s+sessionKey\\s*=\\s*\"([^\"]+)\"");
        java.util.regex.Matcher sessionKeyMatcher = sessionKeyPattern.matcher(scriptContent);
        if (sessionKeyMatcher.find()) {
          sessionKey = sessionKeyMatcher.group(1);
        }
      }
      String videoUrl = decryptAES(encryptedUrl, sessionKey);
      String currentUrl = videoUrl.replaceFirst("http://", "https://");
      return new ViewInfo(null, episodeId, Collections.singletonList(currentUrl));
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

  private String decryptAES(String ciphertext, String key) {
    try {
      byte[] rawBytes = Base64.getDecoder().decode(ciphertext);
      byte[] ivBytes = Arrays.copyOfRange(rawBytes, 0, 16);
      byte[] encryptedBytes = Arrays.copyOfRange(rawBytes, 16, rawBytes.length);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

      byte[] plainText = cipher.doFinal(encryptedBytes);
      return new String(plainText, StandardCharsets.UTF_8);
    } catch (Exception e) {
      System.out.println("URL解密失败: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

}
