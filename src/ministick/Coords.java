package ministick;

public class Coords {
  private int x, y;

  public Coords(int x, int y) {
    this.x = x;
    this.y = y;
  }
  public Coords(double x, double y) {
    this.x = (int) x;
    this.y = (int) y;
  }

  public int x() {
    return x;
  }
  public int y() {
    return y;
  }

  public void invertY() {
    y = -y;
  }
  public void set(int x, int y) {
    setX(x);
    setY(y);
  }
  public void setX(int x) {
    this.x = x;
  }
  public void setY(int y) {
    this.y = y;
  }
  public void addX(double x) {
    this.x += x;
  }
  public void addY(double y) {
    this.y += y;
  }
  public void add(Vector acc) {
    addX(acc.x());
    addY(acc.y());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Coords)) return false;
    Coords coords = (Coords) o;
    return x == coords.x && y == coords.y;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  public Coords shift(Coords coords) {
    return shift(coords.x, coords.y);
  }
  public Coords shift(int x, int y) {
    return new Coords(this.x + x, this.y + y);
  }
}