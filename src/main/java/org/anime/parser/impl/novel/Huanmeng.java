package org.anime.parser.impl.novel;

import org.anime.entity.animation.Schedule;
import org.anime.entity.base.*;
import org.anime.entity.novel.Novel;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.AbstractNovelParser;
import org.anime.util.HttpUtil;
import org.anime.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Huanmeng extends AbstractNovelParser implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(Huanmeng.class);
  public static final String NAME = "幻梦轻小说";
  public static final String LOGOURL = "https://www.huanmengacg.com/template/pc/czyrf01/img/logo2.png";
  public static final String BASEURL = "https://www.huanmengacg.com";

  private Novel fillNovel(String mediaId, String cover, String titleCN, String status, String genre, String author, String description) {
    Novel novel = new Novel();
    novel.setId(mediaId);
    novel.setTitleCn(titleCN);
    novel.setGenre(genre);
    novel.setCoverUrls(Collections.singletonList(cover));
    novel.setAuthor(author);
    novel.setDescription(description);
    novel.setStatus(status);
    return novel;
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
  public List<Novel> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler) {
    String searchUrl = "/index.php/book/search?action=search&key=" + keyword;
    try {
      Element doc = HttpUtil.createConnection(BASEURL + searchUrl).get().body();
      return doc.select("div.rankdatacont dl").stream().map(item -> {
        String id = item.select("dt a").attr("href");
        String cover = item.select("dt a img").attr("data-original");
        String titleCN = item.select("dt a img").attr("alt");
        String author = item.select("i.fa.fa-user-circle-o").parents().get(0).ownText();
        String genre = item.select("span.red-lx").text();
        String status = item.select("span.red-lxa").text();
        String description = item.select("p.big-book-info").text();
        Novel novel = fillNovel(id, cover, titleCN, status, StringUtil.removeUnusedChar(genre), author, description);
        novel.setDescription(description);
        return novel;
      }).collect(Collectors.toList());
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return Collections.emptyList();
    }
  }

  @Nullable
  @Override
  public Detail<Novel> fetchDetailSync(String mediaId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASEURL + mediaId).get().body();
      Elements episodeNode = doc.select("div.zhangjie-quanbu.txt-xs li");
      if (episodeNode.isEmpty()) {
        log.error("未找到章节信息, mediaId: {}", mediaId);
        return null;
      }
      List<Episode> episodes = episodeNode.stream().map(item -> {
        Elements a = item.select("a");
        String episodeId = a.attr("href");
        String episodeName = a.text();
        return new Episode(episodeId, episodeName);
      }).collect(Collectors.toList());
      String cover = doc.select("div.pic-img.fl img").attr("src");
      Elements infoBox = doc.select("div.shu-ef.fl");
      String titleCN = infoBox.select("h1.name.fl").text();
      Elements spans = infoBox.select("div.xs-lx span");
      String status = spans.get(0).text();
      String genre = spans.get(1).text();
      String author = infoBox.select("div.zx-zhangjie.zx-zuozhe").get(0).ownText();
      String lastChapter = infoBox.select("div.zx-zhangjie.zx-zuixinzj a").get(0).ownText();
      String description = infoBox.select("div.kan-jianjie").text();
      Novel novel = fillNovel(mediaId, cover, titleCN, status, genre, author, description);
      novel.setLatestChapter(lastChapter);
      return new Detail<>(novel, null, Collections.singletonList(new Source(0, "default", episodes)));
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }


  @Nullable
  @Override
  public ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler) {

    try {
      Element doc = HttpUtil.createConnection(BASEURL + episodeId).get().body();
      Elements title = doc.select("p.style_h1");
      Elements p = doc.select("div#content p");
      if (p.isEmpty()) {
        log.error("未找内容, episodeId: {}", episodeId);
        return null;
      }
      Elements img = p.select("img");
      if (img.isEmpty()) {
        List<String> texts = p.stream().filter(item -> !item.text().isEmpty() && !item.text().contains(BASEURL.replaceFirst("https://", ""))).map(item -> {
          return item.text() + "\r\n";
        }).collect(Collectors.toList());
        return new ViewInfo(title.text(), episodeId, texts);
      }
      List<String> src = img.stream().map(item -> item.attr("src")).collect(Collectors.toList());
      return new ViewInfo(title.text(), episodeId, src);
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
      return "";
    }
  }

  @Override
  public List<Schedule> fetchWeeklySync(ExceptionHandler exceptionHandler) {
    try {
      return Collections.emptyList();
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return Collections.emptyList();
    }
  }
}
