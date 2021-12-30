package com.ainpuw.figmaker;

import com.ainpuw.figmaker.scenarios.Intro;
import com.ainpuw.figmaker.scenarios.Level2;
import com.ainpuw.figmaker.scenarios.Scenario;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    // Configs and global objects.
    private Config config;
    // Current scenario.
    private Scenario scenario;

    @Override
    public void create () {
        config = new Config();
        scenario = new Level2(config);
    }

    @Override
    public void render () {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(config.screenR, config.screenG, config.screenB, config.screenA);

        /////////////////////////////////////////////
        // Scenario management
        /////////////////////////////////////////////

        if (scenario != null) {
            // Get the next scenario if the scenario is over.
            if (scenario.isOver()) {
                scenario = scenario.nextScenario();
            }
            // Advance the scenario by one step.
            else {
                scenario.step(deltaTime);
            }
        }
        else {
            // Do nothing.
            // System.out.println("No more scenarios.");
        }

        /////////////////////////////////////////////
        // Scene2D
        /////////////////////////////////////////////

        config.amanager.update(deltaTime);  // FIXME: We shouldn't always run this.
        config.menu.ready(false);
        config.stage.act(Math.min(deltaTime, config.maxStageUpdateDelta));
        config.stage.getViewport().apply();
        config.spriteBatch.setProjectionMatrix(config.stage.getCamera().combined);
        config.shapeRenderer.setProjectionMatrix(config.stage.getCamera().combined);
        config.stage.draw();

        /////////////////////////////////////////////
        // Box2D
        /////////////////////////////////////////////

        if (config.evolveWorld) {
            // FIXME: Why would resize mess up with the physics? Need max update delta?
            config.world.step(deltaTime, config.velocityIterations, config.positionIterations);
            config.worm.step();  // Apply forces to worm to be evolved next rendering.
        }
        Worm.drawWorm(deltaTime, config);

        /////////////////////////////////////////////
        // Debug
        /////////////////////////////////////////////

        // config.debugRenderer.render(config.world, config.stage.getCamera().combined);
        // Utils.drawGameBoundingBox(uiConfig, spriteBatch, shapeRenderer);
    }

    @Override
    public void resize (int width, int height) {
        // It is very important to set centerCamera to "false" for ExtendViewport.
        config.stage.getViewport().update(width, height, false);

        // Stick the menu to the right always.
        config.menu.contents.setX(config.menuPosX + Math.max(0, config.stage.getWidth() - config.w) / 2);
        for (Menu.ToolActor tool : config.menu.segTools) tool.alignDisplayAndDrag();

        // Update worm textures.
        config.spriteBatch.setProjectionMatrix(config.stage.getCamera().combined);
    }

    @Override
    public void dispose () {
        config.dispose();
    }
}