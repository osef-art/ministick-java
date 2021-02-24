package ministick.window;

public class Dimensions {
  public static int windowWidth, windowHeight;
  public static int ground;

  Dimensions (int width, int height) {
    windowWidth = width;
    windowHeight = height;
    ground = windowHeight - 25;
  }
}
