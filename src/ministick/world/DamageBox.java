package ministick.world;

import ministick.Vector;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.stickman.Stickman;

import java.awt.*;
import java.util.ArrayList;

public class DamageBox extends Hitbox {
  private final ArrayList<Stickman> excluded = new ArrayList<>();
  private final double damage;
  private Vector vector;

  public DamageBox() {
    super();
    damage = 0;
    vector = new Vector();
  }

  private DamageBox(int x, int y, int width, int height, Vector vector, double dmg, Sound sound) {
    super(x, y, width, height, sound);
    this.vector = vector;
    this.damage = dmg;
  }
  public DamageBox(Rectangle hitbox, Stickman stick, Vector vector, double dmg, SoundName sound) {
    this(stick.x() + (stick.lookingLeft() ? - (hitbox.width + hitbox.x) : hitbox.x),
        (stick.y() - stick.height()) + hitbox.y,
        hitbox.width, hitbox.height,
        vector, dmg, new Sound(sound));
  }
  public DamageBox(int x, int y, int width, int height, Vector vector, double dmg, SoundName sound) {
    this(x, y, width, height, vector, dmg, new Sound(sound));
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DamageBox dmgbox = (DamageBox) o;
    return x == dmgbox.x && y == dmgbox.y && width == dmgbox.width && height == dmgbox.height;
  }

  // getters
  public boolean isCleared() {
    return equals(new DamageBox());
  }
  public ArrayList<Stickman> excluded() {
    return excluded;
  }
  public boolean excludes(Stickman stickman) {
    for (Stickman stick : excluded) if (stickman == stick) return true;
    return false;
  }

  // setters
  public void setVector(Vector vector) {
    this.vector = vector;
  }
  public void exclude(Stickman stick) {
    excluded.add(stick);
  }
  public void hit(Stickman stick) {
    exclude(stick);
    stick.addAcc(vector);
    stick.decreaseHP(damage);
    play();
  }
}
