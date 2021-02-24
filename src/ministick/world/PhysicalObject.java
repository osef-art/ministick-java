package ministick.world;

import ministick.window.Dimensions;

public abstract class PhysicalObject extends Object {
  private final Hitbox landBox;
  private final double weight;

  public PhysicalObject(int x, int y, int width, int height, double weight) {
    super(x, y, width, height);
    landBox = new Hitbox(x() - width()/8, y(), width()/4, 20);
    this.weight = weight;
  }
  public PhysicalObject(int x, int y, int width, int height) {
    this(x, y, width, height, 1);
  }

  // getters
  public Hitbox landBox() {
    return landBox;
  }
  public double weight() {
    return weight;
  }

  // setters
  public void sustainFriction() {
    multXAcc(World.friction());
    if (-0.25 <= xAcc() && xAcc() <= 0.25) setXAcc(0);
    addX(xAcc());
    checkIfBumped();
  }
  public void sustainGravity() {
    addYAcc(weight * 0.95);  // gravity
    if (yAcc() > 1) landBox.setHeight((int) yAcc());
    addY(yAcc());

    // fluid jump
    if (-5 < yAcc() && yAcc() < 0) setYAcc(1);
    multYAcc(World.friction() * (yAcc() < 0 ? 1 : 2));

    checkIfLanded();
  }

  public void checkIfLandedOnGround() {
    if (y() > Dimensions.ground) land(Dimensions.ground);
  }
  public void checkIfLandedOnPlatform() {
    if (yAcc() < 0) return;
    for (Platform platform : World.platforms()) {
      if (landBox.collides(platform)) {
        land(platform.y());
        return;
      }
    }
  }
  public void checkIfLanded() {
    checkIfLandedOnPlatform();
    checkIfLandedOnGround();
  }
  public void checkIfBumped() {
    for (Wall wall : World.walls()) {
      if (zonebox().collides(wall)) {
        bump(wall);
        return;
      }
    }
  }

  public void bump(Wall wall) {
    int acc = x() - (wall.x() + wall.width() /2);
    setX(wall.x() + (acc < 0 ? -width() : wall.width()));
    setXAcc(acc);
  }
  public void land(int y) {
    setY(y);
    setYAcc(-yAcc() / 25);
    landBox().setHeight(5);
  }

  public void updateZoneBox() {
    zonebox().moveTo(x() - width()/2, y() - height());
  }
  public void updateLandBox() {
    landBox().moveTo(x(), y());
  }
  public void updatePosition() {
    sustainFriction();
    sustainGravity();
    updateZoneBox();
    updateLandBox();
  }
}
