package ministick.world.stickman.sidekick;

import ministick.Coords;
import ministick.world.effect.EffectType;
import ministick.world.stickman.State;
import ministick.world.stickman.Stickman;
import ministick.world.stickman.StickmanIA;
import ministick.world.stickman.enemy.Enemy;

import java.util.List;
import java.util.Random;

public class Sidekick extends StickmanIA {
  private final List<Enemy> targets;

  public Sidekick(Coords position, List<Enemy> targets) {
    super("sidekick", position.shift(0, -20), 1);
    this.targets = targets;
    focus(targets.get(0));
    uppaircut();
  }

  // moves
  @Override
  public void randomMove() {
    if (y() - target().y() >= height()*5/4) chargeJump();
    else if (stateIsNot(State.Fall) && y() < target().y()) skipPlatform();
    else if (new Random().nextBoolean()) dashPunch();
    else super.randomMove();
  }

  @Override
  public void jump() {
    super.jump();
    addXAcc(direction(10));
  }

  @Override
  public boolean checkIfHit(Stickman target) {
    if (super.checkIfHit(target)) {
      switch (state()) {
        case DashPunch:
          addXAcc(direction(30));
        case Punch:
        case Punch2:
          addEffect(EffectType.HitHorizontal, 0, height()/3, 60);
          break;
        case Uppercut:
          addEffect(EffectType.HitVertical, 0, 0, 80);
          break;
      }
      return true;
    }
    return false;
  }
  // update
  @Override
  public void updateTarget() {
    if (target().isDead()) {
      for (Enemy enemy : targets) {
        if (!enemy.isDead()) {
          focus(enemy);
          return;
        }
      }
      super.updateTarget();
    }
  }
  @Override
  public void updateState() {

    if (stateIs(State.Jump) && target().y() < y() && yAcc() > 0) uppaircut();

    if (!isOnLastFrame()) return;
    switch (state()) {
      case Uppercut:
        setState(standOrWalk());
        addX(direction(width()/2.));
        break;
      case Punch:
        if (new Random().nextBoolean()) setState(State.ChargingPunch2);
        else setState(standOrWalk());
        break;

      default:
        super.updateState();
    }
  }

  @Override
  public boolean update() {
    boolean hit = false;
    super.update();
    for (Stickman target : targets) {
      if (checkIfHit(target)) hit = true;
    }
    return hit;
  }
}
