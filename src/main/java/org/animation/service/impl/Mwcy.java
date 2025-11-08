package org.animation.service.impl;

import org.animation.entity.*;
import org.animation.service.HtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mwcy implements HtmlParser, Serializable {
  private final static String NAME = "喵物次元";
  private final static String LOGO_URL = "https://www.mwcy.net/template/dsn2/static/img/logo1.png";
  private final static String BASE_URL = "https://www.mwcy.net";
  private final static String DEFAULT_PARSE_URL = "https://play.catw.moe/player/ec.php?code=qw&if=1&url=";

  private Animation fillAnimation(String titleCn, String covet, String id, String director, String actor, String genre) {
    Animation animation = new Animation();
    animation.setId(id);
    animation.setTitleCn(titleCn);
    animation.setCoverUrls(List.of(covet));
    animation.setDirectors(List.of(director));
    animation.setActors(List.of(actor));
    animation.setGenres(List.of(genre));
    return animation;
  }

  private Animation apply(Element item) {
    Elements coverImg = item.select("div.detail-pic img");
    String titleCn = coverImg.attr("alt");
    String covet = coverImg.attr("data-src");
    Elements infoBox = item.select("div.detail-info.rel.flex-auto.lightSpeedIn");
    String id = infoBox.select("a").attr("href");
    Elements infoItem = infoBox.select("div.slide-info.hide");
    String status = infoItem.get(0).text();
    String director = infoItem.get(1).text();
    String actor = infoItem.get(2).text();
    String genre = infoItem.get(3).text();
    Animation animation = fillAnimation(titleCn, covet, id, director, actor, genre);
    animation.setStatus(status);
    return animation;
  }

  public String decryptVideoUrl(String encryptedData, String uid) throws Exception {
    // 1. 构建密钥
    String key = "2890" + uid + "tB959C";
    // 2. 固定向量
    String iv = "2F131BE91247866E";
    byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
    // 3. AES解密
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
    byte[] decrypted = cipher.doFinal(encryptedBytes);
    return new String(decrypted, StandardCharsets.UTF_8);
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
  public List<Animation> fetchSearchSync(String keyword, Integer page, Integer size) throws Exception {
    //https://www.mwcy.net/search/wd/JOJO.html
    String searchUrl = "/search/wd/" + keyword + ".html";
    Element doc = Jsoup.connect(BASE_URL + searchUrl).get().body();
    return doc.select("div.vod-detail.style-detail.cor4.search-list").stream().map(this::apply).toList();
  }

  @Override
  public AnimationDetail fetchDetailSync(String videoId) throws Exception {
    Element doc = Jsoup.connect(BASE_URL + videoId).get().body();
    List<Source> sources = doc.select("div.anthology-list-box.none").stream().map(item -> {
      Source source = new Source();
      List<Episode> episodes = item.select("a").stream().map(a -> {
        Episode episode = new Episode();
        episode.setId(a.attr("href"));
        episode.setTitle(a.text());
        return episode;
      }).toList();
      source.setEpisodes(episodes);
      return source;
    }).toList();

    Animation animation = this.apply(doc);
    String rating = doc.select("div.fraction").text();
    String totalRating = doc.select("span.text-site.cor2").text();
    String introduction = doc.select("div#height_limit").text();
    animation.setRating(rating);
    animation.setRatingCount(totalRating);
    animation.setDescription(introduction);
    return new AnimationDetail(animation, sources);
  }


  @Override
  public PlayInfo fetchPlayInfoSync(String episodeId) throws Exception {
    Element doc = Jsoup.connect(BASE_URL + episodeId).get().body();
    List<Element> playerScript = doc.select("script").stream().filter(item -> item.data().contains("var player_aaaa")).toList();
    String target = playerScript.get(0).data();
    // 提取url
    Pattern urlPattern = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");
    Matcher urlMatcher = urlPattern.matcher(target);
    String fadeUrl = null;
    if (urlMatcher.find()) {
      fadeUrl = urlMatcher.group(1);
    }
    String parseUrl = DEFAULT_PARSE_URL + fadeUrl;
    Element body = Jsoup.connect(parseUrl).get().body();
    List<Element> configScript = body.select("script").stream().filter(item -> item.data().contains("let ConFig")).toList();
    String playerData = configScript.get(0).data();
    // 提取ConFig.url
    Pattern realUrlPattern = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");
    Matcher realUrlMatcher = realUrlPattern.matcher(playerData);
    String url = "";
    if (realUrlMatcher.find()) {
      url = realUrlMatcher.group(1);
    }
    // 提取ConFig["config"]["uid"]
    Pattern uidPattern = Pattern.compile("\"uid\"\\s*:\\s*\"([^\"]+)\"");
    Matcher matcher = uidPattern.matcher(playerData);
    String uid = "";
    if (matcher.find()) {
      uid = matcher.group(1);
    }
    url = url.replaceAll("\\\\/", "/");
    String s = this.decryptVideoUrl(url, uid);
    return new PlayInfo(episodeId, s);
  }

  @Override
  public String fetchRecommendSync(String html) throws Exception {
    return "";
  }

  @Override
  public List<Schedule> fetchWeeklySync() throws Exception {
    return List.of();
  }


}
