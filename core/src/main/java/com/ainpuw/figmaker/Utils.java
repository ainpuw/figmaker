package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

import java.util.Comparator;

public class Utils {
    public static void drawGameBoundingBox(Config config, SpriteBatch spriteBatch,
                                           ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(0, 0, config.w, config.h);
        shapeRenderer.line(0, config.h, config.w, 0);
        shapeRenderer.line(0, 0, config.w, 0);
        shapeRenderer.line(0, 0, 0, config.h);
        shapeRenderer.line(0, config.h, config.w, config.h);
        shapeRenderer.line(config.w, 0, config.w, config.h);
        shapeRenderer.end();
    }

    public static class StrStrFloatTriple {
        public final String s1;
        public final String s2;
        public final float f;

        public StrStrFloatTriple(String s1, String s2, float f) {
            this.s1 = s1;
            this.s2 = s2;
            this.f = f;
        }
    }

    public static DistanceJointDef createDistanceJointDef(Config config, Body bodyA, Body bodyB) {
        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.collideConnected = config.collideConnected;
        jointDef.frequencyHz = config.frequencyHz;
        jointDef.dampingRatio = config.dampingRatio;
        jointDef.bodyA = bodyA;
        jointDef.bodyB = bodyB;

        return jointDef;
    }

    public static Comparator stableBoneComparator = new Comparator<WormSegment>() {
        public int compare(WormSegment s1, WormSegment s2) {
            float diff = s1.stabilizationCountdown - s1.stabilizationCountdown;
            if (diff < 0) return -1;
            else if (diff > 0) return 1;
            else return 0;
        }
    };

    public static void drawBone(ShapeRenderer shapeRenderer, boolean broken, boolean stable,
                                float dashLen, float x1, float y1, float x2, float y2) {
        // Draw dashed line.
        if (broken) {
            /* Disable drawing broken bones.
            Vector2 p3 = new Vector2(x2 - x1, y2 - y1);
            float iMaxF = p3.len() / dashLen;
            int iMax = (int) Math.ceil(iMaxF);
            for (int i = 0; i < iMax; i++) {
                if (i % 2 == 0) {
                    float x1d = x1 + i * p3.x / iMaxF;
                    float y1d = y1 + i * p3.y / iMaxF;
                    float x2d = x1 + (i + 1) * p3.x / iMaxF;
                    float y2d = y1 + (i + 1) * p3.y / iMaxF;
                    if (i == iMax - 1) {
                        x2d = x2;
                        y2d = y2;
                    }

                    if (stable)
                        shapeRenderer.rectLine(x1d, y1d, x2d, y2d, 1f, Color.ORANGE, Color.ORANGE);
                    else
                        shapeRenderer.rectLine(x1d, y1d, x2d, y2d, 1f, Color.RED, Color.RED);
                }
            }
            */
        }
        // Draw a single line.
        else {
            shapeRenderer.rectLine(x1, y1, x2, y2, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        }
    }

    public static void drawInstabilities(Config config, float deltaTime) {
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (WormSegment seg : config.worm.segs) {
            if (seg.isStable()) {
                seg.instabilityAnimationCountdown = config.segInstabilityAnimationTime;
            }
            else if (!seg.isStable() && seg.noOfStabilizations >= config.segMaxStabilizationChances) {
                // The segment is dead, do nothing.
                continue;
            }
            else {
                float rColor = Math.max(0f, config.segMaxStabilizationChances - seg.noOfStabilizations) / config.segMaxStabilizationChances;
                // Use black color earlier as a warning.
                if (seg.noOfStabilizations >= config.segMaxStabilizationChances - 1)
                    rColor = 0;
                config.shapeRenderer.setColor(rColor, 0, 0, seg.instabilityAnimationCountdown / config.segInstabilityAnimationTime);
                Vector2 pos = seg.body.getPosition();
                float r = 30 * (config.segInstabilityAnimationTime - seg.instabilityAnimationCountdown) / config.segInstabilityAnimationTime;
                config.shapeRenderer.circle(pos.x, pos.y, r);
                config.shapeRenderer.circle(pos.x, pos.y, r+0.5f);  // Fake line width.
                config.shapeRenderer.circle(pos.x, pos.y, r+1);
                config.shapeRenderer.circle(pos.x, pos.y, r+1.5f);
                config.shapeRenderer.circle(pos.x, pos.y, r+2);
                seg.instabilityAnimationCountdown -= deltaTime;
                if (seg.instabilityAnimationCountdown < 0)
                    seg.instabilityAnimationCountdown = config.segInstabilityAnimationTime;
            }
        }
        config.shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
    }

    public static void drawTouch(Config config, float deltaTime) {
        // Add new touch points.
        if (Gdx.input.justTouched() && config.touchPos.size < config.maxNoOfTouch) {
            Vector2 touchPos = config.stageBack.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            config.touchPos.add(touchPos);
            config.touchCountDown.add(config.touchCountDownInit);
        }

        // Draw existing touch points
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = config.touchPos.size - 1; i >= 0; i--) {
            if (config.touchCountDown.get(i) <= 0) {
                config.touchPos.removeIndex(i);
                config.touchCountDown.removeIndex(i);
            }
            else {
                Vector2 v = config.touchPos.get(i);
                float timeLeft = config.touchCountDown.get(i);
                config.shapeRenderer.setColor(0, 0, 0, timeLeft/config.touchCountDownInit);
                config.shapeRenderer.circle(v.x, v.y, config.touchRadius*(config.touchCountDownInit-timeLeft)/config.touchCountDownInit);
                config.touchCountDown.set(i, timeLeft - deltaTime);
            }
        }
        config.shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
    }
}
