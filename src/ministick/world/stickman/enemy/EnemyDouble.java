package ministick.world.stickman.enemy;

import ministick.Vector;
import ministick.window.SoundName;
import ministick.world.stickman.State;
import ministick.world.stickman.Stickman;

import java.awt.*;

public final class EnemyDouble extends Enemy {
  public EnemyDouble(Stickman target) {
    super(EnemyType.EnemyDouble, target);
  }

  // setters
  @Override
  public void attackOrJump() {
    jump();
  }
  @Override
  public void setFrameSpeed() {
    if (stateIs(State.Punch, State.ChargingPunch2, State.Punch2)) resetTimer(1/6.);
    else super.setFrameSpeed();
  }
  @Override
  public void chargeUppercut() {
    chargePunch();
  }


  @Override
  public void attack() {
    Rectangle rect;
    Vector vector;

    switch (state()) {
      case Punch:
        if (canNotReleaseDmg(1)) break;
        rect = new Rectangle(width()/4, height()/2, width() * 3/4, height()/8);
        vector = new Vector(direction(10 * strength()), 0);
        setDmgbox(rect, vector, 10, SoundName.Hit);
        break;
      case Punch2:
        if (canNotReleaseDmg(1)) break;
        rect = new Rectangle(width()/4, height()/2, width(), height()/4);
        vector = new Vector(direction(35 * strength()), 0);
        setDmgbox(rect, vector, 15, SoundName.Hit);
        break;
      default:
        clearDmgbox();
    }
  }

  @Override
  public void updateState() {
    if (!isOnLastFrame()) return;
    switch (state()) {
      case Punch:
        setState(dmgbox().excluded().size() == 0 ? standOrWalk() : State.ChargingPunch2);
        break;
      default:
        super.updateState();
    }
  }
}
