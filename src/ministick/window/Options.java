package ministick.window;

import ministick.time.Chrono;

public class Options {
  private static final double fps = 33;
  private static double pausedTime = 0;
  private static final Chrono pauseChrono = new Chrono();
  private static boolean showBackground = false;
  private static boolean showKeyboard = false;
  private static boolean showHitboxes = false;
  private static boolean pause = false;
  private static boolean mute = false;

  public Options(int width, int height) {
    new Dimensions(width, height);
  }

  public static boolean showBackground() {
    return showBackground;
  }
  public static boolean showKeyboard() {
    return showKeyboard;
  }
  public static boolean showHitboxes() {
    return showHitboxes;
  }
  public static boolean onPause() {
    return pause;
  }
  public static boolean isMuted() {
    return mute;
  }
  public static double fps(double fact) {
    return fps * fact;
  }
  public static double fps() {
    return fps;
  }

  public static void toggleBackground() {
    showBackground = !showBackground;
  }
  public static void toggleHitboxes() {
    showHitboxes = !showHitboxes;
  }
  public static void toggleKeyboard() {
    showKeyboard = !showKeyboard;
  }
  public static void togglePause() {
    pause = !pause;
    if (pause) {
      Window.removeListener();
      pauseChrono.reset(pausedTime);
    }
    else {
      Window.restoreListener();
      pausedTime = pauseChrono.value();
    }
  }
  public static void toggleMute() {
    mute = !mute;
  }
}
