package org.anime.parser.impl.comic;

import org.anime.entity.animation.Schedule;
import org.anime.entity.base.*;
import org.anime.entity.comic.Comic;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.HtmlParser;
import org.anime.util.HttpUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Baozi implements HtmlParser, Serializable {
  private static final Logger log = LoggerFactory.getLogger(Baozi.class);
  public static final String NAME = "包子漫画";
  public static final String LOGOURL = "https://static-tw.baozimhcn.com/static/bzmh/img/favicon-96x96.png";
  public static final String BASEURL = "https://cn.baozimhcn.com";

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
      //https://cn.baozimhcn.com/search?q=%E7%A7%9F%E5%80%9F%E5%A5%B3%E5%8F%8B
      Element doc = HttpUtil.createConnection(BASEURL + "/search?q=" + keyword).get().body();
      Elements result = doc.select("div.comics-card.pure-u-1-2.pure-u-sm-1-2.pure-u-md-1-4.pure-u-lg-1-6");
      return result.stream().map(item -> {
        String genre = String.join(",", item.select("span.tab.text-truncate").eachText());
        Elements infoDiv = item.select("a.comics-card__info");
        String id = infoDiv.attr("href");
        String titleCn = infoDiv.attr("aria-label");
        String author = item.select("small.tags.text-truncate").text();
        String cover = item.select("a.comics-card__poster.text-decoration-none :first-child").attr("src");
        Comic comic = new Comic();
        comic.setId(id);
        comic.setTitleCn(titleCn);
        comic.setAuthor(author);
        comic.setCoverUrls(Collections.singletonList(cover));
        comic.setGenre(genre);
        return comic;
      }).collect(Collectors.toList());
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Nullable
  @Override
  public Detail fetchDetailSync(String comicId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASEURL + comicId).get().body();
      Elements chapter1 = doc.select("div#chapter-items");
      Elements chapter2 = doc.select("div#chapters_other_list");
      if (chapter1.isEmpty() && chapter2.isEmpty()) {
        log.error("No chapter found for comicId: {}", comicId);
        return null;
      }
      List<Episode> result1 = chapter1.select("div.comics-chapters").stream().map(item -> {
        String id = item.select("a").attr("href");
        String title = item.select("a span").text();
        return new Episode(id, title);
      }).collect(Collectors.toList());
      List<Episode> result2 = chapter2.select("div.comics-chapters").stream().map(item -> {
        String id = item.select("a").attr("href");
        String title = item.select("a span").text();
        return new Episode(id, title);
      }).collect(Collectors.toList());
      result1.addAll(result2);
      Elements infoBox = doc.select("div.pure-g.de-info__box");
      String cover = infoBox.select("div.pure-u-1-1.pure-u-sm-1-3.pure-u-md-1-6 > amp-img").attr("src");
      String title = infoBox.select("h1.comics-detail__title").text();
      String author = infoBox.select("h2.comics-detail__author").text();
      String description = infoBox.select("p.comics-detail__desc").text();
      String genre = infoBox.select("div.tag-list").text();
      String status = infoBox.select("div.supporting-text.mt-2 > div:last-child").text();
      Comic comic = new Comic();
      comic.setId(comicId);
      comic.setTitleCn(title);
      comic.setAuthor(author);
      comic.setDescription(description);
      comic.setGenre(genre);
      comic.setStatus(status);
      comic.setCoverUrls(Collections.singletonList(cover));
      return new Detail(comic, null, Collections.singletonList(new Source(0, "默认", result1)));
    } catch (Exception e) {
      exceptionHandler.handle(e);
      return null;
    }
  }

  @Override
  @Nullable
  public ViewInfo fetchViewSync(String chapterId, ExceptionHandler exceptionHandler) {
    try {
      Element doc = HttpUtil.createConnection(BASEURL + chapterId).get().body();
      Elements comicPages = doc.select("ul.comic-contain div");
      String title = doc.select("div.header span.title").text();
      List<String> urls = comicPages.stream().skip(1).filter(item -> item.html().contains("amp-img")).map(page -> {
        Elements image = page.select("noscript img");
        //      title = image.attr("alt");
        return image.attr("src");
      }).collect(Collectors.toList());
      return new ViewInfo(title, chapterId, urls);
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
