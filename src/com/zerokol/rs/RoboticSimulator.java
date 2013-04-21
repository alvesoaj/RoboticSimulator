package com.zerokol.rs;

import java.awt.Font;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import com.zerokol.rs.actors.Robot;

public class RoboticSimulator extends BasicGame implements
		InputProviderListener {

	private GameContainer ggc;

	private InputProvider provider;

	private Command quit = new BasicCommand("quit");

	private Command up = new BasicCommand("up");
	private boolean upPressed = false;
	private Command right = new BasicCommand("right");
	private boolean rightPressed = false;
	private Command down = new BasicCommand("down");
	private boolean downPressed = false;
	private Command left = new BasicCommand("left");
	private boolean leftPressed = false;

	private long elapsedTime = 1;
	private long period = 15;

	private Image background, world;

	private Point screenOrigin = new Point(0, 0);
	private Point worldOrigin = new Point(12, 18);
	private Point blockMapOrigin = new Point(823, 16);

	private int worldWidth = 800;
	private int worldHeight = 600;

	private Rectangle upWallColl, rightWallColl, downWallColl, leftWallColl;

	private Robot robot;

	private Image blockMap;
	private int blockSizeWorld = 20;
	private int blockMapProportion = 4;
	private int blockSizeMap = blockSizeWorld / blockMapProportion;
	private int blockWSize, blockHSize;
	private int blockMapMatrix[][];

	private Font awtFont;
	private TrueTypeFont fontMedium;

	private Point sensorPoints[];
	private boolean toUpMap = false;

	public RoboticSimulator(String title) {
		super(title);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		this.ggc = gc;

		// gc.setShowFPS(false);

		this.provider = new InputProvider(gc.getInput());
		this.provider.addListener(this);

		this.provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), this.quit);

		this.provider.bindCommand(new KeyControl(Input.KEY_UP), this.up);
		this.provider.bindCommand(new KeyControl(Input.KEY_RIGHT), this.right);
		this.provider.bindCommand(new KeyControl(Input.KEY_DOWN), this.down);
		this.provider.bindCommand(new KeyControl(Input.KEY_LEFT), this.left);

		this.background = new Image("assets/screen.png");

		this.world = new Image("assets/world.png");

		this.robot = new Robot("assets/robot.png", 132, 138, blockSizeWorld);

		this.upWallColl = new Rectangle(this.worldOrigin.getX(),
				this.worldOrigin.getY(), worldWidth, 30);

		this.rightWallColl = new Rectangle(this.worldOrigin.getX() + worldWidth
				- 30, this.worldOrigin.getY(), 30, worldHeight);

		this.downWallColl = new Rectangle(this.worldOrigin.getX(),
				this.worldOrigin.getY() + worldHeight - 30, worldWidth, 30);

		this.leftWallColl = new Rectangle(this.worldOrigin.getX(),
				this.worldOrigin.getY(), 30, worldHeight);

		this.blockMap = new Image(worldWidth / blockMapProportion, worldHeight
				/ blockMapProportion);

		this.blockWSize = worldWidth / blockSizeWorld;
		this.blockHSize = worldHeight / blockSizeWorld;

		this.blockMapMatrix = new int[blockWSize][blockHSize];

		for (int i = 0; i < blockWSize; i++) {
			for (int j = 0; j < blockHSize; j++) {
				this.blockMapMatrix[i][j] = 126;
			}
		}

		drawBlockMap();

		awtFont = new Font("Times New Roman", Font.BOLD, 30);
		fontMedium = new TrueTypeFont(awtFont, false);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		this.background
				.draw(this.screenOrigin.getX(), this.screenOrigin.getY());

		this.world.draw(this.worldOrigin.getX(), this.worldOrigin.getY());

		this.blockMap.draw(this.blockMapOrigin.getX(),
				this.blockMapOrigin.getY());

		this.robot.drawActor(g);

		this.fontMedium.drawString(830, 560, "X: "
				+ (this.robot.getX() - this.worldOrigin.getX()), Color.black);
		this.fontMedium.drawString(830, 590, "Y: "
				+ (this.robot.getY() - this.worldOrigin.getY()), Color.black);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		this.elapsedTime += delta;

		if (this.elapsedTime > this.period) {
			this.elapsedTime = 0;

			checkSensor();

			if (this.upPressed) {
				Point p = new Point(this.robot.getX(), this.robot.getY());

				p.setX((float) (this.robot.getX() + Math.cos(Math
						.toRadians(this.robot.getInclination()))));

				p.setY((float) (this.robot.getY() + Math.sin(Math
						.toRadians(this.robot.getInclination()))));

				if (!testCollision(p)
						&& !testCollision(new Point(p.getX()
								+ this.robot.getWidth(), p.getY()
								+ this.robot.getHeight()))) {
					this.robot.setX(p.getX());
					this.robot.setY(p.getY());
				}
			}

			if (this.rightPressed) {
				this.robot.increaseRotation();
			}

			if (this.downPressed) {
				Point p = new Point(this.robot.getX(), this.robot.getY());

				p.setX((float) (this.robot.getX() - Math.cos(Math
						.toRadians(this.robot.getInclination()))));

				p.setY((float) (this.robot.getY() - Math.sin(Math
						.toRadians(this.robot.getInclination()))));

				if (!testCollision(p)
						&& !testCollision(new Point(p.getX()
								+ this.robot.getWidth(), p.getY()
								+ this.robot.getHeight()))) {
					this.robot.setX(p.getX());
					this.robot.setY(p.getY());
				}
			}

			if (this.leftPressed) {
				this.robot.decreaseRotation();
			}
		}
	}

	@Override
	public void controlPressed(Command cmd) {
		if (cmd == this.quit) {
			this.ggc.exit();
		}

		if (cmd == this.up) {
			this.upPressed = true;
		}
		if (cmd == this.right) {
			this.rightPressed = true;
		}
		if (cmd == this.down) {
			this.downPressed = true;
		}
		if (cmd == this.left) {
			this.leftPressed = true;
		}
	}

	@Override
	public void controlReleased(Command cmd) {
		if (cmd == this.up) {
			this.upPressed = false;
		}
		if (cmd == this.right) {
			this.rightPressed = false;
		}
		if (cmd == this.down) {
			this.downPressed = false;
		}
		if (cmd == this.left) {
			this.leftPressed = false;
		}
	}

	@SuppressWarnings("unused")
	private boolean testCollision(Rectangle r) {
		if (this.collisionRectangleToRetangle(r, this.upWallColl)) {
			return true;
		}
		if (this.collisionRectangleToRetangle(r, this.rightWallColl)) {
			return true;
		}
		if (this.collisionRectangleToRetangle(r, this.downWallColl)) {
			return true;
		}
		if (this.collisionRectangleToRetangle(r, this.leftWallColl)) {
			return true;
		}
		return false;
	}

	private boolean testCollision(Point p) {
		if (this.collisionPointToRetangle(p, this.upWallColl)) {
			return true;
		}
		if (this.collisionPointToRetangle(p, this.rightWallColl)) {
			return true;
		}
		if (this.collisionPointToRetangle(p, this.downWallColl)) {
			return true;
		}
		if (this.collisionPointToRetangle(p, this.leftWallColl)) {
			return true;
		}
		return false;
	}

	private boolean collisionRectangleToRetangle(Rectangle r1, Rectangle r2) {
		if (r1.getMaxY() < r2.getMinY())
			return false;
		if (r1.getMaxX() < r2.getMinX())
			return false;
		if (r1.getMinY() > r2.getMaxY())
			return false;
		if (r1.getMinX() > r2.getMaxX())
			return false;
		return true;
	}

	private boolean collisionPointToRetangle(Point p, Rectangle r) {
		if (p.getX() > r.getMinX() && p.getX() < r.getMaxX()
				&& p.getY() > r.getMinY() && p.getY() < r.getMaxY()) {
			return true;
		}
		return false;
	}

	private void drawBlockMap() throws SlickException {
		for (int i = 0; i < blockWSize; i++) {
			for (int j = 0; j < blockHSize; j++) {
				switch (this.blockMapMatrix[i][j]) {
				case 0:
					this.blockMap.getGraphics().setColor(Color.black);
					break;
				case 254:
					this.blockMap.getGraphics().setColor(Color.white);
					break;
				case 126:
				default:
					this.blockMap.getGraphics().setColor(Color.gray);
				}

				this.blockMap.getGraphics().fillRect(blockSizeMap * i,
						blockSizeMap * j, blockSizeMap * i + blockSizeMap,
						blockSizeMap * j + blockSizeMap);
			}
		}
		this.blockMap.getGraphics().flush();
	}

	private void checkSensor() throws SlickException {
		sensorPoints = this.robot.getSensorPoints();

		for (int t = 0; t < sensorPoints.length; t++) {
			float xNew = sensorPoints[t].getX();

			float yNew = sensorPoints[t].getY();

			xNew -= this.worldOrigin.getX();
			yNew -= this.worldOrigin.getY();

			// System.out.print("x: " + xNew + ", y: " + yNew + "\n");

			int i = (int) (xNew / blockSizeWorld);
			if (i > blockWSize - 1) {
				i = blockWSize - 1;
			} else if (i < 0) {
				i = 0;
			}
			int j = (int) (yNew / blockSizeWorld);
			if (j > blockHSize - 1) {
				j = blockHSize - 1;
			} else if (j < 0) {
				j = 0;
			}

			if (testCollision(new Point(xNew, yNew))) {
				if (this.blockMapMatrix[i][j] != 0) {
					this.blockMapMatrix[i][j] = 0;
					toUpMap = true;
				}
				break;
			} else {
				if (this.blockMapMatrix[i][j] != 254) {
					this.blockMapMatrix[i][j] = 254;
					toUpMap = true;
				}
			}
		}

		if (toUpMap) {
			drawBlockMap();
			toUpMap = false;
		}
	}
}
