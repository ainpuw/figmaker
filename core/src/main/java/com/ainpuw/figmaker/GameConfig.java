package com.ainpuw.figmaker;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class GameConfig {
    public final float minFrameRate = 30f;

    ////////////////////////////////////////////////////
    // Box2D parameters
    ////////////////////////////////////////////////////

    public final Vector2 gravity = new Vector2(0, -50f);
    public final float friction = 0.5f;

    ////////////////////////////////////////////////////
    // Worm parameters
    ////////////////////////////////////////////////////

    public final float segMidW = 35;
    public final float segMidH = 10;
    public final float segEndW = 5;
    public final float segEndH = 5;
    public final float segDensity = 5;
    public final float jointLen = 10;
    public final float segEndHitboxScale = 1f;

    ////////////////////////////////////////////////////
    // Toolbox parameters
    ////////////////////////////////////////////////////

    public final HashMap<Integer, WormSegConfig> wormSegs = new HashMap<Integer, WormSegConfig>() {{
        put(0, new WormSegConfig("seg_up"));
        put(1, new WormSegConfig("seg_leftright"));
        put(2, new WormSegConfig("seg_leg"));
        put(3, new WormSegConfig("seg_wing"));
    }};

    ////////////////////////////////////////////////////
    // Subclass definitions
    ////////////////////////////////////////////////////

    class WormSegConfig {
        final String name;
        final String imgPath;

        public WormSegConfig(String name) {
            this.name = name;
            this.imgPath = "worm/" + name + ".png";
        }
    }
}
