package org.animation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Source implements Serializable {
  private Integer index;
  private String name;
  private List<Episode> episodes;
}
