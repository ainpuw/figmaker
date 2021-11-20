package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class GameConfig {
    ////////////////////////////////////////////////////
    // General Box2D parameters
    ////////////////////////////////////////////////////

    public final Vector2 gravity = new Vector2(0, -50f);
    public World world = new World(gravity, true);
    public final float friction = 0.5f;
    public final int velocityIterations = 1;
    public final int positionIterations = 1;
    // Used to freeze the world when needed.
    public boolean evolveWorld = true;
    // For quick access to worm segments.
    public Array<WormSegment> wormSegs = null;
    // Decide where to add the next segment.
    public WormSegment.BasicImgSegment touchingSeg = null;

    ////////////////////////////////////////////////////
    // Worm parameters
    ////////////////////////////////////////////////////

    // Coordinates for the worm pen.
    public final float penCenterX = 500;
    public final float penCenterY = 110;
    public final float penW = 650;
    public final float penH = 150;
    public final float penThickness = 20;

    public final float segMidW = 60;
    public final float segMidH = 20;
    public final float segEndW = 10;
    public final float segEndH = 10;
    public final float segDensity = 1;
    public final float jointLen = 10;
    public final boolean collideConnected = true;
    public final float joinPos = segMidW/2 + segEndW;
    public PolygonShape segShapeL;  // End left.
    public PolygonShape segShapeR;  // End right.
    public PolygonShape segShapeM;  // Middle piece.
    public FixtureDef segFixtureDefL = new FixtureDef();
    public FixtureDef segFixtureDefR = new FixtureDef();
    public FixtureDef segFixtureDefM = new FixtureDef();
    public Texture segIndicatorLeftTexture;
    public Texture segIndicatorRightTexture;

    public final HashMap<Integer, WormSegConfig> wormSegConfigs = new HashMap<Integer, WormSegConfig>() {{
        put(0, new WormSegConfig("seg_balloon"));
        put(1, new WormSegConfig("seg_arm"));
        put(2, new WormSegConfig("seg_pendulum"));
        put(3, new WormSegConfig("seg_fan"));
        put(4, new WormSegConfig("seg_leg"));
        put(5, new WormSegConfig("seg_wing"));
    }};

    ////////////////////////////////////////////////////
    // Subclass definitions
    ////////////////////////////////////////////////////

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

    public GameConfig() {
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
        segIndicatorLeftTexture = new Texture(Gdx.files.internal("worm/indicator_left.png"));
        segIndicatorRightTexture = new Texture(Gdx.files.internal("worm/indicator_right.png"));
    }

    public void dispose() {
        world.dispose();

        segShapeL.dispose();
        segShapeR.dispose();
        segShapeM.dispose();
        segIndicatorLeftTexture.dispose();
        segIndicatorRightTexture.dispose();

        for (int i = 0; i < wormSegConfigs.size(); i++) {
            wormSegConfigs.get(i).texture.dispose();
        }
    }
}
