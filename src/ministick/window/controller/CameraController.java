package ministick.window.controller;

import ministick.time.Chrono;
import ministick.window.Window;
import ministick.world.Camera;
import ministick.world.World;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CameraController implements KeyListener {
  private static boolean enabled;
  private final Camera camera;
  private static final Chrono[] pressed = new Chrono[Touch.values().length];

  public CameraController(World world) {
    camera = world.camera();
    initPressed();
  }
  public void initPressed() {
    for (int i = 0; i < pressed.length; i++) pressed[i] = new Chrono(false);
  }
  public static boolean isEnabled() {
    return enabled;
  }
  public static void enable() {
    enabled = true;
  }
  public static void disable() {
    enabled = false;
  }

  // key controller
  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_Q:
      case KeyEvent.VK_LEFT:
        if (isPressed(Touch.Left)) break;
        press(Touch.Left);
        break;

      case KeyEvent.VK_D:
      case KeyEvent.VK_RIGHT:
        if (isPressed(Touch.Right)) break;
        press(Touch.Right);
        break;

      case KeyEvent.VK_S:
      case KeyEvent.VK_DOWN:
        if (isPressed(Touch.Down)) break;
        press(Touch.Down);
        break;

      case KeyEvent.VK_Z:
      case KeyEvent.VK_UP:
        if (isPressed(Touch.Up)) break;
        press(Touch.Up);
        break;

      case KeyEvent.VK_SHIFT:
      case KeyEvent.VK_SPACE:
        if (isPressed(Touch.Space)) break;
        press(Touch.Space);
        enable();
        Window.removeListener();
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_Q:
      case KeyEvent.VK_LEFT:
        release(Touch.Left);
       break;

      case KeyEvent.VK_D:
      case KeyEvent.VK_RIGHT:
        release(Touch.Right);
        break;

      case KeyEvent.VK_S:
      case KeyEvent.VK_DOWN:
        release(Touch.Down);
       break;

      case KeyEvent.VK_Z:
      case KeyEvent.VK_UP:
        release(Touch.Up);
     break;

      case KeyEvent.VK_SHIFT:
      case KeyEvent.VK_SPACE:
        release(Touch.Space);
        disable();
        camera.recenter();
        camera.acc().set(0, 0);
        Window.restoreListener();
        break;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  private void press(Touch touch) {
    pressed[touch.ordinal()].on();
    pressed[touch.ordinal()].reset();
  }
  private void release(Touch touch) {
    pressed[touch.ordinal()].off();
    pressed[touch.ordinal()].reset();
  }
  public static boolean isPressed(Touch ... touches) {
    for (Touch touch : touches)
      if (!pressed[touch.ordinal()].isOff()) return true;
    return false;
  }
}
