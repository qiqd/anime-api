package org.anime.util;

import java.util.ArrayList;

public class Demo {
  void test() {
    ArrayList<Father> fathers = new ArrayList<>();
    fathers.add(new Son());
  }
}


class Father {
}

class Son extends Father {
}
