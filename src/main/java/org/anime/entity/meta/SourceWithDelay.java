package org.anime.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.anime.parser.HtmlParser;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceWithDelay<T extends HtmlParser> implements Serializable {
  private Integer delay;
  private T htmlParser;
}
