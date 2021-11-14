package com.ainpuw.figmaker;

import java.util.HashMap;

public class UIConfig {
    // Default screen size, but allows ExtendViewport scaling.
    // 16:9 aspect ratio, a balance between laptop and phone screens.
    public final float w = 1024;
    public final float h = 576;
    public final float scale = 1;
    public final float screenR = 0.2f;
    public final float screenG = 0.2f;
    public final float screenB = 0.2f;
    public final float screenA = 1f;

    ////////////////////////////////////////////////////
    // Scene2D parameters
    ////////////////////////////////////////////////////

    public final String skinFile = "skin/uiskin.json";

    // Spine animation parameters.
    public final HashMap<String, SpineActorConfig> spineActors = new HashMap<String, SpineActorConfig>() {{
        put("character", new SpineActorConfig(
            300, 576, 100, 0,
            "character",
            "spine/spineboy/export/spineboy.atlas",
            "spine/spineboy/export/spineboy-ess.json",
            "walk"));
        put("background", new SpineActorConfig(
                1024, 1024, 512, 0,
            "background",
            "spine/windmill/export/windmill.atlas",
            "spine/windmill/export/windmill-ess.json",
            "animation"));
    }};

    // Dialogue box parameters.
    public final HashMap<String, DialogueConfig> dialogueActors = new HashMap<String, DialogueConfig>() {{
        put("dialogue", new DialogueConfig(
                700, -1, 200, 500,
                "dialogue",
                0));
    }};

    // Progress bar parameters.
    public final HashMap<String, ProgressConfig> progressActors = new HashMap<String, ProgressConfig>() {{
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
    public final float toolboxX = 910;
    public final float toolboxY = 50;
    public final float toolboxW = 90;
    public final float toolboxH = 300;
    public final float toolboxSpacing = 10;

    ////////////////////////////////////////////////////
    // Box2D parameters
    ////////////////////////////////////////////////////

    public final float penCenterX = 570;
    public final float penCenterY = 110;
    public final float penW = 650;
    public final float penH = 150;
    public final float penThickness = 20;

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
}

