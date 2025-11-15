package org.anime.util;

import org.anime.entity.bangmi.SourceWithDelay;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.HtmlParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HttpUtil implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

  /**
   * 创建配置好的Jsoup连接对象，模拟浏览器请求
   *
   * @return 配置好的Connection对象
   */
  public static Connection createConnection(String url) {
    return new HttpConnection()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
//            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//            .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
//            .header("Accept-Encoding", "gzip, deflate, br")
//            .header("Connection", "keep-alive")
//            .header("Upgrade-Insecure-Requests", "1")
//            .header("Sec-Fetch-Dest", "document")
//            .header("Sec-Fetch-Mode", "navigate")
//            .header("Sec-Fetch-Site", "none")
//            .header("Cache-Control", "max-age=0")
            .timeout(20000); // 20秒超时
  }

  public static Connection createConnection(String url, String referer) {
    return createConnection(url)
            .header("Referer", referer);
  }

  public static <T extends HtmlParser> void delayTestSync(List<SourceWithDelay<T>> delays, Map<String, T> sources) {
    delays.clear();
    for (Map.Entry<String, T> entry : sources.entrySet()) {
      String name = entry.getKey();
      T parser = entry.getValue();
      try {
        long startTime = System.currentTimeMillis();
        Connection connection = Jsoup.connect(parser.getBaseUrl())
                .timeout(5000)
                .followRedirects(true)
                .ignoreContentType(true);
        Connection.Response response = connection.execute();
        long endTime = System.currentTimeMillis();
        int delay = -1;
        if (response.statusCode() == 200) {
          delay = (int) (endTime - startTime);
        }
        delays.add(new SourceWithDelay<T>(delay, parser));
      } catch (Exception e) {
        log.error("Error while initializing source:{},err msg: {}", name, e.getMessage());
        delays.add(new SourceWithDelay<T>(999999, parser));
      }
    }
    delays.sort(Comparator.comparingInt(SourceWithDelay::getDelay));
  }

}
