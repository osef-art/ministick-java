package ministick;

public class Vector {
  private double x, y;

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }
  public Vector() {
    this(0, 0);
  }

  public double x() {
    return x;
  }
  public double y() {
    return y;
  }

  public void set(int x, int y) {
    setX(x);
    setY(y);
  }
  public void setX(double x) {
    this.x = x;
  }
  public void setY(double y) {
    this.y = y;
  }
  public void add(Vector vct) {
    addX(vct.x);
    addY(vct.y);
  }
  public void addX(double x) {
    setX(this.x + x);
  }
  public void addY(double y) {
    setY(this.y + y);
  }
  public void mult(double fact) {
    multX(fact);
    multY(fact);
  }
  public void multX(double fact) {
    setX(this.x * fact);
  }
  public void multY(double fact) {
    setY(this.y * fact);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Vector)) return false;
    Vector vect = (Vector) o;
    return x == vect.x && y == vect.y;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
