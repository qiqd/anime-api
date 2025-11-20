package org.anime.entity.meta;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 条目搜索结果实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectSearch implements Serializable {
  /**
   * 条目列表
   */
  private List<Subject> data;

  /**
   * 总条目数
   */
  private Integer total;

  /**
   * 每页条目数
   */
  private Integer limit;

  /**
   * 每页偏移量
   */
  private Integer offset;


  /**
   * 条目信息实体类
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Subject implements Serializable {
    /**
     * 条目ID
     */
    private Integer id;

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
     * 发行日期
     */
    private String date;

    /**
     * 条目图片信息
     */
    private Images images;

    /**
     * 条目元标签信息
     */
    private List<String> meta_tags;

    /**
     * 条目信息框
     */
    private List<InfoBox> infobox;

    /**
     * 集数
     */
    private Integer eps;

    /**
     * 平台信息（如TV、剧场版等）
     */
    private String platform;

    /**
     * 评分信息
     */
    private Rating rating;

    /**
     * 收藏信息
     */
    private Collection collection;


    /**
     * 图片信息实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Images implements Serializable {
      /**
       * 小图链接
       */
      private String small;

      /**
       * 网格图链接
       */
      private String grid;

      /**
       * 大图链接
       */
      private String large;

      /**
       * 中等尺寸图片链接
       */
      private String medium;

      /**
       * 通用尺寸图片链接
       */
      private String common;


    }

    /**
     * 信息框实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoBox implements Serializable {
      /**
       * 信息键名
       */
      private String key;

      /**
       * 信息值
       */
      private Object value;

    }

    /**
     * 评分信息实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rating implements Serializable {
      /**
       * 排名
       */
      private Integer rank;

      /**
       * 总评分人数
       */
      private Integer total;

      /**
       * 各分数段评分人数
       */
      private ScoreCount count;

      /**
       * 平均分
       */
      private Double score;


      /**
       * 各分数段评分人数实体类
       */
      @Data
      @NoArgsConstructor
      @AllArgsConstructor
      public static class ScoreCount implements Serializable {
        /**
         * 1分人数
         */
        private Integer one;

        /**
         * 2分人数
         */
        private Integer two;

        /**
         * 3分人数
         */
        private Integer three;

        /**
         * 4分人数
         */
        private Integer four;

        /**
         * 5分人数
         */
        private Integer five;

        /**
         * 6分人数
         */
        private Integer six;

        /**
         * 7分人数
         */
        private Integer seven;

        /**
         * 8分人数
         */
        private Integer eight;

        /**
         * 9分人数
         */
        private Integer nine;

        /**
         * 10分人数
         */
        private Integer ten;


      }
    }

    /**
     * 收藏信息实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Collection implements Serializable {
      /**
       * 搁置人数
       */
      private Integer on_hold;

      /**
       * 抛弃人数
       */
      private Integer dropped;

      /**
       * 想看人数
       */
      private Integer wish;

      /**
       * 已看人数
       */
      private Integer collect;

      /**
       * 在看人数
       */
      private Integer doing;

    }
  }
}
