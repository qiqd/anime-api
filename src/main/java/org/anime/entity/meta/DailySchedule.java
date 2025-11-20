package org.anime.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 每日放送实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySchedule implements Serializable {
  /**
   * 星期信息
   */
  private Weekday weekday;

  /**
   * 条目列表
   */
  private List<Item> items;

  /**
   * 星期信息实体类
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Weekday implements Serializable {
    /**
     * 英文星期名称
     */
    private String en;

    /**
     * 中文星期名称
     */
    private String cn;

    /**
     * 日文星期名称
     */
    private String ja;

    /**
     * 星期ID (1-7)
     */
    private Integer id;

    // 无参构造函数

  }

  /**
   * 条目信息实体类
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Item implements Serializable {
    /**
     * 条目ID
     */
    private Integer id;

    /**
     * 条目URL
     */
    private String url;

    /**
     * 条目类型
     */
    private Integer type;

    /**
     * 条目名称
     */
    private String name;

    /**
     * 条目中文名称
     */
    private String name_cn;

    /**
     * 条目简介
     */
    private String summary;

    /**
     * 放送日期
     */
    private String air_date;

    /**
     * 放送星期
     */
    private Integer air_weekday;

    /**
     * 评分信息
     */
    private Rating rating;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 图片信息
     */
    private Images images;

    /**
     * 评分信息实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rating implements Serializable {
      /**
       * 平均分
       */
      private Double score;

      // 无参构造函数

    }

    /**
     * 图片信息实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Images implements Serializable {
      /**
       * 大图链接
       */
      private String large;

      /**
       * 通用尺寸图片链接
       */
      private String common;

      /**
       * 中等尺寸图片链接
       */
      private String medium;

      /**
       * 小图链接
       */
      private String small;

      /**
       * 网格图链接
       */
      private String grid;
    }
  }
}
