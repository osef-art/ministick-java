package ministick.world;

import ministick.Coords;

public class Hook extends Coords {
  private final int range;

  public Hook() {
    this(0, 0, 0);
  }
  public Hook(int x, int y, int range) {
    super(x, y);
    this.range = range;
  }

  public int range() {
    return range;
  }
}
