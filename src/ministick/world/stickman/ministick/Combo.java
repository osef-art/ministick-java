package ministick.world.stickman.ministick;

import ministick.time.Timer;
import ministick.world.stickman.State;

import java.util.ArrayList;

public class Combo extends ArrayList<Move> {
  private final Timer timer = new Timer(1.5);
  private int kicks = 0, punches = 0;
  private boolean dash = false;

  Combo() {
    super();
  }

  public int total() {
    return punches + kicks;
  }
  public double kickRatio() {
    return (double) kicks / total();
  }
  public double punchRatio() {
    return (double) punches / total();
  }
  public boolean containsMoreKicks() {
    return kicks > punches;
  }
  public Timer cooldown() {
    return timer;
  }
  public boolean isAlmostLost() {
    return size() >= 2 && get(0) == get(1);
  }
  public boolean isLost() {
    return size() >= 3 && get(0) == get(1) && get(0) == get(2);
  }
  public String name(int i) {
    if (i > size()) return "";
    return get(i).toString();
  }

  public void add(State state) {
    switch (state) {
      case Dash:
      case ReverseDash:
        dash = true;
        break;
      case Punch:
        punches++;
        if (dash) add(Move.DPunch);
        else add(Move.Punch);
        break;
      case Kick:
        kicks++;
        if (dash) add(Move.DKick);
        else add(Move.Kick);
        break;
      case Jump:
      case Uppercut:
        punches++;
        if (dash) add(Move.DUppercut);
        else add(Move.Uppercut);
        break;
      case Brush:
        kicks++;
        if (dash) add(Move.DBrush);
        else add(Move.Brush);
        break;
      case Airkick:
        kicks++;
        add(Move.Airkick);
        break;
      case Uppaircut:
        punches++;
        add(Move.Uppaircut);
        break;
    }
  }

  @Override
  public boolean add(Move move) {
    super.add(0, move);
    timer.reset(timer.limit() + 0.005);
    dash = false;
    return true;
  }

  public void lose() {
    dash = false;
    clear();
  }
}
