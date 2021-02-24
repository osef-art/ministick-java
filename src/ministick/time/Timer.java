package ministick.time;

public class Timer extends Chrono {
  private double limit;

  public Timer() {
    this(0);
  }
  public Timer(double limit) {
    this(limit, 0);
  }
  public Timer(double limit, double headstart) {
    super(headstart);
    this.limit = limit;
  }
  public Timer(double limit, boolean active) {
    this(limit, active ? 0 : limit);
  }

  public double limit() {
    return limit;
  }
  public double ratio() {
    return 1 - (value() / limit);
  }
  public double remainingTime() {
    return limit - value();
  }
  public boolean isExceeded() {
    return value() >= limit;
  }
  public boolean isActive() {
    return !isExceeded();
  }

  public void reset(double limit) {
    reset();
    this.limit = limit;
  }
}
