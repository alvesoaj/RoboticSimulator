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

	public Point origin;

	public ArrayList<Point> sensorOrigins;
	public ArrayList<Point> sensorDestinations;

	private Rectangle col;

	public int inclination, rotationSpeed;

	public Robot(String imagePath, float x, float y) throws SlickException {
		super();

		this.robotImage = new Image(imagePath);

		this.origin = new Point(x, y);

		sensorOrigins = new ArrayList<Point>();
		sensorDestinations = new ArrayList<Point>();

		for (int s = 0; s < 5; s++) {
			sensorOrigins.add(new Point(x, y));
			sensorDestinations.add(new Point(x, y));
		}

		this.inclination = 0;
		this.rotationSpeed = 1;

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
