package com.zerokol.rs.actors;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

public class Robot {
	private Image robotImage;

	private Point origin;

	private Rectangle col;

	private int inclination, rotationSpeed;

	public Robot(String imagePath, float x, float y) throws SlickException {
		super();
		this.robotImage = new Image(imagePath);
		this.origin = new Point(x, y);

		this.inclination = 0;
		this.rotationSpeed = 1;
		this.col = new Rectangle(this.origin.getX(), this.origin.getY(),
				this.robotImage.getWidth(), this.robotImage.getHeight());
	}

	public synchronized Rectangle getRectangle() {
		return this.col;
	}

	public void drawActor() {
		this.robotImage.draw(this.origin.getX(), this.origin.getY());
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
}
