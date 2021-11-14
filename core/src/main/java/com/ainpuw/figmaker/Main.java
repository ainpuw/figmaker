package com.ainpuw.figmaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.Null;

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
    private DialogueActor dialogueBox;
    private ProgressActor timeTillNext;
    private ProgressActor redProbability;
    private HorizontalGroup toolbox;
    private DragAndDrop toolboxDrag;

    private Worm worm;

    @Override
    public void create () {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);

        /////////////////////////////////////////////
        // Scene2D section.
        /////////////////////////////////////////////

        stage = new Stage(new ExtendViewport(uiConfig.w, uiConfig.h));
        stage.getCamera().position.set(uiConfig.w/2, uiConfig.h/2, 0);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal(uiConfig.skinFile));
        skin.getAtlas().getTextures().iterator().next().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        skin.getFont("default-font").getData().markupEnabled = true;
        skin.getFont("default-font").getData().setScale(uiConfig.scale);

        background = new SpineActor(uiConfig.spineActors.get("background"));
        character = new SpineActor(uiConfig.spineActors.get("character"));
        dialogueBox = new DialogueActor(uiConfig.dialogueActors.get("dialogue"), skin);
        timeTillNext = new ProgressActor(uiConfig.progressActors.get("timeTillNext"), skin);
        redProbability = new ProgressActor(uiConfig.progressActors.get("redProbability"), skin);
        toolbox = utils.initToolbox(uiConfig, gameConfig, dialogueBox);
        toolboxDrag = utils.initToolboxDragAndDrop();

        //stage.addActor(background);
        //stage.addActor(character);
        stage.addActor(dialogueBox);
        //stage.addActor(timeTillNext);
        //stage.addActor(redProbability);
        stage.addActor(toolbox);
        
        /////////////////////////////////////////////
        // Box2D section.
        /////////////////////////////////////////////

        world = new World(gameConfig.gravity, true);
        worm = new Worm(gameConfig, uiConfig, stage, world);
    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
        worm.dispose();
    }

    @Override
    public void render () {
        ScreenUtils.clear(uiConfig.screenR, uiConfig.screenG, uiConfig.screenB, uiConfig.screenA);

        // For debug.
        // dialogueBox.genRandomText();
        // timeTillNext.genRandomProgress();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / gameConfig.minFrameRate));
        stage.getViewport().apply();
        stage.draw();

        // SHOULDN'T BE HERE.
        world.step(Gdx.graphics.getDeltaTime(), 1, 1);
        worm.step();
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