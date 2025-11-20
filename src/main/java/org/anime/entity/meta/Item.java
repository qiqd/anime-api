package org.anime.entity.meta;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {
  /**
   * 分类
   */
  @JSONField(name = "abstract")
  private String abstract_;
  /**
   * staff
   */
  private String abstract_2;
  private String cover_url;
  /**
   * 唯一id
   */
  private Long id;
  private String more_url;
  private Rating rating;
  private String title;
  /**
   * 直接链接
   */
  private String url;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Rating {
    /**
     * 评价人数
     */
    private Integer count;
    private String rating_info;
    private Double star_count;
    /**
     * 评价值
     */
    private Double value;
  }
}


