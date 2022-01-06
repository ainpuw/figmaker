package com.ainpuw.figmaker;

import com.ainpuw.figmaker.scenarios.Level1;
import com.ainpuw.figmaker.scenarios.Scenario;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    // Configs and global objects.
    private Config config;
    // Current scenario.
    private Scenario scenario;

    @Override
    public void create () {
        config = new Config();
        scenario = new Level1(config);
    }

    @Override
    public void render () {
        /*
          TODO:
          1. Play the animation 1 loop then convert to Box2d worm.
          2. Debug why it cannot maintain only maxStabilizedSegs number of segments.
          3. Make it visually better. The touch.
         */
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
        config.stageBack.act(Math.min(deltaTime, config.maxStageUpdateDelta));
        config.stageBack.getViewport().apply();
        config.spriteBatch.setProjectionMatrix(config.stageBack.getCamera().combined);
        config.shapeRenderer.setProjectionMatrix(config.stageBack.getCamera().combined);
        config.stageBack.draw();

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
        // Scene2D cont.
        /////////////////////////////////////////////

        config.stageFront.act(Math.min(deltaTime, config.maxStageUpdateDelta));
        config.stageFront.getViewport().apply();
        config.stageFront.draw();

        /////////////////////////////////////////////
        // Gameplay
        /////////////////////////////////////////////

        // TODO: This can be put inside worm step.
        // Update bone countdowns. Delete bones/joints when they expire.
        for (WormSegment seg : config.worm.segs)
            seg.updateBoneStabilization(deltaTime);
        // Detect inputs and update bones/joints.
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = config.stageBack.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            config.worm.updateBones(touchPos);
        }

        /////////////////////////////////////////////
        // Debug
        /////////////////////////////////////////////

        // config.debugRenderer.render(config.world, config.stageBack.getCamera().combined);
        // Utils.drawGameBoundingBox(uiConfig, spriteBatch, shapeRenderer);
    }

    @Override
    public void resize (int width, int height) {
        // It is very important to set centerCamera to "false" for ExtendViewport.
        config.stageBack.getViewport().update(width, height, false);
        config.stageFront.getViewport().update(width, height, false);

        // Update worm textures.
        config.spriteBatch.setProjectionMatrix(config.stageBack.getCamera().combined);
        config.spriteBatch.setProjectionMatrix(config.stageFront.getCamera().combined);

    }

    @Override
    public void dispose () {
        config.dispose();
    }
}