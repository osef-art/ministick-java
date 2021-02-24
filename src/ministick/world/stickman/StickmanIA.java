package ministick.world.stickman;

import ministick.Coords;
import ministick.Vector;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.stickman.enemy.Enemy;
import ministick.world.stickman.enemy.EnemyType;
import ministick.world.stickman.ministick.Ministick;
import ministick.world.stickman.sidekick.Sidekick;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.awt.*;

public abstract class StickmanIA extends Stickman {
  private final List<Stickman> targets = new ArrayList<>();

  public StickmanIA(EnemyType type, Coords position) {
    super(type, position);
  }
  public StickmanIA(String name, Coords position, double weight) {
    super(name, position, weight);
  }

  // getters
  public Stickman target() {
    return targets.size() > 0 ? targets.get(0) : new Enemy(this);
  }
  @Override
  public int stateFrames() { return state().nbFramesE(); }

  // setters
  public void lookTarget(int diff) {
    look(diff < 0);
  }
  public void focus(Stickman stick) {
    targets.add(0, stick);
  }

  // state
  @Override
  public void setFrameSpeed() {
    switch (state()) {
      case Punch:
      case Punch2:
      case Uppercut:
      case ChargingPunch2:
        resetTimer(1/4.);
        break;
      default:
        super.setFrameSpeed();
    }
  }

  // moves
  @Override
  public void move() {
    if (stateIsNot(State.Stand, State.Walk)) return;
    int diff = target().x() - x();

    lookTarget(diff);

    if (y() < target().y() && yAcc() == 0) {
      skipPlatform();
    }
    else if (Math.abs(diff) > (width()/4. + width()*2/3. + target().hitbox().width) * Math.pow(speed(), 2)) {
      setState(State.Walk);
      addXAcc(direction(2));
    }
    else randomMove();
  }
  public void randomMove() {
    if (new Random().nextBoolean()) punch();
    else uppercut();
  }
  @Override
  public void jump() {
    super.jump();
    addXAcc(direction(25));
    new Sound (SoundName.EnemyJump).play();
  }
  public void chargeJump() {
    setState(State.Squat);
  }
  @Override
  public void attack() {
    Rectangle rect;
    Vector vector;

    switch (state()) {
      case Punch:
      case DashPunch:
        if (canNotReleaseDmg(1)) break;
        rect = new Rectangle(width()/4, height()/2, width() * 3/4, height()/8);
        vector = new Vector(direction(25) * strength()*5/4, 0);
        setDmgbox(rect, vector, 10, SoundName.Hit);
        break;
      case Uppercut:
        if (frame() == 6) clearDmgbox();
        if (canNotReleaseDmg(3)) break;
        rect = new Rectangle(0, -height()/8, width(), height()*5/8);
        vector = new Vector(direction(15) * strength()*5/4, -20 * strength()*1.5);
        setDmgbox(rect, vector, 15, SoundName.Hit);
        new Sound(SoundName.LowWhoosh).play();
        break;
      case Punch2:
        if (canNotReleaseDmg(1)) break;
        rect = new Rectangle(width()/4, height()/2, width(), height()/4);
        vector = new Vector(direction(30 * strength()), 0);
        setDmgbox(rect, vector, 10, SoundName.Hit);
        break;
      default:
        clearDmgbox();
    }
  }

  @Override
  public boolean checkIfHit(Stickman target) {
    if (super.checkIfHit(target)) {
      if (target instanceof StickmanIA) {
        ((StickmanIA) target).focus(this);
      }
      else if (target instanceof Ministick) {
        for (Sidekick sidekick : ((Ministick) target).sidekicks()) sidekick.focus(this);
      }
      return true;
    }
    return false;
  }

  // update
  public void updateTarget() {
    if (target().isDead()) targets.remove(0);
  }

  @Override
  public void updateState() {
    if (!isOnLastFrame()) return;
    switch (state()) {
      case Squat:
        jump();
        break;
      default:
        super.updateState();
    }
  }

  @Override
  public boolean update() {
    super.update();

    updateTarget();
    move();
    return checkIfHit(target());
  }
}
