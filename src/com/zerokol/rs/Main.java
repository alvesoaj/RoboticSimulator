package com.zerokol.rs;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Main {
	private static String title = "AJ' Robotic Simulator v1.0";
	private static int screenWidth = 1030;
	private static int screenHeight = 630;

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new RoboticSimulator(title));

		app.setDisplayMode(screenWidth, screenHeight, false);
		app.start();
	}
}
