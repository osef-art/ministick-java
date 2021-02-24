package ministick.world.stickman.enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum EnemyType {
  Enemy1("1", 5, 5, 5, 5, 5),
  Enemy2("2", 7, 6, 3, 3, 6),
  Enemy3("3", 5, 4, 6, 7, 3),
  Enemy4("4", 4, 3, 7, 6, 5),
  EnemyDash("_dash", 3, 6, 4, 6, 6),
  EnemyDouble("_double", 3, 5, 6, 4, 7),
  ;
  /*
  Enemy1("1", 1,   1,   1,   1,    1),
  Enemy2("2", 1.2, 1.1, 0.7, 0.9,  1.4),
  Enemy3("3", 0.9, 0.8, 1.3, 1.1,  0.75),
  Enemy4("4", 1.4, 0.7, 0.8, 1.05, 1.2),
  ;*/

  private final String name;
  private final double strength, defense, dexterity, speed, weight;

  EnemyType(String num, int str, int def, int dex, int spd, int wgt) {
    name = "enemy" + num;
    dexterity = 1 + (dex - 5) / 6.;
    strength = 1 + (str - 5) / 7.;
    defense = 1 + (def - 5) / 10.;
    weight = 1 + (wgt - 5) / 17.;
    speed = 1 + (spd - 5) / 20.;
  }

  public double strength() {
    return strength;
  }
  public double defense() {
    return defense;
  }
  public double dexterity() {
    return dexterity;
  }
  public double speed() {
    return speed;
  }
  public double weight() {
    return weight;
  }

  @Override
  public String toString() {
    return name;
  }

  public static EnemyType random()  {
    return new ArrayList<>(Arrays.asList(Enemy1, Enemy2, Enemy3, Enemy4)).get(new Random().nextInt(4));
  }
}
