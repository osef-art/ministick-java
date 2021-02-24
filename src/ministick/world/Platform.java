package ministick.world;

public class Platform extends Object {
  public Platform(int x, int y, int width, int height) {
    super(x, y, width, height);
  }
  public Platform(int x, int y, int width) {
    this(x, y, width, 10);
  }
}
