package org.anime.parser.impl.meta;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.animation.Staff;
import org.anime.entity.base.Detail;
import org.anime.entity.base.Media;
import org.anime.entity.base.ViewInfo;
import org.anime.entity.enumeration.StaffType;
import org.anime.entity.meta.Item;
import org.anime.parser.AbstractAnimationParser;
import org.anime.parser.HtmlParser;
import org.anime.util.HttpUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Douban extends AbstractAnimationParser implements HtmlParser, Serializable {
  private static final String NAME = "豆瓣";
  private static final String BASE_URL = "https://movie.douban.com";
  private static final String LOGO_URL = "https://movie.douban.com";

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

  public Staff fetchStaffSync(String mediaId) throws Exception {
    //https://movie.douban.com/subject/35936775/celebrities
    String fullUrl = BASE_URL + "/subject/" + mediaId + "/celebrities";
    Element body = HttpUtil.createConnection(fullUrl).get().body();

    List<Staff.Person> personList = body.select("li.celebrity").stream().map(item -> {
      String style = item.select("div.avatar").attr("style");
      String cover = style.substring(style.indexOf("(") + 1, style.lastIndexOf(")"));
      String nameCn = item.select("span.name").text();
      String role = item.select("span.role").text();
      StaffType type = getStaffType(role);
      List<Media> works = item.select("span.works a").stream().map(i -> Media.builder().title(i.text()).build()).collect(Collectors.toList());
      return Staff.Person.builder()
              .nameCn(nameCn)
              .role(role)
              .type(type)
              .url(cover)
              .latestWorks(works)
              .imageUrl(Collections.singletonList(cover))
              .build();
    }).collect(Collectors.toList());
    Map<StaffType, List<Staff.Person>> map = personList.stream().collect(Collectors.groupingBy(Staff.Person::getType));
    return Staff.builder()
            .mediaId(mediaId)
            .actors(map.get(StaffType.ACTOR))
            .animators(map.get(StaffType.ANIMATOR))
            .directors(map.get(StaffType.DIRECTOR))
            .producers(map.get(StaffType.PRODUCER))
            .musicians(map.get(StaffType.MUSICIAN))
            .writers(map.get(StaffType.WRITER))
            .build();
  }

  @Nullable
  private static StaffType getStaffType(String role) {
    StaffType type = null;
    if (role.contains("Director")) {
      type = StaffType.DIRECTOR;
    }
    if (role.contains("Voice")) {
      type = StaffType.ACTOR;
    }
    if (role.contains("Writer") || role.contains("原著作者")) {
      type = StaffType.WRITER;
    }
    if (role.contains("Music") || role.contains("作曲") || role.contains("作词")) {
      type = StaffType.MUSICIAN;
    }
    if (role.contains("Animator")) {
      type = StaffType.ANIMATOR;
    }
    if (role.contains("Producer")) {
      type = StaffType.PRODUCER;
    }
    return type;
  }

  @Override
  public List<Animation> fetchSearchSync(String keyword, Integer page, Integer size) throws Exception {
    //https://search.douban.com/movie/subject_search?search_text=%E3%80%90%E6%88%91%E6%8E%A8%E7%9A%84%E5%AD%A9%E5%AD%90%E3%80%91
    String fullUrl = "https://search.douban.com/movie/subject_search?search_text=" + keyword;
    Element body = HttpUtil.createConnection(fullUrl).get().body();
    List<Element> data = body.getElementsByTag("script").stream().filter(script -> script.data().contains("window.__DATA__")).collect(Collectors.toList());
    String script = data.get(0).data();
    script = script.substring(script.indexOf("["), script.lastIndexOf("]") + 1);
    List<Item> items = JSON.parseObject(script, new TypeReference<List<Item>>() {
    });
    return items.stream().map(item -> Animation.builder()
            .id(item.getId().toString())
            .title(item.getTitle())
            .coverUrls(Collections.singletonList(item.getCover_url()))
            .rating(item.getRating().getValue().toString())
            .ratingCount(item.getRating().getCount().toString())
            .genre(item.getAbstract_().replaceAll(" / ", ","))
            .actor(item.getAbstract_2().replaceAll(" / ", ","))
            .build()).collect(Collectors.toList());
  }

  @Nullable
  @Override
  public Detail<Animation> fetchDetailSync(String mediaId) throws Exception {
    String fullUrl = BASE_URL + "/subject/" + mediaId;
    Element body = HttpUtil.createConnection(fullUrl).get().body();
    String titleCn = body.select("span[property=\"v:itemreviewed\"]").text();
    String cover = body.select("a.nbgnbg img").attr("src");
    String genre = String.join(",", body.select("span[property=\"v:genre\"]").eachText());
    Elements span = body.select("span.pl");
    String releaseDate = body.select("span[property=\"v:initialReleaseDate\"]").text();
    String country = Optional.ofNullable(span.get(4).nextSibling()).orElse(new Element("div")).toString();
    String language = Optional.ofNullable(span.get(5).nextSibling()).orElse(new Element("div")).toString();
    String totalEpisode = Optional.ofNullable(span.get(7).nextSibling()).orElse(new Element("div")).toString();
    String duration = Optional.ofNullable(span.get(8).nextSibling()).orElse(new Element("div")).toString();
    String otherName = Optional.ofNullable(span.get(8).nextSibling()).orElse(new Element("div")).toString();
    String rating = body.select("strong[property=\"v:average\"]").text();
    String ratingCount = body.select("strong[property=\"v:votes\"]").text();
    String summary = body.select("strong[property=\"v:summary\"]").html().replaceAll("<br/>", "\r\n");
    List<Element> scripts = body.getElementsByTag("script").stream().filter(script -> script.data().contains("SERIES_OTHER_SUBJECTS")).collect(Collectors.toList());
    Animation animation = Animation.builder()
            .id(mediaId)
            .coverUrls(Collections.singletonList(cover))
            .titleCn(titleCn)
            .title(otherName)
            .genre(genre)
            .status("")
            .rating(rating)
            .ratingCount(ratingCount)
            .description(summary)
            .country(country)
            .language(language)
            .totalEpisode(Integer.parseInt(totalEpisode.trim()))
            .duration(duration)
            .ariDate(releaseDate)
            .build();
    Detail<Animation> detail = new Detail<>();
    detail.setMedia(animation);
    Pattern pattern = Pattern.compile("SERIES_OTHER_SUBJECTS\\s*:\\s*(\\[[\\s\\S]*?]),");
    Matcher matcher = pattern.matcher(scripts.get(0).data());
    if (matcher.find()) {
      String seriesArray = matcher.group(1);
      List<Map<String, String>> series = JSON.parseObject(seriesArray, new TypeReference<List<Map<String, String>>>() {
      });
      List<Animation> animations = series.stream().map(item -> Animation.builder()
              .id(item.get("id"))
              .rating(item.get("rating"))
              .titleCn(item.get("title"))
              .releaseDate(item.get("year"))
              .coverUrls(Collections.singletonList(item.get("pic")))
              .build()).collect(Collectors.toList());
      detail.setSeries(animations);
    }
    return detail;
  }

  @Nullable
  @Override
  public ViewInfo fetchViewSync(String episodeId) throws Exception {
    return null;
  }

  @Override
  public String fetchRecommendSync(String html) throws Exception {
    return "";
  }

  @Override
  public List<Schedule> fetchWeeklySync() throws Exception {
    return Collections.emptyList();
  }
}
