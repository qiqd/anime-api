package org.anime.api;

import org.anime.entity.meta.SourceWithDelay;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.HtmlParser;
import org.anime.parser.impl.animation.*;
import org.anime.util.HttpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationApi implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(AnimationApi.class);

  public static final List<SourceWithDelay> SOURCES_WITH_DELAY = new ArrayList<>();
  public static final Map<String, HtmlParser> SOURCE_MAP = new HashMap<>();

  static {
    SOURCE_MAP.put(AAFun.NAME, new AAFun());
    SOURCE_MAP.put(AiyiFan.NAME, new AiyiFan());
    SOURCE_MAP.put(GirigiriLove.NAME, new GirigiriLove());
    SOURCE_MAP.put(MengDao.NAME, new MengDao());
    SOURCE_MAP.put(Mwcy.NAME, new Mwcy());
    SOURCE_MAP.put(SenFun.NAME, new SenFun());
  }

  public static void moveToTop(int index) {
    if (index < 0 || index >= SOURCES_WITH_DELAY.size()) {
      return;
    }
    SourceWithDelay sourceWithDelay = SOURCES_WITH_DELAY.remove(index);
    SOURCES_WITH_DELAY.add(0, sourceWithDelay);
  }

  public static void initialization() {
    HttpUtil.delayTestSync(SOURCES_WITH_DELAY, SOURCE_MAP);
  }
}
