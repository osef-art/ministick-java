package ministick.world.stickman.ministick;

import ministick.Coords;
import ministick.Vector;
import ministick.time.Cooldown;
import ministick.time.Timer;
import ministick.window.Dimensions;
import ministick.world.Hook;
import ministick.world.stickman.sidekick.Sidekick;
import ministick.world.tools.Grapple;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.window.controller.MinistickController;
import ministick.window.controller.Touch;
import ministick.world.World;
import ministick.world.bonus.Bonus;
import ministick.world.bonus.BonusType;
import ministick.world.effect.EffectType;
import ministick.world.effect.LostHP;
import ministick.world.effect.Sparkle;
import ministick.world.stickman.State;
import ministick.world.stickman.Stickman;
import ministick.world.stickman.enemy.Enemy;
import ministick.world.throwable.Shuriken;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

final public class Ministick extends Stickman {
  private final Grapple grapple = new Grapple(this);
  private final List<Sidekick> sidekicks = new ArrayList<>();
  private final List<Shuriken> weapons = new ArrayList<>();
  private final List<Sparkle> sparkles = new ArrayList<>();
  private final Cooldown parryCooldown = new Cooldown(5);
  private final Cooldown dashCooldown = new Cooldown(1);
  private final Timer bonusTimer = new Timer();
  private final Combo combo = new Combo();
  private final List<Bonus> collectibles;
  private final List<Enemy> targets;
  private boolean canParry = true;
  private boolean canAdd = true;
  private BonusType lastBonus;
  private int shurikens = 0;

  public Ministick(World world) {
    super("ministick", new Coords(Dimensions.windowWidth/2, 0), 1);
    collectibles = world.bonuses();
    targets = world.enemies();
    setState(State.Fall);
    setStun(0.3);
  }

  // getters
  public Grapple getGrapple() {
    return grapple;
  }
  public Combo combo() {
    return combo;
  }
  public int nbShurikens() {
    return shurikens;
  }
  public Move combo(int i) {
    return combo.get(i);
  }
  public Timer bonusTimer() {
    return bonusTimer;
  }
  public BonusType lastBonus() {
    return lastBonus;
  }
  public Cooldown parryCooldown() {
    return parryCooldown;
  }
  public List<Sparkle> sparkles() {
    return sparkles;
  }
  public List<Shuriken> weapons() {
    return weapons;
  }
  public List<Sidekick> sidekicks() {
    return sidekicks;
  }
  public boolean hasBonus(BonusType bonus) {
    return hasBonus() && lastBonus == bonus;
  }
  public boolean hasBonus() {
    return bonusTimer.isActive();
  }
  public boolean canParry() {
    return canParry;
  }

  // setters
  @Override
  public void setState(State state) {
    super.setState(state);
    canAdd = stateIsNot(State.Stand);
  }
  @Override
  public State standOrWalk() {
    return MinistickController.isPressed(Touch.Left, Touch.Right) ? State.Walk :
           MinistickController.isPressed(Touch.Down) ? State.Squatting : State.Stand;
  }
  @Override
  public State standOrSquat() {
    return MinistickController.isPressed(Touch.Down) ? State.Squat : State.Stand;
  }
  @Override
  public State standOrParade() {
    return MinistickController.isPressed(Touch.F) ? State.Parry : State.Stand;
  }
  @Override
  public void decreaseHP(double hp) {
    super.decreaseHP(stateIs(State.Parry, State.ParryHurt) ? hp/10 : hp);
  }
  public void enableGrapple() {
    for (Hook hook : World.hooks()) {
      grapple.hook(hook);
      if (grapple.length() <= hook.range()) {
        if (grapple.isBelow()) invertLook();
        grapple.enable();
      }
    }
  }
  public void disableGrapple() {
    grapple.disable();
    if (yAcc() < -20) setState(State.Looping);
  }

  // states
  @Override
  public void move() {
    if (stateIsNot(State.Walk, State.Jump, State.Fall, State.Uppaircut, State.Airkick, State.Punch, State.Kick, State.Brush)) return;
    int acc = hasBonus(BonusType.Kick) ? 10 : 7;
    if (MinistickController.isPressed(Touch.Left)) addXAcc(-acc);
    if (MinistickController.isPressed(Touch.Right)) addXAcc(acc);
  }
  @Override
  public void die() {
    addYAcc(-30);
    new Sound(SoundName.Death).play();
    setState(State.Looping);
  }
  @Override
  public void getHurt(boolean look) {
    if (stateIs(State.Looping)) return;
    if (stateIs(State.Parry, State.ParryHurt) && look != lookingLeft()) {
      parryCooldown.decrease(0.1);
      setState(State.ParryHurt);
      multXAcc(0.85);
      stun().reset(0.25);
    } else {
      setState(State.Hurt);
      stun().reset();
    }
    combo.lose();
  }
  @Override
  public void checkIfLandedOnPlatform() {
    if (stateIs(State.Fall) && MinistickController.isPressed(Touch.Down)) return;
    super.checkIfLandedOnPlatform();
  }
  @Override
  public void sustainGravity() {
    if (grapple.isOff()) super.sustainGravity();
  }
  @Override
  public void disableParry() {
    canParry = false;
  }

  // moves
  @Override
  public void jump() {
    super.jump();
    new Sound (SoundName.Jump).play();
  }
  @Override
  public void punch() {
    super.punch();
    new Sound(SoundName.Whoosh).play();
    if (shurikens > 0) {
      shurikens--;
      weapons.add(new Shuriken(x(), y() - height()*2/3, lookingLeft()));
      new Sound(SoundName.Slice).play();
    }
  }
  public void airkick() {
    setState(State.Airkick, SoundName.LowWhoosh);
    if (yAcc() > 0) setYAcc(0);
  }
  public void dash(Touch first, Touch second) {
    if (!dashCooldown.isReady() || yAcc() == 0) return;
    int dash;

    if (first != second) {
      setState(State.ReverseDash, SoundName.Dash);
      dash = 75;
      invertLook();
    }
    else {
      setState(State.Dash, SoundName.Dash);
      dash = 50;
    }
    addToCombo();
    dashCooldown.reset();
    addXAcc(first == Touch.Left ? -dash : dash);
    addEffect(EffectType.Dash);
  }

  public void skipPlatform(Touch first, Touch second) {
    if (y() < Dimensions.ground && first == Touch.Down && first == second) skipPlatform();
  }

  // attack
  @Override
  public void attack() {
    double kick = hasBonus(BonusType.Kick) ? 1.75 : 1,
        punch = hasBonus(BonusType.Punch) ? 2 : 1;
    Rectangle rect;
    Vector vector;

    switch (state()) {
      case Punch:
        if (frame() != 2) break;
        rect = new Rectangle(width()/4, height()/2, width()*3/4, height()/8);
        vector = new Vector(direction(15*punch), 0);
        setDmgbox(rect, vector, 10*punch, SoundName.Hit);
        setXAcc(direction(10));
        break;
      case Kick:
        if (frame() != 4) break;
        rect = new Rectangle(0, height()/4, width(), height()/2);
        vector = new Vector(direction(20*kick), 20);
        setDmgbox(rect, vector, 20*kick, SoundName.Kick);
        setXAcc(direction(15));
        break;
      case Uppercut:
        if (canNotReleaseDmg(4)) break;
        rect = new Rectangle(0, -height()/8, width()*9/8, height()*3/4);
        vector = new Vector(direction(-5), -40 * punch);
        setDmgbox(rect, vector, 15*punch, SoundName.Hit);
        setXAcc(direction(5));
        new Sound(SoundName.LowWhoosh).play();
        break;
      case Brush:
        if (frame() != 2) break;
        rect = new Rectangle(width()/4, height()*7/8, width(), height()/8);
        vector = new Vector(direction(5), -25 * kick);
        setDmgbox(rect, vector, 5*kick, SoundName.Kick);
        setXAcc(direction(5));
        break;
      case Airkick:
        if (frame() != 3) break;
        rect = new Rectangle(0, height()*5/8, width()*5/4, height()/4);
        vector = new Vector(direction(50*kick), -10);
        setDmgbox(rect, vector, 25*kick, SoundName.Kick);
        break;
      case Uppaircut:
        if (frame() == 3) {
          rect = new Rectangle(0, height()/2, width(), height()*2/3);
          vector = new Vector(direction(10), -30);
          setDmgbox(rect, vector, 20*punch, SoundName.Hit);
        }
        else if (frame() == 6) {
          rect = new Rectangle(0, 0, width(), height()*2/3);
          vector = new Vector(direction(20), -20);
          moveDmgbox(rect, vector);
        }
        break;
      default:
        clearDmgbox();
    }
  }
  @Override
  public boolean checkIfHit(Stickman target) {
    if (!super.checkIfHit(target)) return false;
    int kickBuff = hasBonus(BonusType.Kick) ? 25 : 0,
        punchBuff = hasBonus(BonusType.Punch) ? 25 : 0;

    switch (state()) {
      case Punch:
        addEffect(EffectType.HitHorizontal, 0, height()/3, 40);
        addEffect(EffectType.PunchHit(), -punchBuff, height()/3 - punchBuff, 40 + 2*punchBuff);
        break;
      case Kick:
        addEffect(EffectType.HitDiagonal, 0, height()/4, 50);
        addEffect(EffectType.KickHit(), -kickBuff, height()/4 - kickBuff, 50 + 2*kickBuff);
        break;
      case Uppercut:
        addEffect(EffectType.HitVertical, 0, 0, 60);
        addEffect(EffectType.PunchHit(), -punchBuff, -punchBuff, 60 + 2*punchBuff);
        break;
      case Brush:
        addEffect(EffectType.HitHorizontal, 0, height() - 20, 30);
        addEffect(EffectType.KickHit(), -kickBuff, height() - 20 - kickBuff, 30 + 2*kickBuff);
        break;
      case Uppaircut:
        addEffect(EffectType.HitVertical, 0, 0, 80);
        addEffect(EffectType.PunchHit(), -punchBuff, -punchBuff, 80 + 2*punchBuff);
        break;
      case Airkick:
        addEffect(EffectType.HitHorizontal, 0, height()/2, 70);
        addEffect(EffectType.KickHit(), -kickBuff, height()/2 - kickBuff, 70 + 2*kickBuff);
        break;
    }
    return true;
  }
  @Override
  public boolean affectHit(Stickman enemy) {
    if (super.affectHit(enemy)) return true;

    switch (state()) {
      case Uppercut:
        if (combo.size() > 0 && combo(0) == Move.Brush) {
          jump();
          addYAcc(-10);
          enemy.setState(State.Float);
          enemy.setXAcc(0);
          enemy.setYAcc(yAcc() - 10);
          enemy.setX(x() + direction(75));
        }
        else if (enemy.stateIs(State.GetUp)) {
          enemy.setState(State.Looping);
          enemy.setYAcc(-30);
          enemy.addXAcc(direction(7));
        }
        else enemy.getHurt();
        break;
      case Fall:
      case Uppaircut:
        enemy.setState(State.Looping);
        break;
      case Kick:
        if (enemy.stateIs(State.GetUp)) {
          enemy.setState(State.Looping);
          enemy.setYAcc(-10);
          enemy.addXAcc(direction(7));
        }
        else enemy.getHurt();
        break;
      case Brush:
        if (enemy.stateIs(State.Punch, State.Uppercut)) {
          enemy.setState(State.Looping);
          enemy.setYAcc(-10);
          enemy.addXAcc(direction(7));
        }
        else if (enemy.stateIs(State.GetUp)) {
          enemy.setYAcc(0);
          enemy.addXAcc(direction(15));
        } else {
          addX(direction(10));
          enemy.setY(y());
          enemy.getHurt();
        }
        break;
      default:
        if (enemy.stateIsNot(State.GetUp)) enemy.getHurt(lookingLeft());
    }
    return false;
  }
  @Override
  public void checkIfCollect(Bonus bonus) {
    if (!zonebox().collides(bonus.hitbox()) && !dmgbox().collides(bonus.hitbox())) return;
    new Sound(SoundName.Bonus).play();
    bonus.collect();
    activateBonus(bonus);
  }
  private void activateBonus(Bonus bonus) {
    if (bonus.isGlobal()) return;

    if (!bonus.isPunctual()) {
      lastBonus = bonus.type();
      bonusTimer.reset(lastBonus.time());
    }
    switch (bonus.type()) {
      case Health:
        if (health().isFilled()) return;
        health().increase(10);
        addEffect(new LostHP(10, this, new Color(200, 250, 50)));
        break;
      case Shuriken:
        giveShurikens();
        break;
      case Sidekick:
        sidekicks.add(new Sidekick(position(), targets));
    }
    sparkles.clear();
  }

  // effects
  public void giveShurikens() {
    shurikens += 3;
  }
  private void addToCombo() {
    if (canAdd) combo.add(state());
    if (combo.isLost()) combo.lose();
  }
  private void addSparkle(BonusType bonus) {
    sparkles.add(new Sparkle(zonebox(), 7, bonus.color()));
  }
  private void sparkle() {
    if (hasBonus(BonusType.Punch)) for (int i = 0; i < 5; i++) addSparkle(BonusType.Punch);
    if (hasBonus(BonusType.Kick)) for (int i = 0; i < 5; i++) addSparkle(BonusType.Kick);
  }

  // update
  private void updateSidekicks() {
    for (Sidekick sidekick : sidekicks) {
      sidekick.update();
      if (sidekick.isDead()) {
        sidekicks.remove(sidekick);
        return;
      }
    }
  }
  private void updateSparkles() {
    for (Sparkle sparkle : sparkles) {
      if (sparkle.isOver()) continue;
      sparkle.nextFrame();
    }
  }
  private void updateWeapons() {
    for (Shuriken shuriken : weapons) {
      shuriken.update();
      for (Enemy enemy : targets) {
        if (shuriken.dmgbox().collides(enemy.hitbox())) {
          shuriken.dmgbox().hit(enemy);
          enemy.setState(State.Looping);
          enemy.addEffect(EffectType.Slice, shuriken.x() -25, shuriken.y() - 25, 50, shuriken.isLookingLeft());

          if (shuriken.dmgbox().excluded().size() == 1) combo.add(Move.Shuriken);
        }
      }
      if (-150 > shuriken.x() || shuriken.x() > Dimensions.windowWidth + 150) {
        weapons.remove(shuriken);
        return;
      }
    }
  }
  @Override
  public void updateState() {
    if (parryCooldown.isEmpty()) {
      disableParry();
      if (stateIs(State.Parry)) setState(State.Stand);
      parryCooldown.startsIncreasing();
    } else if (parryCooldown.isReady() && !canParry) canParry = true;

    if (!isOnLastFrame()) return;
    switch (state()) {
      case GetUp:
        if (stun().isExceeded()) setState(MinistickController.isPressed(Touch.F) ? State.Parry : standOrWalk());
        break;
      case Uppercut:
        addX(direction(width()));
        setXAcc(direction(15));
        invertLook();
        setState(standOrSquat());
        break;
      default:
        super.updateState();
    }
  }
  @Override
  public void updateHitbox() {
    if (hasBonus(BonusType.Shield)) {
      hitbox().clear();
      return;
    }
    super.updateHitbox();
  }

  @Override
  public boolean update() {
    boolean hit = false;
    sparkle();
    super.update();
    updateSidekicks();
    updateSparkles();
    updateWeapons();
    if (grapple.isOn()) {
      grapple.resize();
      if (Math.abs(x() - grapple.origin().x()) <= 50 && y() < grapple.origin().y()) disableGrapple();
    }

    if (combo.cooldown().isExceeded()) combo.lose();
    for (Bonus bonus : collectibles) checkIfCollect(bonus);
    for (Stickman target : targets) {
      if (checkIfHit(target)) {
        addToCombo();
        canAdd = false;
        hit = true;
      }
    }
    return hit;
  }
}
