package ministick.window;

import ministick.Coords;
import ministick.Gauge;
import ministick.time.Chrono;
import ministick.time.Timer;
import ministick.window.controller.CameraController;
import ministick.window.controller.MinistickController;
import ministick.window.controller.OptionsController;
import ministick.window.controller.Touch;
import ministick.world.*;
import ministick.world.Object;
import ministick.world.bonus.Bonus;
import ministick.world.bonus.BonusType;
import ministick.world.effect.Effect;
import ministick.world.effect.Sparkle;
import ministick.world.stickman.State;
import ministick.world.stickman.Stickman;
import ministick.world.stickman.enemy.Enemy;
import ministick.world.stickman.ministick.Ministick;
import ministick.world.stickman.sidekick.Sidekick;
import ministick.world.throwable.Shuriken;
import ministick.world.tools.Grapple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Window extends JPanel {
  private static final JFrame frame = new JFrame();
  private static MinistickController ctrl;
  private final Ministick ministick;
  private final World world;
  private static Font font;

  public Window(int width, int height) {
    new Options(width, height);
    world = new World();
    ministick = world.ministick();
    ctrl = new MinistickController(world);

    // font
    try {
      font = Font.createFont(Font.TRUETYPE_FONT, new File("data/fonts/Gotham.ttf"));
    } catch (IOException | FontFormatException e) { e.printStackTrace(); }
    // frame
    initFrame(ctrl, new OptionsController(), new CameraController(world));
  }

  public void initFrame(KeyListener ... listeners) {
    frame.setSize(Dimensions.windowWidth + 15, Dimensions.windowHeight + 35);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);

    for (KeyListener listener : listeners) frame.addKeyListener(listener);
    frame.add(this);
    frame.revalidate();
    frame.setVisible(true);
  }

  public void run() {
    do {
      repaint();
      world.update();
      Chrono.waitMilliseconds(Options.fps());
      if (ministick.isDead()) removeListener();
    } while (!world.gameOver());
  }
  public int xOffset() {
    return world.offset().x();
  }
  public int yOffset() {
    return world.offset().y();
  }
  public static void removeListener() {
    frame.removeKeyListener(ctrl);
    ctrl.initPressed();
  }
  public static void restoreListener() {
    frame.addKeyListener(ctrl);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawBackground(g);
    drawBonuses(g);
    drawEnemies(g);
    drawSidekicks(g);
    drawMinistick(g);
    drawForeground(g);
  }

  // groups
  private void drawPlatforms(Graphics g) {
    for (Platform platform : World.platforms()) drawRect(g, platform.zonebox().shift(world.offset()), new Color(200, 210, 220));
  }
  private void drawSidekicks(Graphics g) {
    for (Sidekick sidekick : new ArrayList<>(ministick.sidekicks())) drawSidekick(g, sidekick);
  }
  private void drawSparkles(Graphics g) {
    for (Sparkle sparkle : new ArrayList<>(ministick.sparkles())) sparkle.draw(g, world.offset());
  }
  private void drawBonuses(Graphics g) {
    for (Bonus bonus : new ArrayList<>(world.bonuses())) drawBonus(g, bonus);
  }
  private void drawEnemies(Graphics g) {
    for (Enemy enemy : new ArrayList<>(world.enemies())) drawEnemy(g, enemy);
  }
  private void drawEffects(Graphics g, Stickman stick) {
    for (Effect effect : new ArrayList<>(stick.effects())) effect.draw(g, world.offset());
  }
  private void drawWeapons(Graphics g) {
    for (Shuriken shuriken : new ArrayList<>(ministick.weapons())) drawImage(g, shuriken.toPath(), shuriken.dmgbox().shift(world.offset()));
  }
  private void drawHooks(Graphics g) {
    for (Hook hook : World.hooks()) drawHook(g, hook);
  }
  private void drawWalls(Graphics g) {
    for (Wall wall : World.walls()) drawRect(g, wall.zonebox().shift(world.offset()), new Color(200, 210, 220));
  }

  // objects
  private void drawMinistick(Graphics g) {
    drawHitboxes(g, ministick);
    drawImage(g, ministick.toPath(), ministick.x() - ministick.width() + xOffset(), ministick.y() - ministick.height() + yOffset(), ministick.height(), ministick.height());

    double timeLeft = ministick.bonusTimer().remainingTime();
    if (ministick.hasBonus(BonusType.Shield) && (timeLeft >= 2 || (int) (timeLeft*10) %2 == 0))
      drawImage(g, "data/img/fx/shield" + (int) (timeLeft*10) %8 + ".png",
        ministick.x() - 3 - ministick.width() + xOffset(), ministick.y() - ministick.height() + yOffset(), ministick.height() + 6, ministick.height() + 6);

    drawGrapple(g, ministick.getGrapple());
    drawEffects(g, ministick);
    drawSparkles(g);
    drawWeapons(g);
  }
  private void drawSidekick(Graphics g, Sidekick sidekick) {
    drawHitboxes(g, sidekick);

    if (sidekick.stateIsNot(State.Explode)) drawHealthBar(g, sidekick, new Color(150, 250, 200));
    drawImage(g, sidekick.toPath(), sidekick.x() - sidekick.width()  + xOffset(), sidekick.y() - sidekick.height()  + yOffset(), sidekick.height(), sidekick.height());
    drawEffects(g, sidekick);
  }
  private void drawGrapple(Graphics g, Grapple grapple) {
    if (grapple.isOff()) return;
    g.setColor(Color.white);
    g.drawLine(grapple.following().x() + xOffset(), grapple.following().y() + yOffset(),
        grapple.origin().x() + xOffset(), grapple.origin().y() + yOffset());
    drawRect(g, grapple.following().x() -5 + xOffset(), grapple.following().y() -5 + yOffset(), 10, 10, new Color(225, 250, 125));
  }
  private void drawEnemy(Graphics g, Enemy enemy) {
    drawHitboxes(g, enemy);

    if (enemy.stateIsNot(State.Explode)) drawHealthBar(g, enemy, new Color(190, 230, 60));
    drawImage(g, enemy.toPath(), enemy.x() - enemy.width()  + xOffset(), enemy.y() - enemy.height()  + yOffset(), enemy.height(), enemy.height());
    drawEffects(g, enemy);
  }
  private void drawBonus(Graphics g, Bonus bonus) {
    drawImage(g, bonus.toPath(), bonus.hitbox().shift(world.offset()));
    drawHitboxes(g, bonus);
  }
  private void drawHook(Graphics g, Hook hook) {
    drawRect(g, hook.x() -5 + xOffset(), hook.y() -5 + yOffset(), 10, 10, new Color(225, 250, 125));
    drawCircle(g, hook.shift(world.offset()), hook.range(), new Color(225, 250, 125, 150));
  }

  // setup
  private void drawBackground(Graphics g) {
    drawRect(g, 0, 0, Dimensions.windowWidth, Dimensions.windowHeight, new Color(75, 75, 100));
    drawText(g, world.score() + "",
        new Rectangle(xOffset()/2, Dimensions.ground/2 + yOffset()/2, Dimensions.windowWidth, 0),
        Dimensions.ground, new Color(255, 255, 255, 85));

    //effects
    if (world.bonusCooldown().isActive())
      drawRect(g, 0, 0, Dimensions.windowWidth, Dimensions.windowHeight, world.lastBonus().color( (int) (150*(world.bonusCooldown().ratio())) ));
    if (world.freezeCooldown().isActive())
      drawGauge(g, 0, 0, Dimensions.windowWidth, Dimensions.windowHeight,
          world.freezeCooldown(),
          BonusType.Freeze.color(100),
          BonusType.Freeze.color(25));

    if (Options.showBackground()) {
      for (int i = -1; i < 3; i++) drawImage(g, "data/img/background/city.png", xOffset()*3/4 + 400*i, Dimensions.ground - 250 + yOffset()*2/3, 400, 300);
      drawRect(g, 0, 0, Dimensions.windowWidth, Dimensions.windowHeight, new Color(150, 125, 175, 150));
    }

    drawPlatforms(g);
    drawHooks(g);
  }
  private void drawForeground(Graphics g) {
    drawWalls(g);
    drawRect(g, -150, Dimensions.ground + yOffset(), Dimensions.windowWidth + 300, 150, new Color(160, 170, 180));

    if (ministick.stateIs(State.Parry, State.ParryHurt) || !ministick.canParry())
      drawGauge(g, ministick.x() - ministick.width()/2 + xOffset(), ministick.y() + 20 + yOffset(), ministick.width(), 10,
        ministick.parryCooldown(),
        ministick.canParry() ? Color.white : new Color(250, 50, 100),
        ministick.canParry() ? new Color(255, 255, 255, 150) : new Color(250, 50, 100, 100));

    drawMiniHealthBar(g);
    drawStats(g);

    if (CameraController.isEnabled()) {
      drawImage(g, "data/img/main/rec.png", 0, 0, Dimensions.windowWidth, Dimensions.windowHeight);
    }
    if (Options.onPause()) {
      drawRect(g, 0, 0, Dimensions.windowWidth, Dimensions.windowHeight, new Color(0, 0, 0, 150));
      drawRect(g, Dimensions.windowWidth/2 - 35, Dimensions.windowHeight/2 - 40, 20, 80, new Color(255, 255, 255, 220));
      drawRect(g, Dimensions.windowWidth/2 + 10, Dimensions.windowHeight/2 - 40, 20, 80, new Color(255, 255, 255, 220));
    }
    if (ministick.isDead()) {
      drawRect(g, 0, 0, Dimensions.windowWidth, Dimensions.windowHeight, new Color(0, 0, 0, 150));
      drawText(g, "GAME OVER", new Rectangle(0, Dimensions.windowHeight/3, Dimensions.windowWidth, 0), 60, Color.white, new Color(250, 50, 100), 5);
      drawText(g, "REPLAY ?", new Rectangle(0, Dimensions.windowHeight*4/5, Dimensions.windowWidth, 0), 20, Color.white);
      drawText(g, (int) (world.chrono().remainingTime() +1) + "", new Rectangle(0, Dimensions.windowHeight*2/3, Dimensions.windowWidth, 0), 200, new Color(255, 255, 255, 200));

      drawImage(g, "data/img/bonus/punch0.png", 10, Dimensions.windowHeight/2 + 45, 30, 30);
      drawImage(g, "data/img/bonus/kick0.png", 180, Dimensions.windowHeight/2 + 45, 30, 30);
      Rectangle rect = new Rectangle(50, Dimensions.windowHeight/2, 120, 120);
      for (int i = 0; i < 10; i++) {
        g.setColor(new Color(150, 125, 225));
        ((Graphics2D) g).draw(new Arc2D.Double(rect.x +i, rect.y -1 +i, rect.width -2*i, rect.height -2*i, 90, 360*ministick.combo().punchRatio(), Arc2D.OPEN));
        g.setColor(new Color(225, 125, 150));
        ((Graphics2D) g).draw(new Arc2D.Double(rect.x +i, rect.y -1 +i, rect.width -2*i, rect.height -2*i, 90, -360*ministick.combo().kickRatio(), Arc2D.OPEN));
      }
      drawText(g, (int) (Math.max(ministick.combo().punchRatio(), ministick.combo().kickRatio())*100) + "%", rect, rect.height/4, Color.white,
          ministick.combo().containsMoreKicks() ? new Color(225, 125, 150) : new Color(150, 125, 225), 3);
      drawText(g, "TOTAL HITS : " + ministick.combo().total(), 50, Dimensions.windowHeight/2 + 150, 15, Color.white);
    }
    if (Options.showKeyboard()) drawKeyboard(g);
  }
  private void drawScore(Graphics g) {
    drawText(g, world.score() + "", Dimensions.windowWidth - 100, 50, 30, Color.white, Color.gray, 4);
    drawImage(g, "data/img/main/kills.png", Dimensions.windowWidth - 50, 20, 40, 40);
  }
  private void drawCombo(Graphics g) {
    int size = ministick.combo().size();
    // gauge
    if (size >= 3) {
      Color color = ministick.combo().isAlmostLost() ? new Color(250, 50, 100) : new Color(255, 255, 255);
      drawText(g, size + "", new Rectangle(Dimensions.windowWidth - 235, 70, 35, 35), 30, color);
      drawText(g, "COMBO !", new Rectangle(Dimensions.windowWidth - 235, 105, 35, 0), 10, color);
      drawGauge(g, Dimensions.windowWidth - 190, 75, 150, 35, ministick.combo().cooldown(),
          ministick.combo(0).color(150), new Color(255, 255, 255, 25));
    }
    // moves
    for (int i = 0; i < Math.min(10, size); i++) {
      int alpha = (int) (size >= 3 ? 255 : Math.max(0, 255 * ministick.combo().cooldown().ratio()));
      if (i == 0) {
        drawText(g, ministick.combo().name(i), Dimensions.windowWidth - 175, 100, 23 + (int) (Math.max(0, ministick.combo().cooldown().ratio() - 0.7) *15),
            new Color(255, 255, 255, alpha), ministick.combo(i).color(alpha), 3);
        continue;
      }
      drawText(g, ministick.combo().name(i), Dimensions.windowWidth - 175, 110 + 23*i, 20,
          ministick.combo(i).isABonus() ? ministick.combo(i).color(225 - i*20) : new Color(255, 255, 255, size >= 3 ? 225 - i*22 : alpha));
    }
  }
  private void drawStats(Graphics g) {
    drawMiniHealthBar(g);
    drawScore(g);
    drawCombo(g);
  }
  private void drawHealthBar(Graphics g, Stickman stick, Color color) {
    drawGauge(g, stick.x() - stick.width()*2/3  + xOffset(),
        stick.y() - stick.height() - 25  + yOffset(),
        stick.width()*4/3, 15,
        stick.health(),
        color,
        new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
    drawText(g, (int) stick.health().value() + "", stick.x() - stick.width()*2/3  + xOffset(),
        stick.y() - stick.height() - 25  + yOffset()+13, 15, Color.white);
  }
  private void drawMiniHealthBar(Graphics g) {
    double ratio = ministick.health().ratio();
    drawGauge(g, 25, 25, Dimensions.windowWidth/2, 25,
        ministick.health(),
        new Color(190 + (int) (50*(1-ratio)), (int) (230 * ratio), 60 + (int) (75*(1-ratio))),
        new Color(190 + (int) (50*(1-ratio)), (int) (230 * ratio), 60 + (int) (75*(1-ratio)), 75));
    if (ministick.hasBonus()) {
      drawGauge(g, 25, 50, Dimensions.windowWidth/2, 5,
          ministick.bonusTimer(),
          ministick.lastBonus().color(),
          ministick.lastBonus().color(50));
      drawImage(g, new Bonus(ministick.lastBonus()).toPath(), Dimensions.windowWidth/2 + 35, 25, 40, 40);
    }
    drawText(g, (int) ministick.health().value() + "", 70, 45, 20, Color.white, new Color(0, 0, 0, 30), 4);
    drawImage(g, "data/img/main/level" + (int) (6*ratio + 0.99) + ".png", 15, 15, 45, 45);

    if (ministick.nbShurikens() > 0) {
      drawText(g, ministick.nbShurikens() + "", 25, 150, 25, Color.white);
      drawImage(g, "data/img/weapons/shuriken0.png", 50, 132, 20, 20);
    }
  }

  // options
  private void drawHitboxes(Graphics g, Object obj) {
    if (!Options.showHitboxes()) return;
    g.setColor(Color.white);
    g.drawRect(obj.zonebox().x + xOffset(), obj.zonebox().y + yOffset(), obj.zonebox().width, obj.zonebox().height);
    drawRect(g, obj.hitbox().shift(world.offset()), new Color(255, 255, 255, 50));
    g.setColor(Color.red);
    g.drawLine(ministick.x() + xOffset(),
        ministick.y() + yOffset(),
        ministick.x() + (int) ministick.xAcc() + xOffset(),
        ministick.y() + (int) ministick.yAcc() + yOffset());

    if (obj instanceof PhysicalObject) {
      PhysicalObject phObj = (PhysicalObject) obj;
      drawRect(g, phObj.landBox().shift(world.offset()), new Color(50, 200, 255, 150));

      if (obj instanceof Stickman) {
        Stickman stick = (Stickman) obj;
        drawRect(g, stick.dmgbox().shift(world.offset()), obj instanceof Enemy ? Color.red : Color.green);

        if (obj instanceof Sidekick) {
          Sidekick sidekick = (Sidekick) obj;
          drawRect(g, sidekick.target().x() + xOffset() -5, sidekick.target().y() + yOffset() - sidekick.target().height()/2, 10, 10, new Color(150, 250, 150));
        }
      }
    }
  }
  private void drawKeyboard(Graphics g) {
    drawKeyboardTouch(g, Touch.Left,  10, Dimensions.windowHeight-35, new Color(255, 255, 255));
    drawKeyboardTouch(g, Touch.Down,  45, Dimensions.windowHeight-35, new Color(255, 255, 255));
    drawKeyboardTouch(g, Touch.Right,  80, Dimensions.windowHeight-35, new Color(255, 255, 255));
    drawKeyboardTouch(g, Touch.Up,  45, Dimensions.windowHeight-70, new Color(255, 255, 255));

    drawKeyboardTouch(g,  Touch.C, 130, Dimensions.windowHeight-35, new Color(150, 125, 225));
    drawKeyboardTouch(g,  Touch.V, 165, Dimensions.windowHeight-35, new Color(225, 125, 150));
  }
  private void drawKeyboardTouch(Graphics g, Touch touch, int x, int y, Color color) {
    if (MinistickController.isPressed(touch)) {
      drawRect(g, x, y + 6, 25, 25, color.darker());
      drawRect(g, x, y + 3, 25, 25, color);
    } else {
      Color shade = color.darker().darker();
      drawRect(g, x, y + 25, 25, 6, new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), 100));
      drawRect(g, x, y, 25, 25, new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
    }
  }


  public static Image img(String path, int w, int h) {
    Image srcImg = new ImageIcon(path).getImage();
    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gg = resizedImg.createGraphics();

    gg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    gg.drawImage(srcImg, 0, 0, w, h, null);
    gg.dispose();

    return resizedImg;
  }

  // shapes
  public static void drawRect(Graphics g, Rectangle rect, Color color) {
    g.setColor(color);
    ((Graphics2D) g).fill(rect);
  }
  public static void drawRect(Graphics g, int x, int y, int width, int height, Color color) {
    g.setColor(color);
    g.fillRect(x, y, width, height);
  }
  public static void drawText(Graphics g, String txt, int x, int y, int size, Color color) {
    g.setFont(font.deriveFont(Font.PLAIN, (float) size));
    g.setColor(color);
    g.drawString(txt, x, y);
  }
  public static void drawText(Graphics g, String txt, int x, int y, int size, Color color, Color shade, int shift) {       // sized shade
    drawText(g, txt, x, y + shift, size, shade);
    drawText(g, txt, x, y, size, color);
  }
  public static void drawText(Graphics g, String txt, Rectangle rect, int size, Color color, Color shade, int shift) {			// centered
    drawText(g, txt, new Rectangle(rect.x, rect.y + shift, rect.width, rect.height), size, shade);
    drawText(g, txt, rect, size, color);
  }
  public static void drawText(Graphics g, String txt, Rectangle rect, int size, Color color) {			// centered
    g.setFont(font.deriveFont(Font.PLAIN, (float) size));
    FontMetrics metrics = g.getFontMetrics(g.getFont());
    int x = rect.x + (rect.width - metrics.stringWidth(txt)) / 2,
        y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

    g.setColor(color);
    g.drawString(txt, x, y);
  }
  public static void drawImage(Graphics g, String path, Rectangle rect) {
    drawImage(g, path, rect.x, rect.y, rect.width, rect.height);
  }
  public static void drawImage(Graphics g, String path, int x, int y, int width, int height) {
    g.drawImage(img(path, width, height), x, y, null);
  }
  public static void drawGauge(Graphics g, int x, int y, int width, int height, Gauge gauge, Color main, Color back) {
    drawGauge(g, x, y, width, height, gauge.ratio(), main, back);
  }
  public static void drawGauge(Graphics g, int x, int y, int width, int height, Timer timer, Color main, Color back) {
    drawGauge(g, x, y, width, height, timer.ratio(), main, back);
  }
  public static void drawGauge(Graphics g, int x, int y, int width, int height, double ratio, Color main, Color back) {
    drawRect(g, x, y, width, height, back);
    drawRect(g, x, y, (int) (width * ratio), height, main);
  }
  public static void drawCircle(Graphics g, int x, int y, int radiusX, int radiusY, int bold, Color color) {
    g.setColor(color);
    for (double i = -bold/2.; i < bold/2.; i++) ((Graphics2D) g).draw( new Ellipse2D.Double(x-radiusX -i, y-radiusY -i, (radiusX+i)*2, (radiusY+i)*2) );
  }
  public static void drawCircle(Graphics g, Coords coords, int radius, Color color) {
    drawCircle(g, coords.x(), coords.y(), radius, radius, 1, color);
  }
  /*
  private void drawDisk(Graphics g, int x, int y, int radiusX, int radiusY, Color color) {
    g.setColor(color);
    ((Graphics2D) g).fill( new Ellipse2D.Double(x-radiusX, y-radiusY, radiusX*2, radiusY*2) );
  }
  private void drawDisk(Graphics g, int x, int y, int radius, Color color) {
    drawDisk(g, x, y, radius, radius, color);
  }
   */
}