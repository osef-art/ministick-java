package ministick.world.stickman.enemy;

import ministick.Coords;
import ministick.Op;
import ministick.time.Timer;
import ministick.window.Dimensions;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.stickman.State;
import ministick.world.stickman.Stickman;
import ministick.world.stickman.StickmanIA;

import java.util.Random;

public class Enemy extends StickmanIA {
  private final Timer charging;

  public Enemy(Stickman target) {
    this(EnemyType.random(), target);
  }
  public Enemy(EnemyType type, Stickman target) {
    super(type, randomPosition());
    focus(target);
    charging = new Timer(0.45 / dexterity());
  }

  // getters
  @Override
  public int stateFrames() { return state().nbFramesE(); }
  private static Coords randomPosition() {
    return new Coords(Op.randInt(-175, Dimensions.windowWidth + 175), 0);
  }

  // setters
  @Override
  public void setFrameSpeed() {
    switch (state()) {
      case Punch:
      case Uppercut:
        resetTimer(1/3.);
        break;
      default:
        super.setFrameSpeed();
    }
  }
  @Override
  public void randomMove() {
    charging.reset();
    if (y() - target().y() >= height()*3/2) chargeJump();
    else if (y() - target().y() >= height()/2) attackOrJump();
    else if (y() < target().y() && yAcc() == 0) skipPlatform();
    else chargeRandomAttack();
  }

  // moves
  @Override
  public void punch() {
    super.punch();
    new Sound(SoundName.LowWhoosh).play();
    addXAcc(direction(15 / weight()));
  }
  @Override
  public void uppercut() {
    setState(State.Uppercut);
    addXAcc(direction(10 / weight()));
  }
  public void attackOrJump() {
    if (new Random().nextBoolean()) chargeJump();
    else chargeUppercut();
  }
  public void chargeRandomAttack() {
    /*
    if (!(target() instanceof Sidekick)) {
      setState(State.Stand);
      return;
    }
    */
    if (new Random().nextBoolean()) chargePunch();
    else chargeUppercut();
  }
  @Override
  public void chargeJump() {
    charging.reset();
    super.chargeJump();
  }
  public void chargePunch() {
    setState(State.ChargingPunch);
  }
  public void chargeUppercut() {
    setState(State.ChargingUppercut);
  }

  @Override
  public boolean affectHit(Stickman enemy) {
    if (super.affectHit(enemy)) return true;

    switch (state()) {
      case Uppercut:
        if (enemy.stateIs(State.Parry)) {
          enemy.setState(State.Looping);
          enemy.setYAcc(-30);
          enemy.disableParry();
        }
        else enemy.getHurt();
        break;
      default:
        if (enemy.stateIsNot(State.GetUp)) enemy.getHurt(lookingLeft());
    }
    return false;
  }

  @Override
  public void move() {
    if (stateIsNot(State.Stand, State.Walk)) return;
    int diff = target().x() - x();

    lookTarget(diff);

    if (Math.abs(diff) > (width()/4. + width()*2/3. + target().hitbox().width) * Math.pow(speed(), 2)) {
      setState(State.Walk);
      addXAcc(direction(2));
    }
    else randomMove();
  }

  @Override
  public void updateState() {
    if (!isOnLastFrame()) return;
    switch (state()) {
      case Uppercut:
        setState(standOrWalk());
        addX(direction(width()/2.));
        break;
      default:
        super.updateState();
    }
  }
  @Override
  public boolean update() {
    super.update();
    if (charging.isExceeded()) {
      switch (state()) {
        case ChargingPunch:
          punch();
          break;
        case ChargingUppercut:
          uppercut();
          break;
        case Squat:
          jump();
          break;
      }
    }
    return checkIfHit(target());
  }
}
