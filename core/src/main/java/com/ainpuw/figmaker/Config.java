package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

import java.util.HashMap;

public class Config {
    ////////////////////////////////////////////////////
    // Game general
    ////////////////////////////////////////////////////
    // Default screen size, but allows ExtendViewport scaling.
    // 16:9 aspect ratio, a balance between laptop and phone screens.
    public final float w = 1024;
    public final float h = 576;
    public final float scale = 1;
    public final float screenR = 246/255f;
    public final float screenG = 255/255f;
    public final float screenB = 240/255f;
    public final float screenA = 1f;
    public Stage stageBack = new Stage(new ExtendViewport(w, h));
    public Stage stageFront = new Stage(new ExtendViewport(w, h));
    public Skin skin;
    // At most update at 30 FPS.
    public final float maxStageUpdateDelta = 1 / 30f;
    public AnimationManager amanager = null;

    // Renderers.
    public SpriteBatch spriteBatch;
    public ShapeRenderer shapeRenderer;
    public SkeletonRenderer skeletonRenderer;
    public Box2DDebugRenderer debugRenderer;

    ////////////////////////////////////////////////////
    // Scene2D parameters
    ////////////////////////////////////////////////////

    // Actors.
    public SpineActor background;
    public SpineActor character;
    public SpineActor wormseg;
    public SpineActor wormhurt;
    public SpineActor wormlvl1;
    public SpineActor wormlvl2;
    public SpineActor wormlvl3;
    public SpineActor wormlvl4;
    public SpineActor wormlvl5;
    public Dialogue dialogueBox;

    // Draw the Spine animation of the initial single worm segment.
    public SpineActor wormOne = null;
    // Only use the skeleton of the Spine animation.
    public SpineActor wormSkeleton = null;

    // Spine animation parameters.
    public final HashMap<String, SpineActorConfig> spineActorConfigs = new HashMap<String, SpineActorConfig>() {{
        put("logo", new SpineActorConfig(
                661/3, 1403/3, 1024/2 - 661/3/2, 650,
                "logo",
                "spine/logo/logo.atlas",
                "spine/logo/logo.json",
                "idle", true));
        put("intro", new SpineActorConfig(
                14644.26f, 3128.68f,512, 800,
                "intro",
                "spine/intro/intro.atlas",
                "spine/intro/intro.json",
                "default", false));
        put("character", new SpineActorConfig(
                68.16f, 177, 70, 370,
                "character",
                "spine/character/character.atlas",
                "spine/character/character.json",
                "intro", false));
        put("portrait", new SpineActorConfig(
                210, 304, 630, 560,  // w, h, x and y are useless here.
                "portrait",
                "spine/portrait/character_portrait.atlas",
                "spine/portrait/character_portrait.json",
                "idle", true));
        put("background", new SpineActorConfig(
                1920, 1080, -480, 850,
                "background",
                "spine/background/background.atlas",
                "spine/background/background.json",
                "intro", false));
        put("wormseg", new SpineActorConfig(
                -1, -1, 512, 75, // Here x and y are center positions!
                "worm",
                "spine/worm/worm.atlas",
                "spine/worm/worm_seg.json",
                "idle", true));
        put("wormhurt", new SpineActorConfig(
                -1, -1, 512, 75, // Here x and y are center positions!
                "worm",
                "spine/worm/worm.atlas",
                "spine/worm/worm_hurt.json",
                "idle", true));
        put("wormlvl1", new SpineActorConfig(
                -1, -1, 512, 75, // Here x and y are center positions!
                "worm",
                "spine/worm/worm.atlas",
                "spine/worm/worm_level1.json",
                "grow", false));
        put("wormlvl2", new SpineActorConfig(
                -1, -1, 512, 75, // Here x and y are center positions!
                "worm",
                "spine/worm/worm.atlas",
                "spine/worm/worm_level4.json",
                "grow", false));
    }};

    // Dialogue box parameters.
    public final HashMap<String, DialogueConfig> dialogueActorConfigs = new HashMap<String, DialogueConfig>() {{
        put("dialogueLabel", new DialogueConfig(
                390, -1, 50, 163,  // x and y are useless here.
                "dialogue",
                0));
    }};
    public final Texture dialogueBackgroundTexture = new Texture("texture/dialogue_background.png");
    public final Vector2 dialogueOffset = new Vector2(210, 330);
    public final float dialogueScale = 0.8f;

    ////////////////////////////////////////////////////
    // General Box2D parameters
    ////////////////////////////////////////////////////

    public Worm worm;

    public final Vector2 gravity = new Vector2(0, 0);
    public World world = new World(gravity, true);
    public final float friction = 0.5f;
    public final int velocityIterations = 1;
    public final int positionIterations = 1;
    // Used to freeze the world when needed.
    public boolean evolveWorld = false;
    // Decide where to add the next segment.

    ////////////////////////////////////////////////////w
    // Worm parameters
    ////////////////////////////////////////////////////

    // Coordinates for the worm pen.
    public final float penCenterX = 512;
    public final float penCenterY = 309;
    public final float penW = 990;
    public final float penH = 495;
    public final float penThickness = 100;

    public final float segMidW = 60;
    public final float segMidH = 20;
    public final float segEndW = 10;
    public final float segEndH = 10;
    public final float segDensity = 1;
    public final float jointLen = 5;
    public final boolean collideConnected = false;
    public final float joinPos = segMidW/2 + segEndW;
    public PolygonShape segShapeL;  // End left.
    public PolygonShape segShapeR;  // End right.
    public PolygonShape segShapeM;  // Middle piece.
    public FixtureDef segFixtureDefL = new FixtureDef();
    public FixtureDef segFixtureDefR = new FixtureDef();
    public FixtureDef segFixtureDefM = new FixtureDef();

    public final Texture segTexture;
    public final Texture shadowTexture;
    public final TextureRegion[][] segTextureRegions;
    public final TextureRegion[][] shadowTextureRegions;
    public final Vector2 segShadowYRange = new Vector2(62, 212);
    public final Vector2 segShadowYRangeRef = new Vector2(62, 576);  // Assumed body Y range.
    public final float adjacentRepulsiveForceCutoff = 60;
    public final float adjacentRepulsiveForceFactor = 10000000;

    ////////////////////////////////////////////////////
    // Subclass definitions
    ////////////////////////////////////////////////////

    public class SpineActorConfig {
        public final float w;
        public final float h;
        public final float x;
        public final float y;
        public final String name;
        public final String atlas;
        public final String skeletonJson;
        public final String defaultAnimation;
        public final boolean loop;

        public SpineActorConfig(float w, float h, float x, float y, String name, String atlas,
                                String skeletonJson, String defaultAnimation, boolean loop) {
            this.w = w;
            this.h = h;
            this.x = x;  // What this means depends on the Spine file.
            this.y = y;  // Same as the above.
            this.name = name;
            this.atlas = atlas;
            this.skeletonJson = skeletonJson;
            this.defaultAnimation = defaultAnimation;
            this.loop = loop;
        }
    }

    public class DialogueConfig {
        public final float w;
        public final float h;  // Can remove.
        public final float x;
        public final float y;
        public final String name;
        public final float bgColor;

        public DialogueConfig(float w, float h, float x, float y, String name,
                              float bgColor) {
            this.w = w;
            this.h = h;
            this.x = x;
            this.y = y;
            this.name = name;
            this.bgColor = bgColor;
        }
    }

    ////////////////////////////////////////////////////
    // Class functions
    ////////////////////////////////////////////////////

    public Config() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        skeletonRenderer = new SkeletonRenderer();
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);

        stageBack.getCamera().position.set(w/2, h/2, 0);
        stageFront.getCamera().position.set(w/2, h/2, 0);
        Gdx.input.setInputProcessor(stageFront);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        skin.getAtlas().getTextures().iterator().next().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skin.getFont("default-font").getData().markupEnabled = true;
        skin.getFont("default-font").getData().setScale(scale);

        // Initialize actors.
        background = new SpineActor(spineActorConfigs.get("background"), skeletonRenderer);
        character = new SpineActor(spineActorConfigs.get("character"), skeletonRenderer);
        wormseg = new SpineActor(spineActorConfigs.get("wormseg"), skeletonRenderer);
        wormhurt = new SpineActor(spineActorConfigs.get("wormhurt"), skeletonRenderer);
        wormlvl1 = new SpineActor(spineActorConfigs.get("wormlvl1"), skeletonRenderer);
        wormlvl2 = new SpineActor(spineActorConfigs.get("wormlvl2"), skeletonRenderer);
        wormlvl3 = new SpineActor(spineActorConfigs.get("wormlvl2"), skeletonRenderer);
        wormlvl4 = new SpineActor(spineActorConfigs.get("wormlvl2"), skeletonRenderer);
        wormlvl5 = new SpineActor(spineActorConfigs.get("wormlvl2"), skeletonRenderer);
        dialogueBox = new Dialogue(this);
        amanager = new AnimationManager(character);

        // Initialize assets to be reused when generating worm segments.
        segShapeL = new PolygonShape();  // End left.
        segShapeR = new PolygonShape();  // End right.
        segShapeM = new PolygonShape();  // Middle piece.
        segFixtureDefL.shape = segShapeL;
        segFixtureDefL.density = segDensity;
        segFixtureDefR.shape = segShapeR;
        segFixtureDefR.density = segDensity;
        segFixtureDefM.shape = segShapeM;
        segFixtureDefM.density = segDensity;
        segTexture = new Texture(Gdx.files.internal("texture/wormseg.png"));
        segTextureRegions = TextureRegion.split(segTexture, segTexture.getWidth(), segTexture.getHeight());
        shadowTexture = new Texture(Gdx.files.internal("texture/shadow.png"));
        shadowTextureRegions = TextureRegion.split(shadowTexture, shadowTexture.getWidth(), shadowTexture.getHeight());
        worm = new Worm(this);
    }

    public void dispose() {
        stageBack.dispose();
        stageFront.dispose();
        skin.dispose();
        dialogueBackgroundTexture.dispose();

        world.dispose();
        segShapeL.dispose();
        segShapeR.dispose();
        segShapeM.dispose();
        segTexture.dispose();
        shadowTexture.dispose();
    }
}

