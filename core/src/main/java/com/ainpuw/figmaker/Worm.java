package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Bone;

public class Worm {
    private Config config;
    private Array<Body> pen = new Array<>();
    public Array<WormSegment> segs = new Array<>();
    public Array<Body> anchors = new Array<>();
    public Array<Body> repulsivePairs = new Array<>();
    public Array<Joint> segJoints = new Array<>();
    public Array<Joint> anchorJoints = new Array<>();

    public Worm(Config config) {
        this.config = config;
        createPen();
    }

    public void createBox2dWorm(Bone bone, WormSegment parent) {
        for (Bone childBone : bone.getChildren()) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(childBone.getWorldX(), childBone.getWorldY());
            Body anchorBody = config.world.createBody(bodyDef);
            anchors.add(anchorBody);

            WormSegment newWormSegment = new WormSegment(config, childBone.getWorldX(), childBone.getWorldY(), childBone.getWorldRotationX());
            segs.add(newWormSegment);
            joinSegments(anchorBody, parent, newWormSegment);
            createBox2dWorm(childBone, newWormSegment);
        }
    }

    private void createPen() {
        // Set pen positions.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // The floor.
        bodyDef.position.set(config.w / 2, config.penCenterY - config.penH / 2);
        pen.add(config.world.createBody(bodyDef));
        // The left wall.
        bodyDef.position.set(config.penCenterX - config.penW / 2, config.penCenterY);
        pen.add(config.world.createBody(bodyDef));
        // The right wall.
        bodyDef.position.set(config.penCenterX + config.penW / 2, config.penCenterY);
        pen.add(config.world.createBody(bodyDef));
        // The ceiling.
        bodyDef.position.set(config.w / 2, config.penCenterY + config.penH / 2 + config.penThickness);
        pen.add(config.world.createBody(bodyDef));

        // Set pen wall shape.
        FixtureDef fixture = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        fixture.shape = shape;
        fixture.friction = config.friction;
        fixture.density = 0.0f;
        // The floor.
        shape.setAsBox(config.w * 0.6f, config.penThickness / 2, new Vector2(0, -config.penThickness / 2), 0);
        pen.get(0).createFixture(fixture);
        // The left wall.
        shape.setAsBox(config.penThickness / 2, config.penH / 2, new Vector2(-config.penThickness / 2, 0), 0);
        pen.get(1).createFixture(fixture);
        // The right wall.
        shape.setAsBox(config.penThickness / 2, config.penH / 2, new Vector2(config.penThickness / 2, 0), 0);
        pen.get(2).createFixture(fixture);
        // The ceiling.
        shape.setAsBox(config.w * 0.6f, config.penThickness / 2, new Vector2(0, -config.penThickness / 2), 0);
        pen.get(3).createFixture(fixture);
        shape.dispose();
    }


    public void joinSegments(Body anchorBody, WormSegment parent, WormSegment child) {
        // Create anchor joint.

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.collideConnected = child.config.collideConnected;
        distanceJointDef.frequencyHz = child.config.frequencyHz;
        distanceJointDef.dampingRatio = child.config.dampingRatio;
        distanceJointDef.bodyA = anchorBody;
        distanceJointDef.bodyB = child.body;
        distanceJointDef.length = 0;
        distanceJointDef.localAnchorA.set(0, 0);
        distanceJointDef.localAnchorB.set(0, 0);
        anchorJoints.add(child.config.world.createJoint(distanceJointDef));

        if (parent == null) return;

        // Create segment to segment joints.

        distanceJointDef.bodyA = parent.body;
        distanceJointDef.bodyB = child.body;
        // Center to center.
        Vector2 bodyACtr = distanceJointDef.bodyA.getPosition();
        Vector2 bodyBCtr = distanceJointDef.bodyB.getPosition();
        distanceJointDef.length = bodyACtr.dst(bodyBCtr);
        distanceJointDef.localAnchorA.set(0, 0);
        distanceJointDef.localAnchorB.set(0, 0);
        segJoints.add(parent.config.world.createJoint(distanceJointDef));
        // End to end.
        // Connect the shortest ends of both, then the furthest ends of both.
        Vector2 bodyAEndUR = new Vector2(parent.config.joinPos, parent.config.segMidH);
        Vector2 bodyAEndLL = new Vector2(-parent.config.joinPos, -parent.config.segMidH);
        Vector2 bodyBEndUR = new Vector2(parent.config.joinPos, parent.config.segMidH);
        Vector2 bodyBEndLL = new Vector2(-parent.config.joinPos, -parent.config.segMidH);
        bodyAEndUR.rotateRad(distanceJointDef.bodyA.getAngle());
        bodyAEndLL.rotateRad(distanceJointDef.bodyA.getAngle());
        bodyBEndUR.rotateRad(distanceJointDef.bodyB.getAngle());
        bodyBEndLL.rotateRad(distanceJointDef.bodyB.getAngle());
        bodyAEndUR.add(bodyACtr);
        bodyAEndLL.add(bodyACtr);
        bodyBEndUR.add(bodyBCtr);
        bodyBEndLL.add(bodyBCtr);
        float AURBUR = bodyAEndUR.dst(bodyBEndUR);
        float AURBLL = bodyAEndUR.dst(bodyBEndLL);
        float ALLBUR = bodyAEndLL.dst(bodyBEndUR);
        float ALLBLL = bodyAEndLL.dst(bodyBEndLL);
        if (AURBUR < Math.min(Math.min(AURBLL, ALLBUR), ALLBLL) ||
            ALLBLL < Math.min(Math.min(AURBLL, ALLBUR), AURBUR)) {
            distanceJointDef.length = AURBUR;
            distanceJointDef.localAnchorA.set(parent.config.joinPos, parent.config.segMidH);
            distanceJointDef.localAnchorB.set(parent.config.joinPos, parent.config.segMidH);
            segJoints.add(parent.config.world.createJoint(distanceJointDef));
            distanceJointDef.length = ALLBLL;
            distanceJointDef.localAnchorA.set(-parent.config.joinPos, -parent.config.segMidH);
            distanceJointDef.localAnchorB.set(-parent.config.joinPos, -parent.config.segMidH);
            segJoints.add(parent.config.world.createJoint(distanceJointDef));
        } else {
            distanceJointDef.length = AURBLL;
            distanceJointDef.localAnchorA.set(parent.config.joinPos, parent.config.segMidH);
            distanceJointDef.localAnchorB.set(-parent.config.joinPos, -parent.config.segMidH);
            segJoints.add(parent.config.world.createJoint(distanceJointDef));
            distanceJointDef.length = ALLBUR;
            distanceJointDef.localAnchorA.set(-parent.config.joinPos, -parent.config.segMidH);
            distanceJointDef.localAnchorB.set(parent.config.joinPos, parent.config.segMidH);
            segJoints.add(parent.config.world.createJoint(distanceJointDef));
        }

        // Update repulsive body array.

        if (bodyACtr.x < bodyBCtr.x) {
            repulsivePairs.add(parent.body);
            repulsivePairs.add(child.body);
        } else {
            repulsivePairs.add(child.body);
            repulsivePairs.add(parent.body);
        }
    }

    public void step() {
        for (Joint j : segJoints) {
            System.out.println(j.getAnchorA());
            
            break;
        }
        for (WormSegment seg : segs) {
            seg.step();
        }

        for (int i = 0; i < repulsivePairs.size / 2; i++) {
            // Odd indices are pairs of even indices.
            Body body1 = repulsivePairs.get(2 * i);
            Body body2 = repulsivePairs.get(2 * i + 1);
            // v2 - v1.
            Vector2 body1Pos = new Vector2(body1.getPosition());
            body1Pos.x = -body1Pos.x;
            body1Pos.y = -body1Pos.y;
            Vector2 one2two = new Vector2(body2.getPosition());
            one2two.add(body1Pos);
            // Apply repulsion.
            float dist = one2two.len();
            if (dist > config.adjacentRepulsiveForceCutoff) continue;
            one2two.nor();
            one2two.x *= config.adjacentRepulsiveForceFactor / dist / dist;  // 1/r^2 force.
            one2two.y *= config.adjacentRepulsiveForceFactor / dist / dist;
            Vector2 two2one = new Vector2(-one2two.x, -one2two.y);
            body1.applyLinearImpulse(two2one, body1.getPosition(), true);
            body2.applyLinearImpulse(one2two, body2.getPosition(), true);
        }
    }

    public static void drawWorm(float delta, Config config) {
        if (config.wormOne != null)
            drawWormOne(delta, config);
        else if (config.wormSkeleton != null)
            drawWormSkeleton(delta, config);
        else
            drawWormBox2d(delta, config);
    }

    public static void drawWormOne(float delta, Config config) {
        config.wormOne.skeleton.getRootBone().setX(config.wormOne.getX());
        config.wormOne.skeleton.getRootBone().setY(config.wormOne.getY());

        // Draw shadow.
        config.spriteBatch.begin();
        Bone bone = config.wormOne.skeleton.getRootBone().getChildren().get(0);
        // Basic segment angle.
        float angle = bone.getRotation() - 90;
        if (angle > 180) angle -= 180;
        // Center and actual width.
        Vector2 ctrPos = new Vector2(config.wormOne.getX() + bone.getX(),
                                     config.wormOne.getY() + bone.getY());
        Vector2 segW = new Vector2(config.segTexture.getWidth(), 0);
        segW.rotateDeg(angle);
        // Calculate shadow relative Y percent position.
        float shadowYPercent = (ctrPos.y - config.segShadowYRangeRef.x) / (config.segShadowYRangeRef.y - config.segShadowYRangeRef.x);
        shadowYPercent = Math.min(shadowYPercent, 1);
        shadowYPercent = Math.max(shadowYPercent, 0);
        // Calculate shadow size.
        float shadowW = Math.abs(segW.x) * (1 - shadowYPercent);
        float shadowH = config.shadowTextureRegions[0][0].getRegionHeight() * (1 - shadowYPercent);
        shadowW = Math.max(20, shadowW);
        shadowH = Math.max(6.67f, shadowH);
        // Calculate shadow center position.
        float shadowX = ctrPos.x - shadowW / 2;
        float shadowY = shadowYPercent * (config.segShadowYRange.y - config.segShadowYRange.x) + config.segShadowYRange.x - shadowH / 2;

        config.spriteBatch.draw(config.shadowTextureRegions[0][0],
                                shadowX, shadowY,
                                0, 0,
                                shadowW, shadowH,
                                1, 1,
                                0);
        config.spriteBatch.end();

        // Draw the worm Spine animation.
        config.spriteBatch.begin();
        config.wormOne.skeleton.updateWorldTransform();
        config.wormOne.animationState.update(delta);
        config.wormOne.animationState.apply(config.wormOne.skeleton);
        config.skeletonRenderer.draw(config.spriteBatch, config.wormOne.skeleton);
        config.spriteBatch.end();
    }

    public static void drawWormSkeletonShadow(Bone bone, Config config) {
        // Basic segment angle.
        float angle = bone.getWorldRotationX();
        if (angle > 180) angle -= 180;
        // Center and actual width.
        Vector2 ctrPos = new Vector2(bone.getWorldX(), bone.getWorldY());
        Vector2 segW = new Vector2(config.segTexture.getWidth(), 0);
        segW.rotateDeg(angle);
        // Calculate shadow relative Y percent position.
        float shadowYPercent = (ctrPos.y - config.segShadowYRangeRef.x) / (config.segShadowYRangeRef.y - config.segShadowYRangeRef.x);
        shadowYPercent = Math.min(shadowYPercent, 1);
        shadowYPercent = Math.max(shadowYPercent, 0);
        // Calculate shadow size.
        float shadowW = Math.abs(segW.x) * (1 - shadowYPercent * 0.5f);
        float shadowH = config.shadowTextureRegions[0][0].getRegionHeight() * (1 - shadowYPercent * 0.5f);
        shadowW = Math.max(20, shadowW);
        shadowH = Math.max(6.67f, shadowH);
        // Calculate shadow center position.
        float shadowX = ctrPos.x - shadowW / 2;
        float shadowY = shadowYPercent * (config.segShadowYRange.y - config.segShadowYRange.x) + config.segShadowYRange.x - shadowH / 2;

        config.spriteBatch.setColor(0, 0, 0, 1 - shadowYPercent);
        config.spriteBatch.draw(config.shadowTextureRegions[0][0],
                shadowX, shadowY,
                0, 0,
                shadowW, shadowH,
                1, 1,
                0);

        for (Bone childBone : bone.getChildren()) {
            drawWormSkeletonShadow(childBone, config);
        }
    }

    public static void drawWormSkeletonJoint(Bone bone, Config config) {
        for (Bone childBone : bone.getChildren()) {
            // Center to center.
            Vector2 body1Ctr = new Vector2(bone.getWorldX(), bone.getWorldY());
            Vector2 body2Ctr = new Vector2(childBone.getWorldX(), childBone.getWorldY());
            config.shapeRenderer.rectLine(body1Ctr.x, body1Ctr.y, body2Ctr.x, body2Ctr.y, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

            // End to end.
            float angle1 = bone.getWorldRotationX();
            float angle2 = childBone.getWorldRotationX();
            Vector2 disp1 = new Vector2(config.segTexture.getWidth() / 2.2f, 0);
            Vector2 disp2 = new Vector2(-config.segTexture.getWidth() / 2.2f, 0);
            if (body2Ctr.x < body1Ctr.x) {
                disp1 = new Vector2(-config.segTexture.getWidth() / 2.2f, 0);
                disp2 = new Vector2(config.segTexture.getWidth() / 2.2f, 0);
            }
            disp1.rotateDeg(angle1);
            disp2.rotateDeg(angle2);
            Vector2 ctrDisp1 = body1Ctr.add(disp1);
            Vector2 ctrDisp2 = body2Ctr.add(disp2);
            config.shapeRenderer.rectLine(ctrDisp1.x, ctrDisp1.y, ctrDisp2.x, ctrDisp2.y, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

            drawWormSkeletonJoint(childBone, config);
        }
    }

    public static void drawWormSkeleton(float delta, Config config) {
        config.wormSkeleton.skeleton.getRootBone().setX(config.wormSkeleton.getX());
        config.wormSkeleton.skeleton.getRootBone().setY(config.wormSkeleton.getY());

        // Draw shadow.
        config.spriteBatch.begin();
        for (Bone bone : config.wormSkeleton.skeleton.getRootBone().getChildren()) {
            drawWormSkeletonShadow(bone, config);
        }
        config.spriteBatch.end();

        // Draw joints.
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Bone bone : config.wormSkeleton.skeleton.getRootBone().getChildren()) {
            drawWormSkeletonJoint(bone, config);
        }
        config.shapeRenderer.end();

        // Draw the worm Spine animation.
        config.spriteBatch.begin();
        config.wormSkeleton.skeleton.updateWorldTransform();
        config.wormSkeleton.animationState.update(delta);
        config.wormSkeleton.animationState.apply(config.wormSkeleton.skeleton);
        config.skeletonRenderer.draw(config.spriteBatch, config.wormSkeleton.skeleton);
        config.spriteBatch.end();
    }

    public static void drawWormBox2d(float delta, Config config) {
        // Draw shadow.
        config.spriteBatch.begin();
        for (WormSegment seg : config.worm.segs) {
            // Segment angle.
            float angle = seg.body.getAngle() * 57.2958f;
            if (angle > 180) angle -= 180;
            // Center and actual width.
            Vector2 ctrPos = new Vector2(seg.body.getPosition().x, seg.body.getPosition().y);
            Vector2 segW = new Vector2(config.segTexture.getWidth(), 0);
            segW.rotateDeg(angle);
            // Calculate shadow relative Y percent position.
            float shadowYPercent = (ctrPos.y - config.segShadowYRangeRef.x) / (config.segShadowYRangeRef.y - config.segShadowYRangeRef.x);
            shadowYPercent = Math.min(shadowYPercent, 1);
            shadowYPercent = Math.max(shadowYPercent, 0);
            // Calculate shadow size.
            float shadowW = Math.abs(segW.x) * (1 - shadowYPercent);
            float shadowH = config.shadowTextureRegions[0][0].getRegionHeight() * (1 - shadowYPercent);
            shadowW = Math.max(20, shadowW);
            shadowH = Math.max(6.67f, shadowH);
            // Calculate shadow center position.
            float shadowX = ctrPos.x - shadowW / 2;
            float shadowY = shadowYPercent * (config.segShadowYRange.y - config.segShadowYRange.x) + config.segShadowYRange.x - shadowH / 2;

            config.spriteBatch.draw(config.shadowTextureRegions[0][0],
                    shadowX, shadowY,
                    0, 0,
                    shadowW, shadowH,
                    1, 1,
                    0);

        }
        config.spriteBatch.end();

        // Draw joints.
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < config.worm.repulsivePairs.size / 2; i++) {
            // Odd indices are pairs of even indices.
            Body body1 = config.worm.repulsivePairs.get(2 * i);
            Body body2 = config.worm.repulsivePairs.get(2 * i + 1);
            Vector2 body1Ctr = body1.getPosition();
            Vector2 body2Ctr = body2.getPosition();
            config.shapeRenderer.rectLine(body1Ctr.x, body1Ctr.y, body2Ctr.x, body2Ctr.y, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

            float angle1 = body1.getAngle() * 57.2958f;
            float angle2 = body2.getAngle() * 57.2958f;
            Vector2 disp1 = new Vector2(config.segTexture.getWidth() / 2.2f, 0);
            Vector2 disp2 = new Vector2(-config.segTexture.getWidth() / 2.2f, 0);
            disp1.rotateDeg(angle1);
            disp2.rotateDeg(angle2);
            Vector2 ctrDisp1 = body1Ctr.add(disp1);
            Vector2 ctrDisp2 = body2Ctr.add(disp2);
            config.shapeRenderer.rectLine(ctrDisp1.x, ctrDisp1.y, ctrDisp2.x, ctrDisp2.y, 4, Color.LIGHT_GRAY, Color.LIGHT_GRAY);

        }
        config.shapeRenderer.end();

        // Draw segments.
        config.spriteBatch.begin();
        for (WormSegment seg : config.worm.segs) {
            float angle = seg.body.getAngle() * 57.2958f;
            Vector2 ctrPos = new Vector2(seg.body.getPosition().x, seg.body.getPosition().y);
            seg.skeleton.getRootBone().setX(ctrPos.x);
            seg.skeleton.getRootBone().setY(ctrPos.y);
            seg.skeleton.getRootBone().setRotation(angle);
            seg.skeleton.updateWorldTransform();
            seg.animationState.update(delta);
            seg.animationState.apply(seg.skeleton);
            config.skeletonRenderer.draw(config.spriteBatch, seg.skeleton);
        }
        config.spriteBatch.end();
    }
}

