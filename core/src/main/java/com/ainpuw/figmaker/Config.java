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
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
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
    public Stage stage = new Stage(new ExtendViewport(w, h));
    public Skin skin;
    public DragAndDrop toolboxDragAndDrop;
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
    public SpineActor wormlvl2;
    public Dialogue dialogueBox;
    public ProgressActor timeTillNext;
    public ProgressActor redProbability;
    public Menu menu;

    // Draw the Spine animation of the initial single worm segment.
    public SpineActor wormOne = null;
    // Only use the skeleton of the Spine animation.
    public SpineActor wormSkeleton = null;

    public final String skinFile = "skin/uiskin.json";

    // Spine animation parameters.
    public final HashMap<String, SpineActorConfig> spineActorConfigs = new HashMap<String, SpineActorConfig>() {{
        put("logo", new SpineActorConfig(
                661/3, 1403/3, 1024/2 - 661/3/2, 650,
                "logo",
                "spine/logo/logo.atlas",
                "spine/logo/logo.json",
                "idle", true));
        put("character", new SpineActorConfig(
                68.16f, 177, 70, 370,
                "character",
                "spine/character/character.atlas",
                "spine/character/character.json",
                "idle", true));
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
                "idle", true));
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
    public final Texture dialogueBackgroundTexture = new Texture("dialogue_background.png");
    public final Vector2 dialogueOffset = new Vector2(210, 330);
    public final float dialogueScale = 0.8f;

    // Progress bar parameters.
    public final HashMap<String, ProgressConfig> progressActorConfigs = new HashMap<String, ProgressConfig>() {{
        put("timeTillNext", new ProgressConfig(
                100, 10, 800, 200, "timeTillNext",
                0, 100, 1, false,
                0));
        put("redProbability", new ProgressConfig(
                100, 10, 800, 150, "redProbability",
                0, 100, 1, false,
                0));
    }};

    // Menu parameters.
    public final float menuPosX = 798;
    public final float menuPosY = 520;
    public final float toolboxX = 32;
    public final float toolboxY = -470;
    public final float toolboxW = 170;
    public final float toolboxH = 300;
    public final float toolboxSpacing = 1;
    public final float dragActorPositionX = -20;
    public final float dragActorPositionY = 20;
    public final float dragAndDropTouchOffsetX = -60;
    public final float dragAndDropTouchOffsetY = 60;
    public String dragAndDrogSourceName = "";
    public final float tabTagScaleSmall = 0.7f;
    public final float tabTagScaleLarge = 0.8f;
    public final Texture menuBackgroundTexture = new Texture("tab_background.png");
    public final Texture menuTabTagWormTexture = new Texture("tab_worm.png");
    public final Texture menuTabTagStatTexture = new Texture("tab_stat.png");
    public final Texture menuTabTagUnknownTexture = new Texture("tab_unknown.png");

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
    public WormSegment.BasicImgSegment touchingSeg = null;

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
    public final Texture segIndicatorLeftTexture;
    public final Texture segIndicatorRightTexture;
    public final Vector2 segShadowYRange = new Vector2(62, 212);
    public final Vector2 segShadowYRangeRef = new Vector2(62, 576);  // Assumed body Y range.

    public final HashMap<Integer, WormSegConfig> wormSegConfigs = new HashMap<Integer, WormSegConfig>() {{
        put(0, new WormSegConfig("seg_balloon"));
        put(1, new WormSegConfig("seg_arm"));
        put(2, new WormSegConfig("seg_pendulum"));
        put(3, new WormSegConfig("seg_fan"));
        put(4, new WormSegConfig("seg_leg"));
        put(5, new WormSegConfig("seg_wing"));
    }};

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

    public class ProgressConfig {
        public final float w;
        public final float h;
        public final float x;
        public final float y;
        public final String name;
        public final float min;
        public final float max;
        public final float stepSize;
        public final boolean vertical;
        public final float startValue;

        public ProgressConfig(float w, float h, float x, float y, String name,
                              float min, float max, float stepSize, boolean vertical,
                              float startValue) {
            this.w = w;  // May remove.
            this.h = h;  // May remove.
            this.x = x;
            this.y = y;
            this.name = name;
            this.min = min;
            this.max = max;
            this.stepSize = stepSize;
            this.vertical = vertical;
            this.startValue = startValue;
        }
    }

    class WormSegConfig {
        final String name;
        final String imgPath;
        final Texture texture;

        public WormSegConfig(String name) {
            this.name = name;
            this.imgPath = "worm/" + name + ".png";
            this.texture = new Texture(this.imgPath);
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

        stage.getCamera().position.set(w/2, h/2, 0);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal(skinFile));
        skin.getAtlas().getTextures().iterator().next().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skin.getFont("default-font").getData().markupEnabled = true;
        skin.getFont("default-font").getData().setScale(scale);
        toolboxDragAndDrop = new DragAndDrop();
        // Drag and drop offsets for touch screen inputs.
        toolboxDragAndDrop.setDragActorPosition(dragActorPositionX, dragActorPositionY);
        toolboxDragAndDrop.setTouchOffset(dragAndDropTouchOffsetX, dragAndDropTouchOffsetY);

        // Initialize actors.
        background = new SpineActor(spineActorConfigs.get("background"), skeletonRenderer);
        character = new SpineActor(spineActorConfigs.get("character"), skeletonRenderer);
        wormseg = new SpineActor(spineActorConfigs.get("wormseg"), skeletonRenderer);
        wormhurt = new SpineActor(spineActorConfigs.get("wormhurt"), skeletonRenderer);
        wormlvl2 = new SpineActor(spineActorConfigs.get("wormlvl2"), skeletonRenderer);
        dialogueBox = new Dialogue(this);
        timeTillNext = new ProgressActor(progressActorConfigs.get("timeTillNext"), skin);
        redProbability = new ProgressActor(progressActorConfigs.get("redProbability"), skin);
        menu = new Menu(this);
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
        segTexture = new Texture(Gdx.files.internal("worm/seg_balloon.png"));  // FIXME: Debug.
        segTextureRegions = TextureRegion.split(segTexture, segTexture.getWidth(), segTexture.getHeight());
        shadowTexture = new Texture(Gdx.files.internal("worm/shadow.png"));  // FIXME: Debug.
        shadowTextureRegions = TextureRegion.split(shadowTexture, shadowTexture.getWidth(), shadowTexture.getHeight());
        segIndicatorLeftTexture = new Texture(Gdx.files.internal("worm/indicator_left.png"));
        segIndicatorRightTexture = new Texture(Gdx.files.internal("worm/indicator_right.png"));
        worm = new Worm(this);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        dialogueBackgroundTexture.dispose();
        menuBackgroundTexture.dispose();
        menuTabTagWormTexture.dispose();
        menuTabTagStatTexture.dispose();
        menuTabTagUnknownTexture.dispose();

        world.dispose();
        segShapeL.dispose();
        segShapeR.dispose();
        segShapeM.dispose();
        segTexture.dispose();
        shadowTexture.dispose();
        segIndicatorLeftTexture.dispose();
        segIndicatorRightTexture.dispose();

        for (int i = 0; i < wormSegConfigs.size(); i++) {
            wormSegConfigs.get(i).texture.dispose();
        }
    }
}

