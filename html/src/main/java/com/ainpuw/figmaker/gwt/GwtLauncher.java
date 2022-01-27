package com.ainpuw.figmaker.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.ainpuw.figmaker.Main;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
		@Override
		public GwtApplicationConfiguration getConfig () {
			// Resizable application, uses available space in browser
			return new GwtApplicationConfiguration(true);
		}

		@Override
		public ApplicationListener createApplicationListener () { 
			return new Main();
		}

		@Override
		public void onModuleLoad () {
			FreetypeInjector.inject(new OnCompletion() {
				public void run () {
					GwtLauncher.super.onModuleLoad();
				}
			});
		}
}