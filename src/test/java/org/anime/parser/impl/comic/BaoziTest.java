package org.anime.parser.impl.comic;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.anime.entity.base.Detail;
import org.anime.entity.base.Media;

import java.util.List;

public class BaoziTest extends TestCase {
  private final Baozi baozi = new Baozi();

  public void testFetchSearchSync() throws Exception {
    List<Media> comics = baozi.fetchSearchSync("租借女友", 1, 10, System.out::println);
    System.out.println(JSON.toJSONString(comics));
    System.out.println(comics.size());
  }

  public void testFetchDetailSync() throws Exception {
    Detail comicDetail = baozi.fetchDetailSync("/comic/zujienuyou-gongdaolili", System.out::println);
    System.out.println(JSON.toJSONString(comicDetail));
  }

  public void testFetchViewSync() throws Exception {
    System.out.println(baozi.fetchViewSync("/user/page_direct?comic_id=zujienuyou-gongdaolili_ts2oyk&section_slot=0&chapter_slot=1", System.out::println));
  }
}