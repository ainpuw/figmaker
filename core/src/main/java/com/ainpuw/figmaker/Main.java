package com.ainpuw.figmaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private GameConfig gameConfig;
    private UIConfig uiConfig;
    private GameState state;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Box2DDebugRenderer debugRenderer;

    private SpineActor background;
    private SpineActor character;
    private DialogueActor dialogueBox;
    private ProgressActor timeTillNext;
    private ProgressActor redProbability;
    private HorizontalGroup toolbox;

    private Worm worm;

    @Override
    public void create () {
        gameConfig = new GameConfig();
        uiConfig = new UIConfig();
        state = new GameState();

        /////////////////////////////////////////////
        // Scene2D section
        /////////////////////////////////////////////

        background = new SpineActor(uiConfig.spineActors.get("background"));
        character = new SpineActor(uiConfig.spineActors.get("character"));
        dialogueBox = new DialogueActor(uiConfig.dialogueActors.get("dialogue"), uiConfig.skin);
        timeTillNext = new ProgressActor(uiConfig.progressActors.get("timeTillNext"), uiConfig.skin);
        redProbability = new ProgressActor(uiConfig.progressActors.get("redProbability"), uiConfig.skin);
        toolbox = new Toolbox(uiConfig, gameConfig, state, dialogueBox);

        // uiConfig.stage.addActor(background);
        // uiConfig.stage.addActor(character);
        uiConfig.stage.addActor(dialogueBox);
        // uiConfig.stage.addActor(timeTillNext);
        // uiConfig.stage.addActor(redProbability);
        uiConfig.stage.addActor(toolbox);
        
        /////////////////////////////////////////////
        // Box2D section
        /////////////////////////////////////////////

        worm = new Worm(gameConfig, uiConfig);

        /////////////////////////////////////////////
        // Other
        /////////////////////////////////////////////

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);

        // Add the necessary stateful objects to game state to be used elsewhere.
        state.wormSegs = worm.segs;
    }

    @Override
    public void dispose () {
        gameConfig.dispose();
        uiConfig.dispose();
    }

    @Override
    public void render () {
        ScreenUtils.clear(uiConfig.screenR, uiConfig.screenG, uiConfig.screenB, uiConfig.screenA);

        // For debug.
        // dialogueBox.genRandomText();
        // timeTillNext.genRandomProgress();

        uiConfig.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / gameConfig.minFrameRate));
        uiConfig.stage.getViewport().apply();
        uiConfig.stage.draw();

        if (state.doBox2DStep) {
            gameConfig.world.step(Gdx.graphics.getDeltaTime(), 1, 1);
            worm.step();
        }
        debugRenderer.render(gameConfig.world, uiConfig.stage.getCamera().combined);

        spriteBatch.setProjectionMatrix(uiConfig.stage.getCamera().combined);
        shapeRenderer.setProjectionMatrix(uiConfig.stage.getCamera().combined);

        // FIXME: For debug.
        utils.drawGameBoundingBox(uiConfig, spriteBatch, shapeRenderer);
    }

    @Override
    public void resize (int width, int height) {
        // It is very important to set centerCamera to "false" for ExtendViewport.
        uiConfig.stage.getViewport().update(width, height, false);
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }
}