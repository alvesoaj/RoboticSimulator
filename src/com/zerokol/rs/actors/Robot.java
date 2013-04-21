package com.zerokol.rs.actors;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

public class Robot {
	private Image robotImage;

	private Point origin;

	private Point sensorPoints[];

	private Rectangle col;

	private int inclination, rotationSpeed;

	private int sensorReach;
	private int blockSizeWorld;

	private int sensorSeguimentation = 4;

	public Robot(String imagePath, float x, float y, int blockSizeWorld)
			throws SlickException {
		super();

		this.robotImage = new Image(imagePath);

		this.origin = new Point(x, y);

		this.inclination = 0;
		this.rotationSpeed = 1;

		this.blockSizeWorld = blockSizeWorld / 2;
		this.sensorReach = sensorSeguimentation * this.blockSizeWorld;

		sensorPoints = new Point[sensorSeguimentation];

		this.col = new Rectangle(this.origin.getX(), this.origin.getY(),
				this.robotImage.getWidth(), this.robotImage.getHeight());

		sensorPoints[0] = new Point(origin.getX() + this.robotImage.getWidth(),
				origin.getY() + this.robotImage.getHeight() / 2);

		for (int t = 1; t < sensorSeguimentation - 1; t++) {
			sensorPoints[t] = new Point(0, 0);
		}

		sensorPoints[sensorSeguimentation - 1] = new Point(origin.getX()
				+ this.robotImage.getWidth() + this.sensorReach, origin.getY()
				+ this.robotImage.getHeight() / 2);
	}

	public synchronized Rectangle getRectangle() {
		return this.col;
	}

	public void drawActor(Graphics g) {
		this.robotImage.draw(this.origin.getX(), this.origin.getY());

		g.setColor(Color.red);

		sensorPoints[0]
				.setX((float) (origin.getX() + this.robotImage.getWidth() / 2 + (Math
						.cos(Math.toRadians(this.inclination))
						* this.robotImage.getWidth() / 2)));

		sensorPoints[0]
				.setY((float) (origin.getY() + this.robotImage.getHeight() / 2 + (Math
						.sin(Math.toRadians(this.inclination))
						* this.robotImage.getHeight() / 2)));

		for (int t = 1; t < sensorSeguimentation; t++) {
			sensorPoints[t].setX((float) (sensorPoints[t - 1].getX() + (Math
					.cos(Math.toRadians(this.inclination))
					* this.blockSizeWorld * t)));

			sensorPoints[t].setY((float) (sensorPoints[t - 1].getY() + (Math
					.sin(Math.toRadians(this.inclination))
					* this.blockSizeWorld * t)));
		}

		// sensorPoints[sensorSeguimentation - 1].setX((float)
		// (sensorPoints[0].getX() + (Math.cos(Math.toRadians(this.inclination))
		// * this.sensorReach)));

		// sensorPoints[sensorSeguimentation - 1].setY((float)
		// (sensorPoints[0].getY() + (Math.sin(Math.toRadians(this.inclination))
		// * this.sensorReach)));

		g.drawLine(sensorPoints[0].getX(), sensorPoints[0].getY(),
				sensorPoints[sensorSeguimentation - 1].getX(),
				sensorPoints[sensorSeguimentation - 1].getY());
	}

	public void increaseRotation() {
		this.robotImage.rotate(this.rotationSpeed);

		this.inclination += this.rotationSpeed;

		if (this.inclination > 359) {
			this.inclination = 0;
		}
	}

	public void decreaseRotation() {
		this.robotImage.rotate(-this.rotationSpeed);

		this.inclination -= this.rotationSpeed;

		if (this.inclination < 0) {
			this.inclination = 359;
		}
	}

	public Point getPosition() {
		return this.origin;
	}

	public float getX() {
		return this.origin.getX();
	}

	public float getY() {
		return this.origin.getY();
	}

	public void setX(float x) {
		this.origin.setX(x);
	}

	public void setY(float y) {
		this.origin.setY(y);
	}

	public int getWidth() {
		return this.robotImage.getWidth();
	}

	public int getHeight() {
		return this.robotImage.getHeight();
	}

	public void setInclination(int inclination) {
		this.inclination = inclination;
	}

	public int getInclination() {
		return this.inclination;
	}

	public Point[] getSensorPoints() {
		return this.sensorPoints;
	}
}
