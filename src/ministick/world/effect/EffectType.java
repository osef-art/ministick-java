package ministick.world.effect;

import ministick.Op;
import ministick.time.Chrono;

import java.util.ArrayList;
import java.util.Arrays;

public enum EffectType {
  Dash("dash", 8, true),
  Land("land", 4, 20),
  Slice("slice", 8, true),
  HitDiagonal("hitd", 6, true),
  HitVertical("hit_v", 6),
  HitHorizontal("hit_h", 6),
  PunchHit1("hitpunch0_", 8),
  PunchHit2("hitpunch1_", 8),
  PunchHit3("hitpunch2_", 8),
  PunchHit4("hitpunch3_", 8),
  KickHit1("hitkick0_", 8),
  KickHit2("hitkick1_", 8),
  KickHit3("hitkick2_", 8),
  KickHit4("hitkick3_", 8),
  Particle(20),
  LostHP(20),
  ;

  private final int nbFrames;
  private final double speed;
  private final String name;
  private final boolean oriented;

  EffectType(String name, int nbFrames, double fps, boolean oriented) {
    this.name = name;
    this.speed = fps;
    this.nbFrames = nbFrames;
    this.oriented = oriented;
  }
  EffectType(String name, int nbFrames, int fps) {
    this(name, nbFrames, Chrono.fps(fps), false);
  }
  EffectType(String name, int nbFrames, boolean oriented) {
    this(name, nbFrames, Chrono.fps(100), oriented);
  }
  EffectType(String name, int nbFrames) {
    this(name, nbFrames, false);
  }
  EffectType(int nbFrames) {
    this("", nbFrames, false);
  }

  public double speed() {
    return speed;
  }
  public int nbFrames() {
    return nbFrames;
  }
  public boolean oriented() {
    return oriented;
  }
  public static EffectType PunchHit() {
    ArrayList<EffectType> list = new ArrayList<>(Arrays.asList(PunchHit1, PunchHit2, PunchHit3, PunchHit4));
    return list.get(Op.randInt(0, list.size()-1));
  }
  public static EffectType KickHit() {
    ArrayList<EffectType> list = new ArrayList<>(Arrays.asList(KickHit1, KickHit2, KickHit3, KickHit4));
    return list.get(Op.randInt(0, list.size()-1));
  }
  @Override
  public String toString() {
    return name;
  }
}
