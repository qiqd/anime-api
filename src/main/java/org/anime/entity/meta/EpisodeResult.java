package org.anime.entity.meta;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 剧集搜索结果实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeResult implements Serializable {
  /**
   * 剧集列表
   */
  private List<Episode> data;

  /**
   * 总剧集数
   */
  private Integer total;

  /**
   * 每页剧集数
   */
  private Integer limit;

  /**
   * 偏移量
   */
  private Integer offset;


  /**
   * 剧集信息实体类
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Episode implements Serializable {
    /**
     * 放送日期
     */
    private String air_date;

    /**
     * 剧集名称
     */
    private String name;

    /**
     * 剧集中文名称
     */
    private String name_cn;

    /**
     * 时长
     */
    private String duration;

    /**
     * 简介
     */
    private String desc;

    /**
     * 剧集内的集数，从1开始
     */
    private Integer ep;

    /**
     * 同类条目的排序和集数
     */
    private Double sort;

    /**
     * 章节ID
     */
    private Integer id;

    /**
     * 条目ID
     */
    private Integer subject_id;

    /**
     * 回复数量
     */
    private Integer comment;

    /**
     * 章节类型
     */
    private Integer type;

    /**
     * 音乐曲目的碟片数
     */
    private Integer disc;

    /**
     * 服务器解析的时长，单位秒
     */
    private Integer duration_seconds;
  }
}
