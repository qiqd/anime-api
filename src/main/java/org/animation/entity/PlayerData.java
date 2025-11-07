package org.animation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData implements Serializable {
  private String flag;
  private Integer encrypt;
  private Integer trysee;
  private Integer points;
  private String link;
  private String link_next;
  private String link_pre;
  private String url;
  private String url_next;
  private String from;
  private String server;
  private String note;
  private String id;
  private Integer sid;
  private Integer nid;
}

