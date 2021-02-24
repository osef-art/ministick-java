package ministick.world;

import ministick.Coords;
import ministick.Op;
import ministick.time.Timer;
import ministick.window.Dimensions;
import ministick.window.Options;
import ministick.window.Sound;
import ministick.window.SoundName;
import ministick.world.bonus.Bonus;
import ministick.world.bonus.BonusType;
import ministick.world.effect.LostHP;
import ministick.world.stickman.State;
import ministick.world.stickman.Stickman;
import ministick.world.stickman.enemy.Enemy;
import ministick.world.stickman.enemy.EnemyDash;
import ministick.world.stickman.enemy.EnemyDouble;
import ministick.world.stickman.ministick.Ministick;
import ministick.world.stickman.ministick.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World {
  private int score = 0;
  private BonusType lastBonus;
  private final Camera camera;
  private final Ministick Matteo_t_as_pas_dis_Greco;
  private final static double friction = 0.8;
  private final List<Bonus> bonuses = new ArrayList<>();
  private final List<Enemy> enemies = new ArrayList<>();
  private final static List<Hook> hooks = new ArrayList<>();
  private final static List<Wall> walls = new ArrayList<>();
  private final static List<Platform> platforms = new ArrayList<>();
  private final Timer bonusEffectTimer = new Timer(0.5, false);
  private final Timer newBonusTimer = new Timer(5);
  private final Timer freezeTimer = new Timer(5, false);
  private final Timer chrono = new Timer();

  public World() {
    Matteo_t_as_pas_dis_Greco = new Ministick(this);
    camera  = new Camera(Matteo_t_as_pas_dis_Greco);
    addEnemy();
    addBonus();
    addBonus();

    addPlatforms(
      new Platform(Dimensions.windowWidth/2 - 100, Dimensions.windowHeight/4, 200),
      new Platform(Dimensions.windowWidth/4 - 100, Dimensions.windowHeight*2/4, 200),
      new Platform(Dimensions.windowWidth*3/4 - 100, Dimensions.windowHeight*2/4, 200),
      new Platform(-175, Dimensions.windowHeight*3/4, 200),
      new Platform(Dimensions.windowWidth - 25, Dimensions.windowHeight*3/4, 200)
    );
    addWalls(
      new Wall(-175, -500, -500, Dimensions.windowHeight + 500),
      new Wall(Dimensions.windowWidth + 175, -500, 500, Dimensions.windowHeight + 500)
    );
    addHooks(
      new Hook(Dimensions.windowWidth/2, Dimensions.windowHeight -200, 200)
    );
  }

  // getters
  public int score() {
    return score;
  }
  public Timer chrono() {
    return chrono;
  }
  public Camera camera() {
    return camera;
  }
  public Coords offset() {
    return camera.offset();
  }
  public boolean gameOver() {
    return Matteo_t_as_pas_dis_Greco.isDead() && chrono.isExceeded();
  }
  public List<Enemy> enemies() {
    return enemies;
  }
  public List<Bonus> bonuses() {
    return bonuses;
  }
  public Ministick ministick() {
    return Matteo_t_as_pas_dis_Greco;
  }
  public BonusType lastBonus() {
    return lastBonus;
  }
  public Timer freezeCooldown() {
    return freezeTimer;
  }
  public Timer bonusCooldown() {
    return bonusEffectTimer;
  }
  public static double friction() {
    return friction;
  }
  public static List<Wall> walls() {
    return walls;
  }
  public static List<Hook> hooks() {
    return hooks;
  }
  public static List<Platform> platforms() {
    return platforms;
  }

  // setters
  private void shake(int y, double time) {
    camera.startShaking(0, y, time);
  }
  private void addBonus() {
    bonuses.add(new Bonus(BonusType.random()));

  }
  private void addEnemy() {
    switch (Op.randInt(0, 5)) {
      case 1:
        enemies.add(new EnemyDash(Matteo_t_as_pas_dis_Greco));
        break;
      case 2:
        enemies.add(new EnemyDouble(Matteo_t_as_pas_dis_Greco));
        break;
      default:
        enemies.add(new Enemy(Matteo_t_as_pas_dis_Greco));
    }
  }
  private void addNewEnemy() {
    if (score %15 == 0 || score == 1 || score == 5) enemies.add(new EnemyDash(Matteo_t_as_pas_dis_Greco));
  }
  private void addPlatforms(Platform ... platforms) {
    World.platforms.addAll(Arrays.asList(platforms));
  }
  private void addWalls(Wall ... walls) {
    World.walls.addAll(Arrays.asList(walls));
  }
  private void addHooks(Hook ... hooks) {
    World.hooks.addAll(Arrays.asList(hooks));
  }
  private void activateBonus(Bonus bonus) {
    lastBonus = bonus.type();
    bonusEffectTimer.reset();
    if (!bonus.isGlobal()) return;

    switch (bonus.type()) {
      case Health:
        if (Matteo_t_as_pas_dis_Greco.health().isFilled()) return;
        Matteo_t_as_pas_dis_Greco.health().increase(20);
        Matteo_t_as_pas_dis_Greco.addEffect(new LostHP(10, Matteo_t_as_pas_dis_Greco, new Color(200, 250, 50)));
        break;
      case Bomb:
        shake(8, 0.7);
        Matteo_t_as_pas_dis_Greco.combo().add(Move.Bomb);
        new Sound(SoundName.Bomb).play();
        for (Stickman enemy : enemies) {
          enemy.setState(State.Looping);
          enemy.decreaseHP(25);
        }
        break;
      case Freeze:
        freezeTimer.reset();
        break;
    }
  }

  private void updateBonuses() {
    for (Bonus bonus : bonuses) {
      bonus.updatePosition();
      if (bonus.isCollected()) {
        activateBonus(bonus);
        bonuses.remove(bonus);
        newBonusTimer.reset();
        return;
      }
    }
    if (bonuses.size() == 0 && newBonusTimer.isExceeded()) addBonus();
  }
  private void updateEnemies() {
    for (Enemy enemy : enemies) {
      enemy.update();
      if (enemy.isDead()) {
        enemies.remove(enemy);
        score++;
        addEnemy();
        addNewEnemy();
        return;
      }
    }
  }

  public void update() {
    if (Options.onPause()) return;
    if (Matteo_t_as_pas_dis_Greco.update()) shake(3, 0.4);
    if (Matteo_t_as_pas_dis_Greco.isDead() && chrono.isExceeded()) chrono.reset(5);
    camera.update();
    updateBonuses();
    if (freezeTimer.isExceeded() || ((int) (freezeTimer.value()*20)) %5 == 0) updateEnemies();
  }
}
