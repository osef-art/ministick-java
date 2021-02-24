package ministick.window.controller;

import ministick.time.Chrono;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.stickman.ministick.Ministick;
import ministick.world.stickman.State;
import ministick.world.World;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MinistickController implements KeyListener {
  private boolean canAttack = true;
  private final Ministick ministick;
  private double prevPress, prevRelease;
  private static final Touch[] history = new Touch[2];
  private static final Chrono[] pressed = new Chrono[Touch.values().length];

  public MinistickController(World world) {
    ministick = world.ministick();
    initPressed();
  }
  public void initPressed() {
    for (int i = 0; i < pressed.length; i++) pressed[i] = new Chrono(false);
  }

  // key controller
  @Override
  public void keyPressed(KeyEvent e) {
    if (ministick.stateIs(State.Hurt, State.ParryHurt, State.GetUp)) return;

    switch (e.getKeyCode()) {
      case KeyEvent.VK_Q:
      case KeyEvent.VK_LEFT:
        if (isPressed(Touch.Left)) break;
        if (ministick.stateIs(State.Walk, State.Stand)) ministick.setState(State.Walk);
        ministick.lookLeft();
        press(Touch.Left);
        break;
      case KeyEvent.VK_D:
      case KeyEvent.VK_RIGHT:
        if (isPressed(Touch.Right)) break;
        if (ministick.stateIs(State.Walk, State.Stand)) ministick.setState(State.Walk);
        ministick.lookRight();
        press(Touch.Right);
        break;

      case KeyEvent.VK_Z:
      case KeyEvent.VK_UP:
        if (ministick.stateIs(State.Stand, State.Walk)) ministick.jump();
        press(Touch.Up);
        break;
      case KeyEvent.VK_S:
      case KeyEvent.VK_DOWN:
        if (isPressed(Touch.Down)) break;
        if (ministick.stateIs(State.Stand, State.Walk)) ministick.setState(State.Squatting);
        press(Touch.Down);
        break;

      case KeyEvent.VK_F:
        if (isPressed(Touch.F)) break;
        press(Touch.F);
        if (ministick.stateIs(State.Stand, State.Walk)) {
          if (ministick.canParry()) {
            ministick.setState(State.Parry);
            ministick.parryCooldown().startsDecreasing();
          } else {
            new Sound(SoundName.Cant).play();
          }
        }
        break;

      case KeyEvent.VK_L:
      case KeyEvent.VK_C:
        press(Touch.C);
        if (!canAttack) return;
        canAttack = false;

        if (ministick.stateIs(State.Jump)) ministick.uppaircut();

        else if (ministick.stateIs(State.Squat, State.Squatting, State.Brush) || (ministick.stateIs(State.Stand) && isPressed(Touch.Down)))
          ministick.uppercut();

        else if (ministick.stateIs(State.Stand, State.Walk, State.Kick))
          ministick.punch();
        break;

      case KeyEvent.VK_M:
      case KeyEvent.VK_V:
        press(Touch.V);
        if (!canAttack) return;
        canAttack = false;

        if (ministick.stateIs(State.Jump)) ministick.airkick();

        else if (ministick.stateIs(State.Squat, State.Squatting, State.Uppercut) || (ministick.stateIs(State.Stand) && isPressed(Touch.Down)))
          ministick.setState(State.Brush, SoundName.LowWhoosh);

        else if (ministick.stateIs(State.Stand, State.Walk, State.Punch))
          ministick.setState(State.Kick, SoundName.LowWhoosh);
        break;

      case KeyEvent.VK_X:
        if (isPressed(Touch.X)) break;
        press(Touch.X);
        ministick.enableGrapple();
        break;
    }
  }
  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_Q:
      case KeyEvent.VK_LEFT:
        release(Touch.Left);
        if (isPressed(Touch.Right)) ministick.lookRight();
        else if (ministick.stateIs(State.Walk)) ministick.setState(State.Stand);
        break;

      case KeyEvent.VK_D:
      case KeyEvent.VK_RIGHT:
        release(Touch.Right);
        if (isPressed(Touch.Left)) ministick.lookLeft();
        else if (ministick.stateIs(State.Walk)) ministick.setState(State.Stand);
        break;

      case KeyEvent.VK_S:
      case KeyEvent.VK_DOWN:
        release(Touch.Down);
        if (ministick.stateIs(State.Squat)) ministick.setState(ministick.standOrWalk());
        break;
      case KeyEvent.VK_Z:
      case KeyEvent.VK_UP:
        release(Touch.Up);
        break;

      case KeyEvent.VK_F:
        release(Touch.F);
        ministick.parryCooldown().startsIncreasing();
        if (ministick.stateIs(State.Parry)) ministick.setState(ministick.standOrWalk());
        break;

      case KeyEvent.VK_L:
      case KeyEvent.VK_C:
        release(Touch.C);
        canAttack = true;
        break;
      case KeyEvent.VK_M:
      case KeyEvent.VK_V:
        release(Touch.V);
        canAttack = true;
        break;
      case KeyEvent.VK_X:
        release(Touch.X);
        ministick.disableGrapple();
    }
  }
  @Override
  public void keyTyped(KeyEvent e) {
  }

  private void press(Touch touch) {
    history[0] = touch;
    if (touch == Touch.Left || touch == Touch.Right || touch == Touch.Down) prevRelease = Math.min(timePressed(Touch.Left), Math.min(timePressed(Touch.Right), timePressed(Touch.Down)));
    if (0 < prevPress && 0 < prevRelease && prevPress < 0.2 && prevRelease < 0.2) {
      if (ministick.stateIs(State.Walk)) ministick.dash(history[0], history[1]);
      if (ministick.stateIs(State.Squat, State.Squatting)) ministick.skipPlatform(history[0], history[1]);
    }
    pressed[touch.ordinal()].on();
    pressed[touch.ordinal()].reset();
  }
  private void release(Touch touch) {
    if (touch == Touch.Left || touch == Touch.Right || touch == Touch.Down) prevPress = timePressed(touch);
    history[1] = history[0];
    pressed[touch.ordinal()].off();
    pressed[touch.ordinal()].reset();
  }
  public double timePressed(Touch touch) {
    return pressed[touch.ordinal()].value();
  }
  public static boolean isPressed(Touch ... touches) {
    for (Touch touch : touches)
      if (!pressed[touch.ordinal()].isOff()) return true;
    return false;
  }
}
