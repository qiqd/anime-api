package org.anime.entity.base;

@FunctionalInterface
public interface ExceptionHandler {
  void handle(Exception e);
}
