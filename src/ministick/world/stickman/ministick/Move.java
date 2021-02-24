package ministick.world.stickman.ministick;

import ministick.world.bonus.BonusType;

import java.awt.*;

public enum Move {
  Bomb(      "BOMB !!",         BonusType.Bomb),
  Shuriken(  "shuriken hit !!", BonusType.Shuriken),
  Punch(     "punch",           new Color(150, 125, 225)),
  Uppercut(  "uppercut",        new Color(150, 125, 225)),
  Uppaircut( "air uppercut !",  new Color(150, 125, 225)),
  DPunch(    "dash + punch",    new Color(200, 200, 250)),
  DUppercut( "dash + uppercut", new Color(200, 200, 250)),
  Kick(      "kick",            new Color(225, 125, 150)),
  Brush(     "brush",           new Color(225, 125, 150)),
  Airkick(   "air kick !",      new Color(225, 125, 150)),
  DKick(     "dash + kick",     new Color(250, 200, 200)),
  DBrush(    "dash + brush",    new Color(250, 200, 200)),
  ;

  private final String name;
  private final Color color;
  private final boolean bonus;

  Move(String name, Color color, boolean bonus) {
    this.name = name;
    this.color = color;
    this.bonus = bonus;
  }
  Move(String name, Color color) {
    this(name, color, false);
  }
  Move(String name, BonusType bonus) {
    this(name, bonus.color(), true);
  }

  public boolean isABonus() {
    return bonus;
  }

  public Color color(int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  @Override
  public String toString() {
    return name;
  }

}
