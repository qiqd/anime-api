package org.anime.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingInfo implements Serializable {
  private String rating;
  private String count;
  private String oneStar;
  private String twoStar;
  private String threeStar;
  private String fourStar;
  private String fiveStar;
}
