package ministick.world.bonus;

import ministick.Op;
import ministick.time.Chrono;
import ministick.time.Cooldown;
import ministick.window.Dimensions;
import ministick.window.SoundName;
import ministick.world.Hitbox;
import ministick.world.PhysicalObject;

public class Bonus extends PhysicalObject {
  private final Cooldown timer = new Cooldown(Chrono.fps(8));
  private boolean collected = false;
  private final BonusType type;
  private int frame = 0;

  public Bonus(BonusType type) {
    super(Op.randInt(0, Dimensions.windowWidth), 0, 30, 30);
    this.type = type;
  }

  public BonusType type() {
    return type;
  }
  public Hitbox hitbox() {
    return new Hitbox(x()-width()/2, y()-height(), width(), height(), SoundName.Bonus);
  }
  public boolean isCollected() {
    return collected;
  }
  public boolean isPunctual() {
    return type.isPunctual();
  }
  public boolean isGlobal() {
    return type.isGlobal();
  }

  public void collect() {
    collected = true;
  }
  public void nextFrame() {
    if (timer.isReady()) {
      frame = (frame+1) % 8;
      timer.reset();
    }
  }

  public void updatePosition() {
    super.updatePosition();
    nextFrame();
  }

  public String toPath() {
    return "data/img/bonus/" + type + frame + ".png";
  }

}
