package org.anime.meta.impl;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.meta.MetaService;

public class BangumiTest extends TestCase {
  private final MetaService bangumi = new Bangumi();

  public void testFetchSubjectSearchSync() throws Exception {
    System.out.println(JSON.toJSONString(bangumi.fetchSubjectSearchSync("租借女友", 1, 10)));
  }

  public void testFetchWeeklyUpdateSync() throws Exception {
    System.out.println(JSON.toJSONString(bangumi.fetchWeeklyUpdateSync()));
  }

  public void testFetchSubjectSync() throws Exception {
    System.out.println(JSON.toJSONString(bangumi.fetchSubjectSync(425208)));
  }
}