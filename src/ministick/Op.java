package ministick;

import java.util.Random;

public abstract class Op {
  public static int randInt(int min, int max) {
    if (min >= max) throw new IllegalArgumentException("max must be greater than min");
    return new Random().nextInt((max - min) + 1) + min;
  }
}
