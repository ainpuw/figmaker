package com.ainpuw.figmaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends ApplicationAdapter {
    private GameConfig gameConfig = new GameConfig();
    private UIConfig uiConfig = new UIConfig();

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Box2DDebugRenderer debugRenderer;
    private Stage stage;
    private Skin skin;
    private World world;

    private SpineActor background;
    private SpineActor character;
    private DialogueActor dialogue;
    private ProgressActor timeTillNext;
    private ProgressActor redProbability;

    private Worm worm;

    @Override
    public void create () {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);

        // Scene2D section.
        stage = new Stage(new ExtendViewport(uiConfig.w, uiConfig.h));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal(uiConfig.skinFile));
        // Define how texts upscale, "nearest" gives a sharper look than "linear".
        skin.getAtlas().getTextures().iterator().next().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        // Allow libGDX font markup language for font effects.
        skin.getFont("default-font").getData().markupEnabled = true;
        // Size of the font.
        skin.getFont("default-font").getData().setScale(uiConfig.scale);

        // Initialize actors.
        background = new SpineActor(uiConfig.spineActors.get("background"));
        character = new SpineActor(uiConfig.spineActors.get("character"));
        dialogue = new DialogueActor(uiConfig.dialogueActors.get("dialogue"), skin);
        timeTillNext = new ProgressActor(uiConfig.progressActors.get("timeTillNext"), skin);
        redProbability = new ProgressActor(uiConfig.progressActors.get("redProbability"), skin);

        //stage.addActor(background);
        //stage.addActor(character);
        stage.addActor(dialogue);
        stage.addActor(timeTillNext);
        stage.addActor(redProbability);

        // Box2D section.
        world = new World(gameConfig.gravity, true);
        worm = new Worm(world);
    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void render () {
        ScreenUtils.clear(uiConfig.screenR, uiConfig.screenG, uiConfig.screenB, uiConfig.screenA);

        // For debug.
        dialogue.genRandomText();
        timeTillNext.genRandomProgress();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / gameConfig.minFrameRate));
        stage.getViewport().apply();
        stage.draw();

        // SHOULDN'T BE HERE.
        for (int i = 0 ; i < 5; i++) {
            worm.step();
            world.step(Gdx.graphics.getDeltaTime(), 3, 3);
        }
        debugRenderer.render(world, stage.getCamera().combined);

        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

        // For debug.
        drawLine(0, 0, uiConfig.w, uiConfig.h);
        drawLine(0, uiConfig.h, uiConfig.w, 0);
        drawLine(0, 0, uiConfig.w, 0);
        drawLine(0, 0, 0, uiConfig.h);
        drawLine(0, uiConfig.h, uiConfig.w, uiConfig.h);
        drawLine(uiConfig.w, 0, uiConfig.w, uiConfig.h);

    }

    public void drawLine (float x1, float y1, float x2, float y2) {
        shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.line(x1, y1, x2, y2);
        shapeRenderer.end();
    }

    @Override
    public void resize (int width, int height) {
        // It is very important to set centerCamera to "false" for ExtendViewport.
        stage.getViewport().update(width, height, false);
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }
}