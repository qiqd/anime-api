package org.anime.entity.animation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.anime.entity.base.Media;
import org.anime.entity.enumeration.StaffType;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Staff implements Serializable {
  private String mediaId;
  private List<Person> directors;
  private List<Person> actors;
  private List<Person> producers;
  private List<Person> writers;
  private List<Person> musicians;
  private List<Person> animators;


  @Data
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Person implements Serializable {
    private String nameCn;
    private List<String> otherNames;
    private StaffType type;
    private String role;
    private String url;
    private List<String> imageUrl;
    private String birthDate;
    /**
     * 性别，0女1男
     */
    private Integer gender;
    private String introduction;
    /**
     * 最新作品列表
     */
    private List<Media> latestWorks;
    /**
     * 收藏人数最多的作品列表
     */
    private List<Media> mostFavoriteWorks;
  }
}
