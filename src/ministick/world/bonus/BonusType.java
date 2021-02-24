package ministick.world.bonus;

import java.awt.*;
import java.util.List;
import java.util.Random;

public enum BonusType {
  Bomb(     "bomb",     new Color(250, 100, 100), true),
  Health(   "health",   new Color(250, 100, 150)),
  Kick(     "kick",     new Color(250, 150, 200), 10),
  Punch(    "punch",    new Color(200, 150, 250), 10),
  Shuriken( "shuriken", new Color(100, 150, 250)),
  Shield(   "shield",   new Color(100, 200, 250), 7),
  Freeze(   "freeze",   new Color(150, 250, 250), 5, true),
  Sidekick( "sidekick", new Color(150, 250, 150)),
  ;

  private final Color color;
  private final String name;
  private final double time;
  private final boolean global;
  private static final List<BonusType> VALUES = List.of(values());
  private static final int SIZE = VALUES.size();

  BonusType(String name, Color color, double time, boolean global) {
    this.global = global;
    this.color = color;
    this.name = name;
    this.time = time;
  }
  BonusType(String name, Color color) {
    this(name, color, 0);
  }
  BonusType(String name, Color color, double time) {
    this(name, color, time, false);
  }
  BonusType(String name, Color color, boolean global) {
    this(name, color, 0, global);
  }

  public boolean isGlobal() {
    return global;
  }
  public boolean isPunctual() {
    return time == 0;
  }
  public double time() {
    return time;
  }
  public Color color() {
    return color;
  }
  public Color color(int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  @Override
  public String toString() {
    return name;
  }

  public static BonusType random()  {
    return VALUES.get(new Random().nextInt(SIZE));
  }
}
