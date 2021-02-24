package ministick.world.throwable;

import ministick.Vector;
import ministick.window.SoundName;
import ministick.world.DamageBox;
import ministick.world.PhysicalObject;

public class Shuriken extends PhysicalObject {
  private final boolean lookingLeft;
  private final DamageBox dmgbox;
  private int frame = 0;

  public Shuriken(int x, int y, boolean left) {
    super(x, y, 15, 15);
    lookingLeft = left;
    dmgbox = new DamageBox(x, y, 15, 15, new Vector(left ? -5 : 5, -15), 15, SoundName.Sliced);
  }

  public boolean isLookingLeft() {
    return lookingLeft;
  }

  public String toPath() {
    return "data/img/weapons/shuriken" + frame + ".png";
  }

  public DamageBox dmgbox() {
    return dmgbox;
  }

  public void update() {
    setX(x() + 50*(lookingLeft ? -1 : 1));
    dmgbox.moveTo(x(), y());
    frame = (frame+1) %2;
  }
}
