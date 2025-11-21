package org.anime.entity.animation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.anime.entity.base.Media;

import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Animation extends Media implements Serializable {

  /**
   * 子ID
   */
  private String subId;


  /**
   * 导演信息
   */
  private String director;

  /**
   * 主演信息
   */
  private String actor;

  /**
   * 首播日期
   */
  private String ariDate;

  /**
   * 评分人数
   */
  private String ratingCount;

  /**
   * 更新时间
   */
  private String lastUpdateAt;

  /**
   * 总集数
   */
  private Integer totalEpisode;


  /**
   * 国家/地区
   */
  private String country;


  /**
   * 角色列表
   */
  private String role;
  /**
   * 语言
   */
  private String language;
  /**
   * 编剧
   */
  private String screenwriter;
  /**
   * 片长
   */
  private String duration;

}
