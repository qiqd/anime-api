package org.anime.parser.impl.animation;

import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.base.Detail;
import org.anime.entity.base.ExceptionHandler;
import org.anime.entity.base.Source;
import org.anime.entity.base.ViewInfo;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.AbstractAnimationParser;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Mwcy extends AbstractAnimationParser implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(Mwcy.class);

  public static final String NAME = "喵物次元";
  public static final String LOGO_URL = "https://www.mwcy.net/template/dsn2/static/img/logo1.png";
  public static final String BASE_URL = "https://www.mwcy.net";
  private final static String DEFAULT_PARSE_URL = "https://play.catw.moe/player/ec.php?code=qw&if=1&url=";

  private Animation fillAnimation(String titleCn, String covet, String id, String director, String actor, String genre) {
    Animation animation = new Animation();
    animation.setSubId(id);
    animation.setTitleCn(titleCn);
    animation.setCoverUrls(Collections.singletonList(covet));
    animation.setDirector(director);
    animation.setActor(actor);
    animation.setGenre(genre);
    return animation;
  }

  private Animation apply(Element item) {
    Elements coverImg = item.select("div.detail-pic img");
    String titleCn = coverImg.attr("alt");
    String covet = coverImg.attr("data-src");
    Elements infoBox = item.select("div.detail-info.rel.flex-auto.lightSpeedIn");
    String id = infoBox.select("a").attr("href");
    Elements infoItem = infoBox.select("div.slide-info.hide");
    String status = !infoItem.isEmpty() ? infoItem.get(0).text() : "";
    String director = infoItem.size() > 1 ? infoItem.get(1).text().substring(4) : "";
    String actor = infoItem.size() > 2 ? infoItem.get(2).text().substring(4) : "";
    String genre = infoItem.size() > 3 ? infoItem.get(3).text().substring(4) : "";

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
  public List<Animation> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler) {
    //https://www.mwcy.net/search/wd/JOJO.html
    String searchUrl = "/search/wd/" + StringUtil.removeBlank(keyword) + ".html";
    try {
      Element doc = HttpUtil.createConnection(BASE_URL + searchUrl).get().body();
      Elements elements = doc.select("div.vod-detail.style-detail.cor4.search-list");
      return elements.isEmpty() ? Collections.emptyList() : elements.stream().map(this::apply).collect(Collectors.toList());
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return Collections.emptyList();
    }
  }

  @Override
  @Nullable
  public Detail<Animation> fetchDetailSync(String videoId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASE_URL + videoId).get().body();
      Elements elements = doc.select("div.anthology-list-box.none");
      if (elements.isEmpty()) {
        log.error("未找到视频源, videoId: {}", videoId);
        return null;
      }
      List<Source> sources = elements.stream().map(SenFun::parseSource).collect(Collectors.toList());
      Animation animation = this.apply(doc);
      String rating = doc.select("div.fraction").text();
      String totalRating = doc.select("span.text-site.cor2").text();
      String introduction = doc.select("div#height_limit").text();
      animation.setRating(rating);
      animation.setRatingCount(totalRating);
      animation.setDescription(introduction);
      return new Detail<>(animation, null, sources);
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }

  }

  @Nullable
  @Override
  public ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASE_URL + episodeId).get().body();
      List<Element> playerScript = doc.select("script").stream().filter(item -> item.data().contains("var player_aaaa")).collect(Collectors.toList());
      if (playerScript.isEmpty()) {
        log.error("未找到player_aaaa脚本, episodeId: {}", episodeId);
        return null;
      }
      String target = playerScript.get(0).data();
      // 提取url
      Pattern urlPattern = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");
      Matcher urlMatcher = urlPattern.matcher(target);
      if (!urlMatcher.find()) {
        log.error("未找到url, episodeId: {}", episodeId);
        return null;
      }
      String fadeUrl = urlMatcher.group(1);
      String parseUrl = DEFAULT_PARSE_URL + fadeUrl;
      Element body = Jsoup.connect(parseUrl).get().body();
      List<Element> configScript = body.select("script").stream().filter(item -> item.data().contains("let ConFig")).collect(Collectors.toList());
      String playerData = configScript.get(0).data();
      // 提取ConFig.url
      Pattern realUrlPattern = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");
      Matcher realUrlMatcher = realUrlPattern.matcher(playerData);
      if (!realUrlMatcher.find()) {
        log.error("未找到realUrl, episodeId: {}", episodeId);
        return null;
      }
      String url = realUrlMatcher.group(1);
      // 提取ConFig["config"]["uid"]
      Pattern uidPattern = Pattern.compile("\"uid\"\\s*:\\s*\"([^\"]+)\"");
      Matcher matcher = uidPattern.matcher(playerData);
      if (!matcher.find()) {
        log.error("未找到uid, episodeId: {}", episodeId);
        return null;
      }
      String uid = matcher.group(1);
      url = url.replaceAll("\\\\/", "/");
      String s = this.decryptVideoUrl(url, uid);
      return new ViewInfo(null, episodeId, Collections.singletonList(s));
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