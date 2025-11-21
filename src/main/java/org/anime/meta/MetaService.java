package org.anime.meta;

import org.anime.entity.animation.Animation;
import org.anime.entity.animation.Schedule;
import org.anime.entity.meta.EpisodeResult;

import java.io.Serializable;
import java.util.List;

public interface MetaService extends Serializable {
  /**
   * 网站名称
   */
  String getName();

  /**
   * 网站logo地址
   */
  String getLogoUrl();

  /**
   * 网站地址
   */
  String getBaseUrl();

  /**
   * 搜索
   *
   * @param keyword 关键字
   * @param page    分页
   * @param size    分页大小
   * @return SubjectSearch
   */
  List<Animation> fetchSubjectSearchSync(String keyword, Integer page, Integer size) throws Exception;

  /**
   * 获取条目信息
   *
   * @param subjectId 条目ID
   * @return 条目信息
   */
  Animation fetchSubjectSync(Integer subjectId) throws Exception;

  /**
   * 每日推荐
   *
   * @return 每日推荐
   */
  String fetchDailyRecommendSync() throws Exception;

  /**
   * 每周更新
   *
   * @return 每周更新
   */
  List<Schedule> fetchWeeklyUpdateSync() throws Exception;

  /**
   * 获取剧集信息
   *
   * @param subjectId 剧集ID
   * @return 剧集信息
   */
  EpisodeResult fetchEpisodeSync(String subjectId) throws Exception;


}
