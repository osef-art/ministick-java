package ministick.world;

public class Wall extends Object {
  public Wall(int x, int y, int width, int height) {
    super(x, y, width * (0 < width ? 1 : -1), height * (0 < height ? 1 : -1));
    if (width < 0) addX(width);
    if (height < 0) addY(height);
    updateZonebox();
  }
}
