package ministick.time;

public class Chrono {
  private boolean off;
  private double start, headstart = 0;

  public Chrono() {
    start = getTime();
  }
  public Chrono(double headstart) {
    start = getTime();
    this.headstart = headstart;
  }
  public Chrono(boolean mode) {
    this();
    this.off = !mode;
  }

  // getters
  public double value() {
    return getTime() - start + headstart;
  }
  public boolean isOff() {
    return off;
  }

  // setters
  public void on() {
    off = false;
  }
  public void off() {
    off = true;
  }
  public void reset() {
    headstart = 0;
    start = getTime();
  }
  public void reset(double headstart) {
    this.headstart = headstart;
    start = getTime();
  }
  public void addHeadstart(double time) {
    headstart += time;
  }


  private static double getTime() {
    return System.nanoTime() / 1000000000.;
  }
  public static double fps(double fps) {
    return 1. / fps;
  }
  public static void waitMilliseconds(double ms) {
    Chrono timer = new Chrono();
    while ((timer.value())*1000 < ms);
  }
}
