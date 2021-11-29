package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import java.util.HashMap;

public class UIConfig {
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

    ////////////////////////////////////////////////////
    // Scene2D parameters
    ////////////////////////////////////////////////////

    public final String skinFile = "skin/uiskin.json";

    // Spine animation parameters.
    public final HashMap<String, SpineActorConfig> spineActorConfigs = new HashMap<String, SpineActorConfig>() {{
        put("character", new SpineActorConfig(
            300, 576, 100, 0,
            "character",
            "spine/spineboy/export/spineboy.atlas",
            "spine/spineboy/export/spineboy-ess.json",
            "walk"));
        put("portrait", new SpineActorConfig(
                210, 304, 630, 560,  // w, h, x and y are useless here.
                "portrait",
                "spine/portrait/character_portrait.atlas",
                "spine/portrait/character_portrait.json",
                "idle"));
        put("background", new SpineActorConfig(
                1920, 1080, -480, 900,
                "background",
                "spine/background/background.atlas",
                "spine/background/background.json",
                "idle"));
    }};

    // Dialogue box parameters.
    public final HashMap<String, DialogueConfig> dialogueActorConfigs = new HashMap<String, DialogueConfig>() {{
        put("dialogueLabel", new DialogueConfig(
                390, -1, 50, 163,  // x and y are useless here.
                "dialogue",
                0));
    }};
    public Dialogue dialogueBox = null;  // Provide quick access to dialogue box.
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


    // Toolbox parameters.
    public final float toolboxX = 830;
    public final float toolboxY = 50;
    public final float toolboxW = 170;
    public final float toolboxH = 300;
    public final float toolboxSpacing = 1;
    public final float dragActorPositionX = -20;
    public final float dragActorPositionY = 20;
    public final float dragAndDropTouchOffsetX = -60;
    public final float dragAndDropTouchOffsetY = 60;
    public String dragAndDrogSourceName = "";

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

        public SpineActorConfig(float w, float h, float x, float y, String name, String atlas,
                                String skeletonJson, String defaultAnimation) {
            this.w = w;
            this.h = h;
            this.x = x;  // What this means depends on the Spine file.
            this.y = y;  // Same as the above.
            this.name = name;
            this.atlas = atlas;
            this.skeletonJson = skeletonJson;
            this.defaultAnimation = defaultAnimation;
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

    ////////////////////////////////////////////////////
    // Class functions
    ////////////////////////////////////////////////////

    public UIConfig() {
        stage.getCamera().position.set(w/2, h/2, 0);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal(skinFile));
        skin.getAtlas().getTextures().iterator().next().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skin.getFont("default-font").getData().markupEnabled = true;
        skin.getFont("default-font").getData().setScale(scale);
        toolboxDragAndDrop = new DragAndDrop();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        dialogueBackgroundTexture.dispose();
    }
}

