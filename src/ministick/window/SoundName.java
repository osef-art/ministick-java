package ministick.window;

import ministick.Op;

public enum SoundName {
  EnemyDeath("enemy_death", 4),
  LowWhoosh("whoosh_low", 3),
  EnemyJump("enemy_jump"),
  Whoosh("whoosh", 4),
  Slice("slice", 2),
  Sliced("sliced"),
  Kick("kick", 5),
  Dash("dash", 3),
  Bomb("bomb", 5),
  Pause("pause"),
  Bonus("bonus"),
  Death("death"),
  Hit("hit", 6),
  Jump("jump"),
  Land("land"),
  Cant("cant"),
  ;

  private final String name;
  private final int variants;

  SoundName(String name, int nb) {
    this.name = name;
    variants = nb;
  }
  SoundName(String name) {
    this(name, 1);
  }

  public String randomVariant() {
    return variants == 1 ? "" : Op.randInt(0, variants - 1) + "";
  }

  @Override
  public String toString() {
    return name;
  }
}
