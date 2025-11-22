package org.anime.parser;

import org.anime.entity.animation.Animation;
import org.anime.entity.base.Detail;
import org.anime.entity.base.ExceptionHandler;
import org.anime.entity.base.ViewInfo;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractAnimationParser implements HtmlParser, Serializable {
  @Override
  public abstract List<Animation> fetchSearchSync(String keyword, Integer page, Integer size, ExceptionHandler exceptionHandler);

  @Nullable
  @Override
  public abstract Detail<Animation> fetchDetailSync(String mediaId, ExceptionHandler exceptionHandler);

  @Nullable
  @Override
  public abstract ViewInfo fetchViewSync(String episodeId, ExceptionHandler exceptionHandler);
}
