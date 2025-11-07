package org.animation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Animation implements Serializable {
  /**
   * 动画唯一标识符
   */
  private String id;

  /**
   * 子ID，可能用于区分同一动画的不同版本，该字段补充html解析获得
   */
  private Integer subId;

  /**
   * 动画名称（原文）
   */
  private String title;

  /**
   * 动画中文名称
   */
  private String titleCn;

  /**
   * 动画英文名称
   */
  private String titleEn;
  /**
   * 动画描述信息
   */
  private String description;

  /**
   * 导演信息
   */
  private List<String> directors;

  /**
   * 主演信息
   */
  private List<String> actors;

  /**
   * 动画类型/分类
   */
  private List<String> genres;

  /**
   * 首播日期
   */
  private String ariDate;

  /**
   * 评分
   */
  private Double rating;

  /**
   * 动画状态（如连载中、已完结等）
   */
  private String status;

  /**
   * 更新时间
   */
  private String lastUpdateAt;

  /**
   * 总集数
   */
  private Integer totalEpisode;

  /**
   * 播放平台
   */
  private String platform;

  /**
   * 国家/地区
   */
  private String country;

  /**
   * 封面图片URL
   */
  private List<String> coverUrls;

}
