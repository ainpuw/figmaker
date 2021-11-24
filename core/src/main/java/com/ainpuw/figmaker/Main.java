package com.ainpuw.figmaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    // Config and World global objects.
    private GameConfig gameConfig;
    // Config and Stage global objects.
    private UIConfig uiConfig;

    // Draw debug shapes outside of Stage.
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    // Draw debug view of World.
    private Box2DDebugRenderer debugRenderer;

    // Stage Actors.
    private BackgroundActor background;
    private SpineActor character;
    private DialogueActor dialogueBox;
    private ProgressActor timeTillNext;
    private ProgressActor redProbability;
    private ToolboxActor toolbox;

    // World Bodies.
    private Worm worm;

    @Override
    public void create () {
        gameConfig = new GameConfig();
        uiConfig = new UIConfig();

        /////////////////////////////////////////////
        // Scene2D
        /////////////////////////////////////////////

        background = new BackgroundActor(uiConfig);
        character = new SpineActor(uiConfig.spineActorConfigs.get("character"));
        dialogueBox = new DialogueActor(uiConfig.dialogueActorConfigs.get("dialogue"), uiConfig.skin);
        timeTillNext = new ProgressActor(uiConfig.progressActorConfigs.get("timeTillNext"), uiConfig.skin);
        redProbability = new ProgressActor(uiConfig.progressActorConfigs.get("redProbability"), uiConfig.skin);
        toolbox = new ToolboxActor(gameConfig, uiConfig);
        uiConfig.dialogueBox = dialogueBox;

        uiConfig.stage.addActor(background);
        uiConfig.stage.addActor(character);
        uiConfig.stage.addActor(dialogueBox);
        uiConfig.stage.addActor(toolbox);
        // uiConfig.stage.addActor(timeTillNext);
        // uiConfig.stage.addActor(redProbability);

        /////////////////////////////////////////////
        // Box2D
        /////////////////////////////////////////////

        worm = new Worm(gameConfig, uiConfig);
        gameConfig.wormSegs = worm.segs;

        /////////////////////////////////////////////
        // Others
        /////////////////////////////////////////////

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
    }

    @Override
    public void render () {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(uiConfig.screenR, uiConfig.screenG, uiConfig.screenB, uiConfig.screenA);

        /////////////////////////////////////////////
        // Scene2D
        /////////////////////////////////////////////

        toolbox.ready(false);  // This only runs for the first 30 frames.
        uiConfig.stage.act(Math.min(deltaTime, uiConfig.maxStageUpdateDelta));
        uiConfig.stage.getViewport().apply();
        uiConfig.stage.draw();

        /////////////////////////////////////////////
        // Box2D
        /////////////////////////////////////////////

        if (gameConfig.evolveWorld) {
            // FIXME: Why would resize mess up with the physics? Need max update delta?
            gameConfig.world.step(deltaTime, gameConfig.velocityIterations, gameConfig.positionIterations);
            worm.step();  // Apply forces to worm to be evolved next rendering.
        }

        /////////////////////////////////////////////
        // Debug
        /////////////////////////////////////////////

        // FIXME: For debug.
        debugRenderer.render(gameConfig.world, uiConfig.stage.getCamera().combined);
        spriteBatch.setProjectionMatrix(uiConfig.stage.getCamera().combined);
        shapeRenderer.setProjectionMatrix(uiConfig.stage.getCamera().combined);
        Utils.drawGameBoundingBox(uiConfig, spriteBatch, shapeRenderer);
    }

    @Override
    public void resize (int width, int height) {
        // It is very important to set centerCamera to "false" for ExtendViewport.
        uiConfig.stage.getViewport().update(width, height, false);
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose () {
        gameConfig.dispose();
        uiConfig.dispose();
    }
}