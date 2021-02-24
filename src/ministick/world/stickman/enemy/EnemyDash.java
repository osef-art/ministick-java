package ministick.world.stickman.enemy;

import ministick.world.stickman.Stickman;

public final class EnemyDash extends Enemy {
  public EnemyDash(Stickman target) {
    super(EnemyType.EnemyDash, target);
  }

  @Override
  public void punch() {
    dashPunch();
    addXAcc(direction(15));
  }
  @Override
  public void attackOrJump() {
    jump();
  }
  @Override
  public void chargeUppercut() {
    chargePunch();
  }

  @Override
  public boolean checkIfHit(Stickman target) {
    if (super.checkIfHit(target)) {
      addXAcc(direction(30));
      return true;
    }
    return false;
  }
}
