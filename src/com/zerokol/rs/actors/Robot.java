package com.zerokol.rs.actors;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

public class Robot {
	public Image robotImage;

	public Point origin, encoderPosition;

	public ArrayList<Point> sensorOrigins;
	public ArrayList<Point> sensorDestinations;

	private Rectangle col;

	public int inclination;
	public int encoderInclination;
	public int rotationSpeed = 1;

	public float leftRadius = (float) 0.1592;
	public float rightRadius = (float) 0.1592;

	public float shaftSize = (float) 0.2;

	public short endodersResolution = 1;

	public short leftRotation, rightRotation;

	public Robot(String imagePath, float x, float y) throws SlickException {
		super();

		this.robotImage = new Image(imagePath);

		this.origin = new Point(x, y);
		this.encoderPosition = new Point(x, y);

		sensorOrigins = new ArrayList<Point>();
		sensorDestinations = new ArrayList<Point>();

		for (int s = 0; s < 5; s++) {
			sensorOrigins.add(new Point(x, y));
			sensorDestinations.add(new Point(x, y));
		}

		this.inclination = 0;
		this.encoderInclination = 0;

		this.leftRotation = 0;
		this.rightRotation = 0;

		this.col = new Rectangle(this.origin.getX(), this.origin.getY(),
				this.robotImage.getWidth(), this.robotImage.getHeight());
	}

	public synchronized Rectangle getRectangle() {
		return this.col;
	}

	public void drawActor(Graphics g) {
		this.robotImage.draw(this.origin.getX(), this.origin.getY());

		g.setColor(Color.red);

		for (int s = 0; s < 5; s++) {
			g.drawLine(sensorOrigins.get(s).getX(),
					sensorOrigins.get(s).getY(), sensorDestinations.get(s)
							.getX(), sensorDestinations.get(s).getY());
		}
	}

	public void increaseRotation() {
		this.robotImage.rotate(this.rotationSpeed);

		this.inclination += this.rotationSpeed;

		this.increaseLeftRotation();

		this.decreaseRightRotation();

		if (this.inclination > 359) {
			this.inclination = 0;
		}

		this.updateEncoderInclination();
	}

	public void decreaseRotation() {
		this.robotImage.rotate(-this.rotationSpeed);

		this.inclination -= this.rotationSpeed;

		this.decreaseLeftRotation();

		this.increaseRightRotation();

		if (this.inclination < 0) {
			this.inclination = 359;
		}

		this.updateEncoderInclination();
	}

	public void increaseLeftRotation() {
		this.leftRotation += this.rotationSpeed;

		if (this.leftRotation == 32767) {
			this.leftRotation = 0;
		}
	}

	public void decreaseLeftRotation() {
		this.leftRotation -= this.rotationSpeed;

		if (this.leftRotation == -32768) {
			this.leftRotation = 0;
		}
	}

	public void increaseRightRotation() {
		this.rightRotation += this.rotationSpeed;

		if (this.rightRotation == 327677) {
			this.rightRotation = 0;
		}
	}

	public void decreaseRightRotation() {
		this.rightRotation -= this.rotationSpeed;

		if (this.rightRotation == -32768) {
			this.rightRotation = 0;
		}
	}

	public float getLeftPulse() {
		return this.leftRotation / this.endodersResolution;
	}

	public float getRightPulse() {
		return this.rightRotation / this.endodersResolution;
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

	public float getEncoderX() {
		return this.encoderPosition.getX();
	}

	public float getEncoderY() {
		return this.encoderPosition.getY();
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

	public void updateEncoderPosition() {
		float newX = (float) (this.encoderPosition.getX() + (this
				.getRightPulse() * this.rightRadius + this.getLeftPulse()
				* this.leftRadius)
				* Math.PI
				/ this.endodersResolution
				* Math.cos(this.encoderInclination));

		this.encoderPosition.setX(newX);

		float newY = (float) (this.encoderPosition.getY() + (this
				.getRightPulse() * this.rightRadius + this.getLeftPulse()
				* this.leftRadius)
				* Math.PI
				/ this.endodersResolution
				* Math.sin(this.encoderInclination));

		this.encoderPosition.setY(newY);
		
		this.leftRotation = 0;
		this.rightRotation = 0;
	}

	public void updateEncoderInclination() {
		this.encoderInclination = (int) (this.encoderInclination + 2
				* Math.PI
				/ this.shaftSize
				* this.endodersResolution
				* (this.getRightPulse() * this.rightRadius - this
						.getLeftPulse() * this.leftRadius));

		if (this.encoderInclination > 359) {
			this.encoderInclination = 0;
		}

		if (this.encoderInclination < 0) {
			this.encoderInclination = 359;
		}
	}
}
