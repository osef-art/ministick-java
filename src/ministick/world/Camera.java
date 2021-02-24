package ministick.world;

import ministick.Coords;
import ministick.time.Timer;
import ministick.window.Dimensions;
import ministick.window.controller.CameraController;
import ministick.window.controller.Touch;
import ministick.world.stickman.Stickman;

public class Camera extends Object {
  private final Timer shakingTimer = new Timer(0, false);
  private final Coords shaking = new Coords(0, 0);
  private final Coords shift = new Coords(0, 0);
  private final Stickman following;

  public Camera(Stickman stick) {
    super(stick.x() - Dimensions.windowWidth/2,
      stick.y() - Dimensions.windowHeight/2,
      Dimensions.windowWidth,
      Dimensions.windowHeight);
    following = stick;
  }

  public int xCenter() {
    return x() + width()/2;
  }
  public int yCenter() {
    return y() + height()/2;
  }
  public Coords offset() {
    return new Coords(-x(), -y() + height()/4).shift(shaking);
  }
  public Coords shaking() {
    return shaking;
  }
  public boolean isShaking() {
    return !shaking().equals(new Coords(0, 0));
  }
  @Override
  public Hitbox zonebox() {
    return new Hitbox(x(), y(), width(), height());
  }

  public void stopShaking() {
    shaking.set(0, 0);
  }
  public void startShaking(int x, int y, double time) {
    shaking.set(x, y);
    shakingTimer.reset(time);
  }
  public void recenter() {
    shift.set(0, 0);
  }
  /*
  public void recenterIfLost() {
    if (!zonebox().contains(following.zonebox())) recenter();
  }
   */

  public void update() {
    if (CameraController.isEnabled()) {
      if (CameraController.isPressed(Touch.Left)) addXAcc(-6);
      if (CameraController.isPressed(Touch.Right)) addXAcc(6);
      if (CameraController.isPressed(Touch.Down)) addYAcc(9);
      if (CameraController.isPressed(Touch.Up)) addYAcc(-9);
      acc().mult(0.9);
      shift.add(acc());
    }

    addX((following.x() + shift.x() - xCenter()) / 6.);
    addY((following.y() + shift.y() - yCenter()) / 10.);

    if (y() + height() > Dimensions.windowHeight + 200) {
      //shift.setY(Dimensions.windowHeight + 200 - height() - y());
    }

    if (shakingTimer.isActive()) {
      if ((int) (shakingTimer.value()*40) %2 == 0) shaking.invertY();
    }
    else if (isShaking()) stopShaking();
  }
}
