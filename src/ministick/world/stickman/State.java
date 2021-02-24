package ministick.world.stickman;

public enum State {
  ChargingUppercut("charge_upp", 2),
  ChargingPunch2("charge2", 2),
  ChargingPunch("charging", 2),
  ParryHurt("paradehurt", 2),
  DashPunch("dashpunch", 4),
  Squatting("squatting", 4),
  Uppaircut("uppaircut", 8),
  ReverseDash("rdash", 2),
  Uppercut("uppercut", 8),
  Punch2("punch2", 6, 4),
  Looping("looping", 4),
  Airkick("airkick", 8),
  Punch("punch", 6, 4),
  Explode("death", 8),
  Parry("parade", 2),
  Fall("fall", 3, 2),
  Squat("squat", 2),
  Stand("stand", 2),
  Float("float", 2),
  GetUp("getup", 4),
  Brush("brush", 6),
  Dash("dash", 2),
  Dead("dead", 2),
  Hurt("hurt", 2),
  Walk("walk", 2),
  Jump("jump", 4),
  Kick("kick", 8),
  ;

  private final String name;
  private final int nbFrame, nbFrameE;

  State(String name, int nbFrame) {
    this.name = name;
    this.nbFrame = nbFrame;
    nbFrameE = nbFrame;
  }
  State(String name, int nbFrame, int nbFrameE) {
    this.name = name;
    this.nbFrame = nbFrame;
    this.nbFrameE = nbFrameE;
  }

  public int nbFrames() {
    return nbFrame;
  }
  public int nbFramesE() {
    return nbFrameE;
  }

  @Override
  public String toString() {
    return name;
  }
}
