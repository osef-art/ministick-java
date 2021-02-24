package ministick;

public abstract class Gauge {
  private double max;

  public Gauge(double max) {
    this.max = max;
  }

  public boolean isFilled() {
    return value() >= max;
  }
  public boolean isEmpty() {
    return value() <= 0;
  }
  public double ratio() {
    return value() / max;
  }
  public double value() {
    return 1;
  }

  public double max() {
    return max;
  }

  public void setMax(double max) {
    this.max = max;
  }
}
