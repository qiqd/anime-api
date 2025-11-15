package org.anime.api;

import org.junit.Test;

public class AnimationApiTest {

  @Test
  public void testMoveToTop() {
    System.out.println(AnimationApi.SOURCES_WITH_DELAY);
    AnimationApi.initialization();
    System.out.println(AnimationApi.SOURCES_WITH_DELAY);
  }

  @Test
  public void testInitialization() {
  }
}