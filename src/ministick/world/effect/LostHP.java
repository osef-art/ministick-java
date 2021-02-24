package ministick.world.effect;

import ministick.Coords;
import ministick.window.Window;
import ministick.world.stickman.Stickman;

import java.awt.*;

public class LostHP extends Effect {
  private final Color color;
  private final int value;
  private int alpha;

  public LostHP(int value, Stickman stick) {
    this(value, stick, new Color(200, 75, 100));
  }
  public LostHP(int value, Stickman stick, Color color) {
    super(EffectType.LostHP, stick.x(), stick.y() - stick.height()*5/4, 20);
    this.value = value;
    this.color = color;
    this.alpha = 255;
  }

  @Override
  public boolean isOver() {
    return alpha <= 0;
  }
  private Color color(int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  @Override
  public void nextFrame() {
    super.nextFrame();
    addY((type().nbFrames() - frame()) / -3.);
    alpha = Math.max(0, alpha - (frame() > 10 ? 20 : 5));
  }

  @Override
  public void draw(Graphics g, Coords offset) {
    Window.drawText(g, (value > 0 ? "+" : "") + value, x() + offset.x() -10, y() + + offset.y() -10, height(), color(alpha));
  }
}
