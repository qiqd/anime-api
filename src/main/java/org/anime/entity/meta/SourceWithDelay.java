package org.anime.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.anime.parser.HtmlParser;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceWithDelay implements Serializable {
  private Integer delay;
  private HtmlParser htmlParser;
}
