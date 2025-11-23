package org.anime.parser;

import org.anime.entity.base.Detail;
import org.anime.entity.base.ExceptionHandler;
import org.anime.entity.base.ViewInfo;
import org.anime.entity.comic.Comic;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractComicParser implements HtmlParser, Serializable {
  @Override
  public abstract List<Comic> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler);


  @Override
  public abstract Detail<Comic> fetchDetailSync(String mediaId, ExceptionHandler exceptionHandler);

  @Override
  public abstract ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler);
}
