package ministick.world;

import ministick.Coords;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.stickman.Stickman;

import java.awt.*;

public class Hitbox extends Rectangle {
  private final Sound sound;

  public Hitbox() {
    super();
    sound = null;
  }
  public Hitbox(int x, int y, int width, int height) {
    this(x, y, width, height, (Sound) null);
  }
  public Hitbox(int x, int y, int width, int height, Sound sound) {
    super(x, y, width, height);
    this.sound = sound;
  }
  public Hitbox(int x, int y, int width, int height, SoundName sound) {
    this(x, y, width, height, new Sound(sound));
  }

  // getters
  public Hitbox shift(Coords offset) {
    return shift(offset.x(), offset.y());
  }
  public Hitbox shift(int x, int y) {
    return new Hitbox(this.x + x, this.y + y, width, height);
  }

  public boolean collides(Object obj) {
    return collides(obj.zonebox());
  }
  public boolean contains(Hitbox hitbox) {
    return x < hitbox.x && hitbox.x + hitbox.width < x + width &&
           y < hitbox.y && hitbox.y + hitbox.height < y + height;
  }
  public boolean collides(Hitbox hitbox) {
    return x - hitbox.width < hitbox.x && hitbox.x < x + width &&
           y - hitbox.height < hitbox.y && hitbox.y < y + height;
  }

  // setters
  public void moveTo(int x, int y, Stickman stickman) {
    this.x = stickman.x() + (stickman.lookingLeft() ? - (width + x) : x);
    this.y = (stickman.y() - stickman.height()) + y;
  }
  public void moveTo(int x, int y) {
    this.x = x;
    this.y = y;
  }
  public void setHeight(int h) {
    height = h;
  }
  public void setSize(int width, int height) {

    this.width = width;
    this.height = height;
  }
  public void play() {
    if (sound != null) sound.play();
  }

  public void clear() {
    setSize(0, 0);
  }
}
