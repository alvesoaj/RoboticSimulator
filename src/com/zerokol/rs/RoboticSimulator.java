package com.zerokol.rs;

import java.awt.Shape;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

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
	private long period = 10;

	private Image background, world, robot;

	private Point screenOrigin = new Point(0, 0);
	private Point worldOrigin = new Point(12, 18);

	private int worldWidth = 800;
	private int worldHeight = 600;

	private Point robotPosition = new Point(132, 138);
	private int robotDirection = 0;
	private int robotRotation = 1;

	private Rectangle upWallColl, rightWallColl, downWallColl, leftWallColl,
			robotColl;

	public RoboticSimulator(String title) {
		super(title);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		this.ggc = gc;

		gc.setShowFPS(false);

		this.provider = new InputProvider(gc.getInput());
		this.provider.addListener(this);

		this.provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), this.quit);

		this.provider.bindCommand(new KeyControl(Input.KEY_UP), this.up);
		this.provider.bindCommand(new KeyControl(Input.KEY_RIGHT), this.right);
		this.provider.bindCommand(new KeyControl(Input.KEY_DOWN), this.down);
		this.provider.bindCommand(new KeyControl(Input.KEY_LEFT), this.left);

		this.background = new Image("assets/screen.png");

		this.world = new Image("assets/world.png");

		this.robot = new Image("assets/robot.png");

		this.upWallColl = new Rectangle(this.worldOrigin.getX(),
				this.worldOrigin.getY(), worldWidth, 30);

		this.rightWallColl = new Rectangle(this.worldOrigin.getX() + worldWidth
				- 30, this.worldOrigin.getY(), 30, worldHeight);

		this.downWallColl = new Rectangle(this.worldOrigin.getX(),
				this.worldOrigin.getY() + worldHeight - 30, worldWidth, 30);

		this.leftWallColl = new Rectangle(this.worldOrigin.getX(),
				this.worldOrigin.getY(), 30, worldHeight);

		this.robotColl = new Rectangle(this.robotPosition.getX(),
				this.robotPosition.getY(), this.robot.getWidth(),
				this.robot.getHeight());

	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		this.background
				.draw(this.screenOrigin.getX(), this.screenOrigin.getY());

		this.world.draw(this.worldOrigin.getX(), this.worldOrigin.getY());

		this.robot.draw(this.robotPosition.getX(), this.robotPosition.getY());

		this.robotColl.setBounds(this.robotPosition.getX(),
				this.robotPosition.getY(), this.robot.getWidth(),
				this.robot.getHeight());
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		this.elapsedTime += delta;

		if (this.elapsedTime > this.period) {
			this.elapsedTime = 0;

			if (this.upPressed) {
				Point p = new Point(this.robotPosition.getX(),
						this.robotPosition.getY());

				p.setX((float) (this.robotPosition.getX() + Math.cos(Math
						.toRadians(this.robotDirection))));

				p.setY((float) (this.robotPosition.getY() + Math.sin(Math
						.toRadians(this.robotDirection))));

				if (!testCollision(p)
						&& !testCollision(new Point(p.getX()
								+ this.robot.getWidth(), p.getY()
								+ this.robot.getHeight()))) {
					this.robotPosition.setX(p.getX());
					this.robotPosition.setY(p.getY());
				}
			}

			if (this.rightPressed) {
				this.robot.rotate(this.robotRotation);
				this.robotDirection += this.robotRotation;

				if (this.robotDirection > 359) {
					this.robotDirection = 0;
				}
			}

			if (this.downPressed) {
				Point p = new Point(this.robotPosition.getX(),
						this.robotPosition.getY());

				p.setX((float) (this.robotPosition.getX() - Math.cos(Math
						.toRadians(this.robotDirection))));

				p.setY((float) (this.robotPosition.getY() - Math.sin(Math
						.toRadians(this.robotDirection))));

				if (!testCollision(p)
						&& !testCollision(new Point(p.getX()
								+ this.robot.getWidth(), p.getY()
								+ this.robot.getHeight()))) {
					this.robotPosition.setX(p.getX());
					this.robotPosition.setY(p.getY());
				}
			}

			if (this.leftPressed) {
				this.robot.rotate(-this.robotRotation);
				this.robotDirection -= this.robotRotation;

				if (this.robotDirection < 0) {
					this.robotDirection = 359;
				}
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

	public static float calculateAngle(float x, float y, float x1, float y1) {
		double angle = Math.atan2(y - y1, x - x1);
		return (float) (Math.toDegrees(angle) - 90);
	}
}
