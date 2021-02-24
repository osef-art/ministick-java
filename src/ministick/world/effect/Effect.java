package ministick.world.effect;

import ministick.time.Cooldown;
import ministick.Coords;
import ministick.window.Window;
import ministick.world.Object;

import java.awt.*;

public class Effect extends Object {
  private int frame = 0;
  private final EffectType type;
  private final boolean lookingLeft;
  private final Cooldown timer;

  public Effect(EffectType type, int x, int y, int size, boolean lookingLeft) {
    super(x, y, size, size);
    this.lookingLeft = lookingLeft;
    timer = new Cooldown(type.speed());
    this.type = type;
  }
  public Effect(EffectType type, int x, int y, int size) {
    this(type, x, y, size, false);
  }

  public EffectType type() {
    return type;
  }
  public int frame() {
    return frame;
  }
  public boolean isOver() {
    return frame >= type.nbFrames();
  }
  public void nextFrame() {
    if (timer.isReady()) {
      frame++;
      timer.reset();
    }
  }

  public String toPath() {
    return "data/img/fx/" + type + (type.oriented() ? (lookingLeft ? "_l" : "_r") : "") + frame + ".png";
  }

  public void draw(Graphics g, Coords offset) {
    g.drawImage(Window.img(toPath(), width(), height()), x() + offset.x(), y() + offset.y(), null);
  }
}
