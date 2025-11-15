package org.anime.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.anime.util.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 媒体基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Media implements Serializable {
  /**
   * 媒体id
   */
  private String id;
  /**
   * 媒体标题
   */
  private String title;
  /**
   * 媒体标题（中文）
   */
  private String titleCn;
  /**
   * 媒体标题（英文）
   */
  private String titleEn;
  /**
   * 作者
   */
  private String author;
  /**
   * 封面图片URL列表
   */
  private List<String> coverUrls;
  /**
   * 描述
   */
  private String description;
  /**
   * 流派
   */
  private String genre;
  /**
   * 状态
   */
  private String status;
  /**
   * 评分
   */
  private String rating;
  /**
   * 观看次数
   */
  private String views;
  /**
   * 来源网站
   */
  private String sourceSite;
  /**
   * 来源网站URL
   */
  private String sourceUrl;
  /**
   * 播放平台
   */
  private String platform;

  /**
   * 发行时间
   */
  private String releaseDate;

  public String getGenre() {
    return StringUtil.removeUnusedChar(genre);
  }
}