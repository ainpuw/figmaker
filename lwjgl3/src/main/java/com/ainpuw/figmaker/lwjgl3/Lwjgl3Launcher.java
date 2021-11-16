package com.ainpuw.figmaker.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ainpuw.figmaker.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		return new Lwjgl3Application(new Main(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("Sons of the Abyss");
		configuration.setWindowedMode(1024, 576);
		configuration.setWindowIcon("worm128.png", "worm64.png", "worm32.png", "worm16.png");
		return configuration;
	}
}