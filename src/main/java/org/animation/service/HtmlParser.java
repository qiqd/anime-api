package org.animation.service;

import org.animation.entity.Animation;
import org.animation.entity.AnimationDetail;
import org.animation.entity.PlayInfo;
import org.animation.entity.Schedule;

import java.io.Serializable;
import java.util.List;

/**
 * HTML解析接口规范
 */
public interface HtmlParser extends Serializable {

  /**
   * 名称
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
   * 解析视频搜索结果
   *
   * @param keyword 搜索关键词
   * @param page    页码
   * @param size    每页数量
   * @return List<Anime>
   * @throws Exception 解析异常
   */
  List<Animation> fetchSearchSync(String keyword, Integer page, Integer size) throws Exception;

  /**
   * 解析视频详情信息
   *
   * @param videoId 视频ID
   * @return AnimeDetail
   * @throws Exception 解析异常
   */
  AnimationDetail fetchDetailSync(String videoId) throws Exception;

  /**
   * 解析播放信息
   *
   * @param episodeId 剧集id
   * @return PlayInfo
   * @throws Exception 解析异常
   */
  PlayInfo fetchPlayInfoSync(String episodeId) throws Exception;

  /**
   * 解析推荐视频
   *
   * @param html HTML内容
   * @return 推荐视频列表
   * @throws Exception 解析异常
   */
  String fetchRecommendSync(String html) throws Exception;

  /**
   * 获取每周更新时间表
   *
   * @return Schedule列表
   * @throws Exception 解析异常
   */
  List<Schedule> fetchWeeklySync() throws Exception;
}
