package ministick.world.tools;

import ministick.Coords;
import ministick.world.Hook;
import ministick.world.stickman.Stickman;

public class Grapple {
  private final Stickman following;
  private double maxLength;
  private boolean on;
  private Hook hook;

  public Grapple(Stickman following) {
    this(new Hook(), following);
  }
  public Grapple(Hook hook, Stickman following) {
    this.following = following;
    hook(hook);
  }

  // getters
  public boolean isOn() {
    return on;
  }
  public boolean isOff() {
    return !on;
  }
  public Coords origin() {
    return hook;
  }
  public Coords following() {
    return new Coords(following.x(), following.y() - following.height()/4);
  }
  public double length() {
    return Math.sqrt(Math.pow(hook.x() - following().x(), 2) + Math.pow(hook.y() - following().y(), 2));
  }
  public boolean isStretched() {
    return length() >= maxLength;
  }
  public boolean isBelow() {
    return following.y() <= hook.y();
  }


  public void resize() {
    if (!on) return;
    if (isStretched()) {
      int distX = (int) ((following().x() - hook.x()) * maxLength /length());
      int distY = (int) ((following().y() - hook.y()) * maxLength /length());
      following.setX(hook.x() + distX);
      following.setY(hook.y() + distY);
      following.acc().set(following.direction(following.y() - hook.y()), following.direction(hook.x() - following.x()));
      following.acc().mult(0.4);
    }
  }

  public void enable() {
    on = true;
  }
  public void disable() {
    on = false;
  }
  public void hook(Hook hook) {
    this.hook = hook;
    maxLength = length();
  }
}