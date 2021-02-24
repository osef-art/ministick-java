package ministick.time;

import ministick.Gauge;

public class Cooldown extends Gauge {
  private boolean decreasing;
  private final Timer timer;
  private final Chrono chrono;

  public Cooldown() {
    this(0);
  }
  public Cooldown(double max) {
    this(max, max);
  }
  Cooldown(double headstart, double max) {
    super(max);
    timer = new Timer(max, false);
    chrono = new Chrono(headstart);
  }

  @Override
  public double value() {
    return decreasing ? Math.max(0, timer.remainingTime()) : Math.min(max(), chrono.value());
  }

  public boolean isReady() {
    return value() >= max();
  }

  public void startsDecreasing() {
    timer.reset(value());
    decreasing = true;
  }
  public void startsIncreasing() {
    chrono.reset(value());
    decreasing = false;
  }
  public void reset() {
    chrono.reset();
  }
  public void reset(double max) {
    reset();
    setMax(max);
  }

  public void decrease(double ratio) {
    double value = max() * ratio;

    if (decreasing) timer.addHeadstart(value);
    else chrono.addHeadstart(-value);
  }
}
