package org.anime.entity.enumeration;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StaffType implements Serializable {
  @JSONField(name = "director")
  DIRECTOR("director"),
  @JSONField(name = "actor")
  ACTOR("actor"),
  @JSONField(name = "producer")
  PRODUCER("producer"),
  @JSONField(name = "writer")
  WRITER("writer"),
  @JSONField(name = "musician")
  MUSICIAN("musician"),
  @JSONField(name = "animator")
  ANIMATOR("animator");

  private final String value;

  StaffType(String value) {
    this.value = value;
  }
}
