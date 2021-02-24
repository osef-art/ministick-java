package ministick.world.effect;

import ministick.Coords;
import ministick.Op;
import ministick.window.Window;

import java.awt.*;
import java.util.Random;

public class Sparkle extends Effect {
  private final Color color;
  private final int speed;
  private int alpha;

  public Sparkle(Rectangle zone, int maxSize, Color color) {
    super(EffectType.Particle,
        Op.randInt(zone.x, zone.x + zone.width),
        Op.randInt(zone.y, zone.y + zone.height),
        Op.randInt(1, maxSize));
    alpha = Op.randInt(200, 255);
    speed = Op.randInt(2, 20);
    this.color = new Color(randomColorValue(color.getRed()),
        randomColorValue(color.getGreen()),
        randomColorValue(color.getBlue()));
  }

  private int randomColorValue(int value) {
    return new Random().nextBoolean() ?
        Math.max(0, Op.randInt(value - 25, value)) :
        Math.min(255, Op.randInt(value, value + 25));
  }

  private Color color(int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  @Override
  public boolean isOver() {
    return alpha <= 0 || super.isOver();
  }

  @Override
  public void nextFrame() {
    super.nextFrame();
    addY(-speed);
    alpha = Math.max(0, alpha - 20);
  }

  @Override
  public void draw(Graphics g, Coords offset) {
    Window.drawRect(g, x() + offset.x(), y() + offset.y(), width(), height(), color(alpha));
  }
}
