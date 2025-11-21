package org.anime.meta.impl;

import com.alibaba.fastjson.JSON;
import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.meta.DailySchedule;
import org.anime.entity.meta.EpisodeResult;
import org.anime.entity.meta.SubjectSearch;
import org.anime.meta.MetaService;
import org.anime.util.HttpUtil;
import org.jsoup.Connection;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Bangumi implements MetaService, Serializable {
  private static final String baseUrl = "https://api.bgm.tv";

  @Override
  public String getName() {
    return "";
  }

  @Override
  public String getLogoUrl() {
    return "";
  }

  @Override
  public String getBaseUrl() {
    return "";
  }

  /**
   * 搜索
   *
   * @param keyword 关键字
   * @param page    分页
   * @param size    分页大小
   * @return SubjectSearch
   */
  @Override
  public List<Animation> fetchSubjectSearchSync(String keyword, Integer page, Integer size) throws Exception {
    String searchUrl = "/v0/search/subjects";
    HashMap<String, Object> body = new HashMap<>();
    body.put("keyword", keyword);
    body.put("sort", "rank");
    Map<String, Object> filter = new HashMap<>();
    filter.put("type", Collections.singletonList(2));
    body.put("filter", filter);

    // 使用 Jsoup 替代 OkHttpClient
    Connection connection = HttpUtil.createConnection(baseUrl + searchUrl)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .requestBody(JSON.toJSONString(body))
            .method(Connection.Method.POST)
            .ignoreContentType(true);

    String s = connection.execute().body();
    SubjectSearch subjectSearch = JSON.parseObject(s, SubjectSearch.class);
    List<SubjectSearch.Subject> subjects = subjectSearch.getData();
    return subjects.stream().map(item -> {
      Animation anime = new Animation();
      anime.setId(item.getId().toString());
      anime.setSubId(item.getId().toString());
      anime.setTitle(item.getName());
      anime.setTitleCn(item.getName_cn());
      anime.setAriDate(item.getDate());
      anime.setDescription(item.getSummary());
      anime.setGenre(String.join(",", Optional.ofNullable(item.getMeta_tags()).orElse(Collections.emptyList())));
      anime.setTotalEpisode(item.getEps());
      anime.setCoverUrls(Collections.singletonList(item.getImages().getLarge()));
      return anime;
    }).collect(Collectors.toList());
  }


  @Override
  public String fetchDailyRecommendSync() throws IOException {
    return null;
  }

  /**
   * 每周更新
   *
   * @return List<DailySchedule>
   */
  @Override
  public List<Schedule> fetchWeeklyUpdateSync() throws Exception {
    String url = "/calendar";
    // 使用 Jsoup 替代 OkHttpClient
    Connection connection = HttpUtil.createConnection(baseUrl + url)
            .header("Accept", "application/json")
            .method(Connection.Method.GET)
            .ignoreContentType(true);

    String s = connection.execute().body();
    List<DailySchedule> dailySchedules = JSON.parseArray(s, DailySchedule.class);

    return dailySchedules.stream().map(item -> {
      Schedule schedule = new Schedule();
      List<Animation> animeList = item.getItems().stream().map(i -> {
        Animation anime = new Animation();
        anime.setId(i.getId().toString());
        anime.setSubId(i.getId().toString());
        anime.setAriDate(i.getAir_date());
        anime.setTitle(i.getName());
        anime.setTitleCn(i.getName_cn());
        anime.setDescription(i.getSummary());
        anime.setAriDate(i.getAir_date());
        // 添加空值检查
        if (i.getImages() != null) {
          String large = i.getImages().getLarge();
          anime.setCoverUrls(Collections.singletonList(large != null ? large : i.getImages().getCommon()));
        } else {
          anime.setCoverUrls(Collections.singletonList(null)); // 或设置默认图片链接
        }
        return anime;
      }).collect(Collectors.toList());
      schedule.setId(item.getWeekday().getId());
      schedule.setAnimations(animeList);
      return schedule;
    }).collect(Collectors.toList());
  }

  /**
   * 获取剧集
   *
   * @param subjectId 条目id
   * @return EpisodeResult
   */
  @Override
  public EpisodeResult fetchEpisodeSync(String subjectId) throws Exception {
    String url = "/v0/episodes";
    // 构建查询参数
    String fullUrl = baseUrl + url + "?subject_id=" + subjectId;

    // 使用 Jsoup 替代 OkHttpClient
    Connection connection = HttpUtil.createConnection(fullUrl)
            .header("Accept", "application/json")
            .method(Connection.Method.GET)
            .ignoreContentType(true);

    String s = connection.execute().body();
    return JSON.parseObject(s, EpisodeResult.class);
  }

  /**
   * 获取条目详情
   *
   * @param subjectId 条目id
   * @return Anime
   */
  @Override
  public Animation fetchSubjectSync(Integer subjectId) throws Exception {
    String url = "/v0/subjects/" + subjectId;
    // 使用 Jsoup 替代 OkHttpClient
    Connection connection = HttpUtil.createConnection(baseUrl + url)
            .header("Accept", "application/json")
            .method(Connection.Method.GET)
            .ignoreContentType(true);
    String s = connection.execute().body();
    SubjectSearch.Subject subject = JSON.parseObject(s, SubjectSearch.Subject.class);
    Animation anime = new Animation();
    anime.setId(subject.getId().toString());
    anime.setSubId(subject.getId().toString());
    anime.setTitle(subject.getName());
    anime.setTitleCn(subject.getName_cn());
    anime.setPlatform(subject.getPlatform());
    anime.setTotalEpisode(subject.getEps());
    anime.setDescription(subject.getSummary());
    if (subject.getMeta_tags() != null) {
      anime.setGenre(String.join(",", subject.getMeta_tags()));
    }
    anime.setTotalEpisode(subject.getEps());
    if (subject.getImages() != null) {
      anime.setCoverUrls(Collections.singletonList(subject.getImages().getLarge()));
    }
    return anime;
  }
}
