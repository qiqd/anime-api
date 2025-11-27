package org.anime.util;

import org.anime.entity.meta.SourceWithDelay;
import org.anime.loger.Logger;
import org.anime.loger.LoggerFactory;
import org.anime.parser.HtmlParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HttpUtil implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
  private static final List<String> USER_AGENTS = Arrays.asList(
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.138 Safari/537.36",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.138 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.138 Safari/537.36 Edg/128.0.2792.75",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:129.0) Gecko/20100101 Firefox/129.0",
          "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.138 Mobile Safari/537.36",
          "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
          "Mozilla/5.0 (iPad; CPU OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
          "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.138 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.6478.127 Safari/537.36"
  );

  /**
   * 创建配置好的Jsoup连接对象，模拟浏览器请求
   *
   * @return 配置好的Connection对象
   */
  public static Connection createConnection(String url) throws InterruptedException {
    Thread.sleep((long) (Math.random() * 5000));
    return new HttpConnection()
            .url(url)
            .header("User-Agent", USER_AGENTS.get((int) (Math.random() * USER_AGENTS.size())))
            .header("Accept", "*/*")
            .timeout(20000);
  }

  public static Connection createConnection(String url, String referer) throws InterruptedException {
    return createConnection(url)
            .header("Referer", referer);
  }

  public static void delayTestSync(List<SourceWithDelay> delays, Map<String, HtmlParser> sources) {
    delays.clear();
    for (Map.Entry<String, HtmlParser> entry : sources.entrySet()) {
      String name = entry.getKey();
      HtmlParser parser = entry.getValue();
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
        delays.add(new SourceWithDelay(delay, parser));
      } catch (Exception e) {
        log.error("Error while initializing source:{},err msg: {}", name, e.getMessage());
        delays.add(new SourceWithDelay(999999, parser));
      }
    }
    delays.sort(Comparator.comparingInt(SourceWithDelay::getDelay));
  }

}
