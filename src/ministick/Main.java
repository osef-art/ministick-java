package ministick;

import ministick.window.Window;

public class Main {
  public Main() {
    Window window = new Window(700, 500);
    window.run();
    System.exit(0);
  }

  public static void main(String[] args) {
    new Main();
  }
}
