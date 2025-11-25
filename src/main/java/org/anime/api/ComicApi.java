package org.anime.api;

import org.anime.entity.meta.SourceWithDelay;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.AbstractComicParser;
import org.anime.parser.impl.comic.Baozi;
import org.anime.util.HttpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ComicApi implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(ComicApi.class);

  public static final ArrayList<SourceWithDelay<AbstractComicParser>> SOURCES_WITH_DELAY = new ArrayList<>();
  public static final HashMap<String, AbstractComicParser> SOURCE_MAP = new HashMap<>();

  static {
    SOURCE_MAP.put(Baozi.NAME, new Baozi());
  }

  public static void moveToTop(int index) {
    if (index < 0 || index >= SOURCES_WITH_DELAY.size()) {
      return;
    }
    SourceWithDelay<AbstractComicParser> sourceWithDelay = SOURCES_WITH_DELAY.remove(index);
    SOURCES_WITH_DELAY.add(0, sourceWithDelay);
  }

  public static void initialization() {
    HttpUtil.delayTestSync(SOURCES_WITH_DELAY, SOURCE_MAP);
  }
}
