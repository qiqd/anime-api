package org.anime.parser;

import org.anime.entity.animation.Schedule;
import org.anime.entity.base.Detail;
import org.anime.entity.base.ExceptionHandler;
import org.anime.entity.base.Media;
import org.anime.entity.base.ViewInfo;

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
   * 解析搜索结果
   *
   * @param keyword          搜索关键词
   * @param page             页码
   * @param size             每页数量
   * @param exceptionHandler 异常处理器
   * @return List<Anime>
   */

  List<Media> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler);

  /**
   * 解析详情信息
   *
   * @param mediaId          媒体ID
   * @param exceptionHandler 异常处理器
   * @return AnimeDetail
   */
  Detail fetchDetailSync(String mediaId, ExceptionHandler exceptionHandler);

  /**
   * 解析播放信息
   *
   * @param episodeId        剧集id
   * @param exceptionHandler 异常处理器
   * @return PlayInfo
   */
  ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler);

  /**
   * 解析推荐列表
   *
   * @param html             HTML内容
   * @param exceptionHandler 异常处理器
   * @return 推荐视频列表
   */
  String fetchRecommendSync(String html, ExceptionHandler exceptionHandler);

  /**
   * 获取每周更新时间表
   *
   * @param exceptionHandler 异常处理器
   * @return Schedule列表
   */
  List<Schedule> fetchWeeklySync(ExceptionHandler exceptionHandler);


}
