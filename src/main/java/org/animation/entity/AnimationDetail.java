package org.animation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimationDetail implements Serializable {
  private Animation animation;
  private List<Source> sources;
}
