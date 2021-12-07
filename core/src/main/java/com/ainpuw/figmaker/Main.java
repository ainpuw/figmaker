package com.ainpuw.figmaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Main extends ApplicationAdapter {
    // Config and World global objects.
    private GameConfig gameConfig;
    // Config and Stage global objects.
    private UIConfig uiConfig;

    // Draw debug shapes outside of Stage.
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private SkeletonRenderer skeletonRenderer;
    // Draw debug view of World.
    private Box2DDebugRenderer debugRenderer;

    // Stage Actors.
    private SpineActor background;
    private SpineActor character;
    private Dialogue dialogueBox;
    private ProgressActor timeTillNext;
    private ProgressActor redProbability;
    private Menu menu;

    // World Bodies.
    private Worm worm;

    // Cooldowns.
    private AnimationManager animationManager;

    @Override
    public void create () {
        gameConfig = new GameConfig();
        uiConfig = new UIConfig();

        /////////////////////////////////////////////
        // Scene2D
        /////////////////////////////////////////////

        background = new SpineActor(uiConfig.spineActorConfigs.get("background"));
        character = new SpineActor(uiConfig.spineActorConfigs.get("character"));
        dialogueBox = new Dialogue(uiConfig);
        timeTillNext = new ProgressActor(uiConfig.progressActorConfigs.get("timeTillNext"), uiConfig.skin);
        redProbability = new ProgressActor(uiConfig.progressActorConfigs.get("redProbability"), uiConfig.skin);
        menu = new Menu(gameConfig, uiConfig);
        uiConfig.dialogueBox = dialogueBox;

        uiConfig.stage.addActor(background);
        uiConfig.stage.addActor(character);
        uiConfig.stage.addActor(menu.contents);
        dialogueBox.addToStage();

        // uiConfig.stage.addActor(timeTillNext);
        // uiConfig.stage.addActor(redProbability);

        /////////////////////////////////////////////
        // Box2D
        /////////////////////////////////////////////

        worm = new Worm(gameConfig, uiConfig);
        gameConfig.worm = worm;
        gameConfig.wormSegs = worm.segs;

        /////////////////////////////////////////////
        // Others
        /////////////////////////////////////////////

        animationManager = new AnimationManager(character);
        uiConfig.amanager = animationManager;
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        skeletonRenderer = new SkeletonRenderer();
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
    }

    @Override
    public void render () {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(uiConfig.screenR, uiConfig.screenG, uiConfig.screenB, uiConfig.screenA);
        animationManager.update(deltaTime);

        /////////////////////////////////////////////
        // Scene2D
        /////////////////////////////////////////////

        menu.ready(false);
        uiConfig.stage.act(Math.min(deltaTime, uiConfig.maxStageUpdateDelta));
        uiConfig.stage.getViewport().apply();
        spriteBatch.setProjectionMatrix(uiConfig.stage.getCamera().combined);
        shapeRenderer.setProjectionMatrix(uiConfig.stage.getCamera().combined);
        uiConfig.stage.draw();

        /////////////////////////////////////////////
        // Box2D
        /////////////////////////////////////////////

        if (gameConfig.evolveWorld) {
            // FIXME: Why would resize mess up with the physics? Need max update delta?
            gameConfig.world.step(deltaTime, gameConfig.velocityIterations, gameConfig.positionIterations);
            worm.step();  // Apply forces to worm to be evolved next rendering.
        }
        Worm.drawWorm(deltaTime, gameConfig, spriteBatch, shapeRenderer, skeletonRenderer);

        /////////////////////////////////////////////
        // Dialogue.
        /////////////////////////////////////////////
        if (Gdx.input.isTouched())
            dialogueBox.label.skipToTheEnd();

        /////////////////////////////////////////////
        // Debug
        /////////////////////////////////////////////

        // FIXME: For debug.
        // debugRenderer.render(gameConfig.world, uiConfig.stage.getCamera().combined);
        // Utils.drawGameBoundingBox(uiConfig, spriteBatch, shapeRenderer);
    }

    @Override
    public void resize (int width, int height) {
        // It is very important to set centerCamera to "false" for ExtendViewport.
        uiConfig.stage.getViewport().update(width, height, false);

        // Stick the menu to the right always.
        menu.contents.setX(uiConfig.menuPosX + Math.max(0, uiConfig.stage.getWidth() - uiConfig.w) / 2);
        for (Menu.ToolActor tool : menu.segTools) tool.alignDisplayAndDrag();

        // Update worm textures.
        spriteBatch.setProjectionMatrix(uiConfig.stage.getCamera().combined);
    }

    @Override
    public void dispose () {
        gameConfig.dispose();
        uiConfig.dispose();
    }
}