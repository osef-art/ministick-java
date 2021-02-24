package ministick.world;

import ministick.Gauge;

public class HealthBar extends Gauge {
  private double value;

  HealthBar(double value, double max) {
    super(max);
    this.value = value;
  }
  public HealthBar(double max) {
    this(max, max);
  }

  @Override
  public double value() {
    return value;
  }

  public void decrease(double n) {
    value = Math.max(value - n, 0);
  }
  public void increase(double n) {
    value = Math.min(value + n, max());
  }
}
