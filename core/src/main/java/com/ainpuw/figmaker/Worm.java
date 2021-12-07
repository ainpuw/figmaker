package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Worm {
    private GameConfig gameConfig;
    private UIConfig uiConfig;
    private Array<Body> pen = new Array<>();
    public Array<WormSegment> segs = new Array<>();
    public Array<Body> repulsivePairs = new Array<>();

    public Worm(GameConfig gameConfig, UIConfig uiConfig) {
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;

        createPen();
        // FIXME: For debug.
        makeDebugWorm();
    }

    private void createPen() {
        // Set pen positions.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // The floor.
        bodyDef.position.set(uiConfig.w / 2, gameConfig.penCenterY - gameConfig.penH / 2);
        pen.add(gameConfig.world.createBody(bodyDef));
        // The left wall.
        bodyDef.position.set(gameConfig.penCenterX - gameConfig.penW / 2, gameConfig.penCenterY);
        pen.add(gameConfig.world.createBody(bodyDef));
        // The right wall.
        bodyDef.position.set(gameConfig.penCenterX + gameConfig.penW / 2, gameConfig.penCenterY);
        pen.add(gameConfig.world.createBody(bodyDef));

        // Set pen wall shape.
        FixtureDef fixture = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        fixture.shape = shape;
        fixture.friction = gameConfig.friction;
        fixture.density = 0.0f;
        // The floor.
        shape.setAsBox(uiConfig.w, gameConfig.penThickness/2, new Vector2(0, -gameConfig.penThickness/2), 0);
        pen.get(0).createFixture(fixture);
        // The left wall.
        shape.setAsBox(gameConfig.penThickness/2, gameConfig.penH/2, new Vector2(-gameConfig.penThickness/2, 0), 0);
        pen.get(1).createFixture(fixture);
        // The right wall.
        shape.setAsBox(gameConfig.penThickness/2, gameConfig.penH/2, new Vector2(gameConfig.penThickness/2, 0), 0);
        pen.get(2).createFixture(fixture);
        shape.dispose();
    }

    private void makeDebugWorm() {
       segs.add(new WormSegment(gameConfig, uiConfig, "seg_balloon", 500, 100));
    }

    public void step() {
        // TODO: Need to add force on the wall to repel in one direction.

        for (WormSegment seg : segs) {
            seg.step();
        }

        for (int i = 0; i < repulsivePairs.size/2; i++) {
            // Odd indices are pairs of even indices.
            Body body1 = repulsivePairs.get(2*i);
            Body body2 = repulsivePairs.get(2*i+1);
            // v2 - v1.
            Vector2 body1Pos = new Vector2(body1.getPosition());
            body1Pos.x = -body1Pos.x;
            body1Pos.y = -body1Pos.y;
            Vector2 one2two = new Vector2(body2.getPosition());
            one2two.add(body1Pos);
            // Apply repulsion.
            float dist = one2two.len();
            if (dist > gameConfig.adjacentRepulsiveForceCutoff) continue;
            one2two.nor();
            one2two.x *= gameConfig.adjacentRepulsiveForceFactor / dist / dist;  // 1/r^2 force.
            one2two.y *= gameConfig.adjacentRepulsiveForceFactor / dist / dist;
            Vector2 two2one = new Vector2(-one2two.x, -one2two.y);
            body1.applyLinearImpulse(two2one, body1.getPosition(), true);
            body2.applyLinearImpulse(one2two, body2.getPosition(), true);
        }
    }

    public static void joinSegments(WormSegment newSeg, WormSegment.BasicSegment joinSeg,
                                    boolean isLeft, Array<Body> repulsivePairs) {
        // FIXME: We now always use the first element in the basicSegs list.
        Body bodyNew = newSeg.basicSegs.get(0).body;
        Body bodyOld = joinSeg.body;

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.collideConnected = newSeg.gameConfig.collideConnected;
        distanceJointDef.length = newSeg.gameConfig.jointLen;
        distanceJointDef.bodyA = bodyNew;
        distanceJointDef.bodyB = bodyOld;
        distanceJointDef.frequencyHz = 1f;  // Spring strength - higher the stronger.
        distanceJointDef.dampingRatio = 1;  // How bouncy - 1 is stiff.
        // Add top joint.
        if (isLeft) {
            // Join right side of newSeg to the left side of joinSeg.
            distanceJointDef.localAnchorA.set(newSeg.gameConfig.joinPos, newSeg.gameConfig.segMidH/2);
            distanceJointDef.localAnchorB.set(-newSeg.gameConfig.joinPos, newSeg.gameConfig.segMidH/2);
        } else {
            // Join left side of newSeg to the right side of joinSeg.
            distanceJointDef.localAnchorA.set(-newSeg.gameConfig.joinPos, newSeg.gameConfig.segMidH/2);
            distanceJointDef.localAnchorB.set(newSeg.gameConfig.joinPos, newSeg.gameConfig.segMidH/2);
        }
        newSeg.gameConfig.world.createJoint(distanceJointDef);
        // Add bottom joint.
        if (isLeft) {
            // Join right side of newSeg to the left side of joinSeg.
            distanceJointDef.localAnchorA.set(newSeg.gameConfig.joinPos, -newSeg.gameConfig.segMidH/2);
            distanceJointDef.localAnchorB.set(-newSeg.gameConfig.joinPos, -newSeg.gameConfig.segMidH/2);
        } else {
            // Join left side of newSeg to the right side of joinSeg.
            distanceJointDef.localAnchorA.set(-newSeg.gameConfig.joinPos, -newSeg.gameConfig.segMidH/2);
            distanceJointDef.localAnchorB.set(newSeg.gameConfig.joinPos, -newSeg.gameConfig.segMidH/2);
        }
        newSeg.gameConfig.world.createJoint(distanceJointDef);

        // Adjacent segments are repulsive to each other. This gives the worm a stiffer feel.
        if (isLeft) {
            repulsivePairs.add(bodyNew);
            repulsivePairs.add(bodyOld);
        } else {
            repulsivePairs.add(bodyOld);
            repulsivePairs.add(bodyNew);
        }
    }

    public static void drawWorm(float delta, GameConfig config, SpriteBatch spriteBatch,
                                ShapeRenderer shapeRenderer, SkeletonRenderer skeletonRenderer) {
        // Draw shadow.
        spriteBatch.begin();
        for (WormSegment seg : config.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                // Basic segment angle.
                float angle = basicSeg.body.getAngle() * 57.2958f;
                if (angle > 180) angle -= 180;
                // Center and actual width.
                Vector2 ctrPos = new Vector2(basicSeg.body.getPosition().x, basicSeg.body.getPosition().y);
                Vector2 segW = new Vector2(config.segTexture.getWidth(), 0);
                segW.rotateDeg(angle);
                // Calculate shadow relative Y percent position.
                float shadowYPercent = (ctrPos.y - config.segShadowYRangeRef.x)/(config.segShadowYRangeRef.y - config.segShadowYRangeRef.x);
                shadowYPercent = Math.min(shadowYPercent, 1);
                shadowYPercent = Math.max(shadowYPercent, 0);
                // Calculate shadow size.
                float shadowW = Math.abs(segW.x) * (1 - shadowYPercent);
                float shadowH = config.shadowTextureRegions[0][0].getRegionHeight() * (1 - shadowYPercent);
                shadowW = Math.max(20, shadowW);
                shadowH = Math.max(6.67f, shadowH);
                // Calculate shadow center position.
                float shadowX = ctrPos.x - shadowW / 2;
                float shadowY = shadowYPercent * (config.segShadowYRange.y - config.segShadowYRange.x) + config.segShadowYRange.x - shadowH/2;

                spriteBatch.draw(config.shadowTextureRegions[0][0],
                        shadowX, shadowY,
                        0, 0,
                        shadowW, shadowH,
                        1, 1,
                        0);
            }
        }
        spriteBatch.end();

        // Draw joints.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < config.worm.repulsivePairs.size/2; i++) {
            // Odd indices are pairs of even indices.
            Body body1 = config.worm.repulsivePairs.get(2 * i);
            Body body2 = config.worm.repulsivePairs.get(2 * i + 1);
            Vector2 body1Ctr = body1.getPosition();
            Vector2 body2Ctr = body2.getPosition();
            shapeRenderer.rectLine(body1Ctr.x, body1Ctr.y, body2Ctr.x, body2Ctr.y, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

            float angle1 = body1.getAngle() * 57.2958f;
            float angle2 = body2.getAngle() * 57.2958f;
            Vector2 disp1 = new Vector2(config.segTexture.getWidth()/2.2f, 0);
            Vector2 disp2 = new Vector2(-config.segTexture.getWidth()/2.2f, 0);
            disp1.rotateDeg(angle1);
            disp2.rotateDeg(angle2);
            Vector2 ctrDisp1 = body1Ctr.add(disp1);
            Vector2 ctrDisp2 = body2Ctr.add(disp2);
            shapeRenderer.rectLine(ctrDisp1.x, ctrDisp1.y, ctrDisp2.x, ctrDisp2.y, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

        }
        shapeRenderer.end();

        // Draw segments.
        spriteBatch.begin();
        for (WormSegment seg : config.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                float angle = basicSeg.body.getAngle() * 57.2958f;
                Vector2 ctrPos = new Vector2(basicSeg.body.getPosition().x, basicSeg.body.getPosition().y);
                basicSeg.skeleton.getRootBone().setX(ctrPos.x);
                basicSeg.skeleton.getRootBone().setY(ctrPos.y);
                basicSeg.skeleton.getRootBone().setRotation(angle);
                basicSeg.skeleton.updateWorldTransform();
                basicSeg.animationState.update(delta);
                basicSeg.animationState.apply(basicSeg.skeleton);
                skeletonRenderer.draw(spriteBatch, basicSeg.skeleton);
            }
        }
        spriteBatch.end();

    }
}

