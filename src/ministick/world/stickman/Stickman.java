package ministick.world.stickman;

import ministick.Coords;
import ministick.Vector;
import ministick.time.Chrono;
import ministick.time.Timer;
import ministick.window.Dimensions;
import ministick.window.Options;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.*;
import ministick.world.bonus.Bonus;
import ministick.world.effect.Effect;
import ministick.world.effect.EffectType;
import ministick.world.effect.LostHP;
import ministick.world.stickman.enemy.EnemyType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Stickman extends PhysicalObject {
  private final List<Effect> effects = new ArrayList<>();
  private final HealthBar health = new HealthBar(100);
  private final Hitbox hitbox = new Hitbox();
  private DamageBox dmgbox = new DamageBox();
  private double strength = 1, defense = 1;
  private double dexterity = 1, speed = 1;
  private final Timer timer = new Timer();
  private Timer stun = new Timer(0.5);
  private boolean lookingLeft;
  private final String name;
  private State state;
  private int frame;

  public Stickman(EnemyType type, Coords position) {
    this(type.toString(), position, type.weight());
    if (y() < Dimensions.ground) setState(State.Looping);
    setStun(0.4/(type.dexterity() - (1-type.dexterity())/2));
    dexterity = type.dexterity();
    strength = type.strength();
    defense = type.defense();
    speed = type.speed();
  }
  public Stickman(String name, Coords position, double weight) {
    super(position.x(), position.y(), 50, 100, weight);

    this.name = name;
    setState(State.Stand);
  }

  // getters
  public int stateFrames() { return state.nbFrames(); }
  public int direction(double value) { return (int) (value * direction()); }
  public int direction() { return lookingLeft ? -1 : 1; }
  public int frame() {
    return frame;
  }
  public Timer stun() {
    return stun;
  }
  public Timer timer() {
    return timer;
  }
  public State state() {
    return state;
  }
  public double speed() {
    return speed;
  }
  public double weight() {
    return (stateIs(State.Float, State.Looping, State.Hurt) ? 0.9 : 1) * super.weight();
  }
  public Hitbox hitbox() {
    return hitbox;
  }
  public double strength() {
    return strength;
  }
  public double dexterity() {
    return dexterity;
  }
  public HealthBar health() {
    return health;
  }
  public DamageBox dmgbox() {
    return dmgbox;
  }
  public boolean lookingLeft() {
    return lookingLeft;
  }
  public boolean isOnLastFrame() { return frame >= stateFrames()-1; }
  public boolean canNotReleaseDmg(int frame) {
    return !(this.frame == frame && dmgbox().isCleared());
  }
  public boolean stateIsNot(State... states) {
    return !stateIs(states);
  }
  public boolean stateIs(State... states) {
    for (State state : states) if (this.state == state) return true;
    return false;
  }
  public boolean isDead() {
    return stateIs(State.Dead);
  }
  public List<Effect> effects() {
    return effects;
  }
  public State standOrParade() {
    return State.Stand;
  }
  public State standOrSquat() {
    return State.Stand;
  }
  public State standOrWalk() {
    return State.Stand;
  }
  public String toPath() {
    return "data/img/" + name + "/" + state + '_'
               + (lookingLeft ? 'l' : 'r')
               + frame + ".png";
  }

  // setters
  public void setStun(double value) {
    stun = new Timer(value, false);
  }
  public void look(boolean look) {
    lookingLeft = look;
  }
  public void invertLook() {
    lookingLeft = !lookingLeft;
  }
  public void lookLeft() {
    lookingLeft = true;
  }
  public void lookRight() {
    lookingLeft = false;
  }
  public void setDmgbox(Rectangle rect, Vector vector, double dmg, SoundName sound) {
    dmgbox = new DamageBox(rect, this, vector, dmg* strength(), sound);
  }
  public void moveDmgbox(Rectangle rect, Vector vector) {
    dmgbox.moveTo(rect.x, rect.y, this);
    dmgbox.setSize(rect.width, rect.height);
    dmgbox.setVector(vector);
  }
  public void clearDmgbox() {
    dmgbox = new DamageBox();
  }
  public void addEffect(EffectType type, int x, int y, int size, boolean look) {
    effects.add(new Effect(type, x, y, size, look));
  }
  public void addEffect(EffectType type, int x, int y, int size) {
    effects.add(new Effect(type, x() - (width() + x) * (lookingLeft ? 1 : -1) - size/2, y() - height() + y, size, lookingLeft));
  }
  public void addEffect(EffectType type) {
    effects.add(new Effect(type, x() - width(), y() - height(), height(), lookingLeft));
  }
  public void addEffect(Effect effect) {
    effects.add(effect);
  }
  public void decreaseHP(double hp) {
    if (hp == 0) return;
    hp /= defense;
    health.decrease(hp);
    if (health.value() < 1 && !health.isEmpty()) health.increase(1);
    addEffect(new LostHP((int) -hp, this));
    checkIfDead();
  }
  public void resetTimer(double fps) {
    timer.reset(Chrono.fps(Options.fps(fps) / speed));
  }

  // state
  public void setState(State state) {
    if (this.state == State.Dead || this.state == state) return;
    this.state = state;
    frame = 0;
    setFrameSpeed();
    timer.reset();
  }
  public void setState(State state, SoundName sound) {
    new Sound(sound).play();
    setState(state);
  }
  public void nextFrame() {
    if (timer.isExceeded()) {
      frame = (frame+1) % stateFrames();
      timer.reset();
    }
  }
  public void setFrameSpeed() {
    switch (state) {
      case Hurt:
      case Kick:
      case Brush:
      case Punch:
      case Punch2:
      case ParryHurt:
      case ChargingPunch:
      case ChargingUppercut:
        resetTimer(5/3.);
        break;
      case Airkick:
      case Explode:
      case Uppercut:
      case Uppaircut:
      case Squatting:
        resetTimer(5/6.);
        break;
      case Fall:
      case Dash:
      case GetUp:
      case Looping:
      case ReverseDash:
      case ChargingPunch2:
        resetTimer(1/3.);
        break;
      default:
        resetTimer(1/6.);
    }
  }

  // moves
  public abstract void move();
  public void die() {
    setState(State.Explode, SoundName.EnemyDeath);
  }
  public void jump() {
    setState(State.Jump);
    addYAcc(-50);
    landBox().setHeight(0);
  }
  public void punch() {
    setState(State.Punch);
  }
  public void dashPunch() {
    setState(State.DashPunch, SoundName.LowWhoosh);
    addXAcc(direction(15));
  }
  public void uppercut() {
    clearDmgbox();
    setState(State.Uppercut);
  }
  public void uppaircut() {
    setState(State.Uppaircut, SoundName.Whoosh);
    setYAcc(-50);
  }
  @Override
  public void land(int y) {
    super.land(y);

    if (stateIs(State.Jump, State.Airkick, State.Uppaircut, State.Fall)) {
      new Sound(SoundName.Land).play();
      addEffect(EffectType.Land);
      setState(standOrWalk());
    }
    else if (stateIs(State.Float, State.Looping))
      setState(health.isEmpty() ? State.Dead : State.GetUp);
  }
  public void bump(Wall wall) {
    int acc = x() - (wall.x() + wall.width() /2);
    setX(wall.x() + (acc < 0 ? - width()*3/2 : wall.width() + width()/2));
    setXAcc(acc/8.);
  }
  public void skipPlatform() {
    addY(20);
    addYAcc(10);
    setState(State.Fall);
    landBox().moveTo(x(), y() + 10);
  }
  public void getHurt() {
    if (stateIs(State.Looping)) return;
    setState(State.Hurt);
    stun.reset();
  }
  public void getHurt(boolean look) {
    getHurt();
  }
  public void disableParry() {}

  // attack
  public void attack() {
    clearDmgbox();
  }
  public boolean checkIfHit(Stickman target) {
    if (dmgbox.excludes(target) || !dmgbox.collides(target.hitbox)) return false;
    dmgbox.play();
    affectHit(target);
    dmgbox.exclude(target);
    return true;
  }
  public boolean affectHit(Stickman enemy) {
    dmgbox.hit(enemy);
    enemy.look(!lookingLeft);
    return enemy.checkIfDead();
  }
  public boolean checkIfDead() {
    if (stateIs(State.Dead, State.Explode)) return true;
    if (health.isEmpty()) { die(); return true; }
    return false;
  }
  public void checkIfCollect(Bonus bonus) {
  }

  // update
  @Override
  public void sustainFriction() {
    if (stateIs(State.Explode)) return;
    multXAcc(World.friction() * speed);
    if (-0.25 <= xAcc() && xAcc() <= 0.25) setXAcc(0);
    addX(xAcc());
    checkIfBumped();
  }
  public void sustainGravity() {
    if (stateIs(State.Explode)) return;
    addYAcc(weight() * 0.95);  // gravity
    if (yAcc() > 1) landBox().setHeight((int) yAcc());
    else if (yAcc() > 10 && stateIsNot(State.Fall)) setState(State.Fall);

    addY(yAcc());

    // fluid jump
    if (-5 < yAcc() && yAcc() < 0) setYAcc(1);
    multYAcc(World.friction() * (yAcc() < 0 ? 0.95/(weight() + (1-weight())/2) : 1.6*weight()));

    checkIfLanded();
  }
  public void updatePosition() {
    move();
    super.updatePosition();
  }
  private void updateEffects() {
    for (Effect effect : effects) {
      effect.nextFrame();
      if (effect.isOver()) {
        effects.remove(effect);
        return;
      }
    }
  }
  @Override
  public void updateZonebox() {
    zonebox().moveTo(x() - width()/2, y() - height());
  }
  public void updateLandBox() {
    landBox().moveTo(x() - width()/8, y());
  }
  public void updateHitbox() {
    switch (state) {
      case Dead:
      case Explode:
        hitbox.clear();
        break;
      case Brush:
      case Squat:
      case Uppercut:
      case Squatting:
        hitbox.moveTo(x() - width()/2, y() - height()*4/5);
        hitbox.setSize(width(), height()*4/5);
        break;
      case GetUp:
        hitbox.moveTo(x() - width(), y() - height()/2);
        hitbox.setSize(width()*2, height()/2);
        break;
      case Looping:
        hitbox.moveTo(x() - width()*2/3, y() - height()*5/6);
        hitbox.setSize(width()*4/3, height()*2/3);
        break;
      case Hurt:
      case ChargingPunch:
        hitbox.moveTo(x() - width()*2/3, y() - height());
        hitbox.setSize(width()*4/3, height());
        break;
      default:
        hitbox.moveTo(x() - width()/2, y() - height());
        hitbox.setSize(width(), height());
    }
  }
  public void updateState() {
    if (!isOnLastFrame()) return;
    switch (state) {
      case Kick:
      case Dash:
      case Punch:
      case GetUp:
      case Punch2:
      case DashPunch:
      case ReverseDash:
        setState(standOrWalk());
        break;
      case ChargingPunch2:
        setState(State.Punch2);
        break;
      case Jump:
      case Airkick:
      case Uppaircut:
        setState(State.Fall);
        break;
      case Brush:
      case Uppercut:
      case Squatting:
        setState(standOrSquat());
        break;
      case Hurt:
      case ParryHurt:
        if (stun.isExceeded()) setState(standOrParade());
        break;
      case Explode:
        setState(State.Dead);
    }
  }

  public boolean update() {
    updatePosition();
    nextFrame();

    attack();
    updateEffects();
    updateZonebox();
    updateHitbox();
    updateState();
    return false;
  }

}
