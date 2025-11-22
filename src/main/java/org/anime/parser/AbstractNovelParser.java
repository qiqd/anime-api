package org.anime.parser;

import org.anime.entity.base.Detail;
import org.anime.entity.base.ExceptionHandler;
import org.anime.entity.base.ViewInfo;
import org.anime.entity.novel.Novel;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractNovelParser implements HtmlParser, Serializable {
  @Override
  public abstract List<Novel> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler);

  @Nullable
  @Override
  public abstract Detail<Novel> fetchDetailSync(String mediaId, ExceptionHandler exceptionHandler);

  @Nullable
  @Override
  public abstract ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler);
}
