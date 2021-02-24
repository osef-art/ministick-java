package ministick.world;

import ministick.Coords;
import ministick.Vector;

public abstract class Object {
  private final Vector acc = new Vector();
  private final Coords position, size;
  private final Hitbox zonebox;

  public Object(int x, int y, int width, int height) {
    position = new Coords(x, y);
    size = new Coords(width, height);
    zonebox = new Hitbox(x, y, width, height);
  }

  // getters
  public int x() {
    return position.x();
  }
  public int y() {
    return position.y();
  }
  public int width() {
    return size.x();
  }
  public int height() {
    return size.y();
  }
  public Vector acc() {
    return acc;
  }
  public double xAcc() {
    return acc.x();
  }
  public double yAcc() {
    return acc.y();
  }
  public Hitbox hitbox() {
    return zonebox;
  }
  public Hitbox zonebox() {
    return zonebox;
  }
  public Coords position() {
    return position;
  }

  // setters
  public void setX(int x) {
    position.setX(x);
  }
  public void setY(int y) {
    position.setY(y);
  }
  public void addX(double x) {
    position.addX(x);
  }
  public void addY(double y) {
    position.addY(y);
  }
  public void addAcc(Vector vector) {
    acc.add(vector);
  }
  public void setXAcc(double acc) {
    this.acc.setX(acc);
  }
  public void setYAcc(double acc) {
    this.acc.setY(acc);
  }
  public void addXAcc(double acc) {
    this.acc.addX(acc);
  }
  public void addYAcc(double acc) {
    this.acc.addY(acc);
  }
  public void multXAcc(double acc) {
    this.acc.multX(acc);
  }
  public void multYAcc(double acc) {
    this.acc.multY(acc);
  }
  public void updateZonebox() {
    zonebox.moveTo(x(), y());
  }
}
