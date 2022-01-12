package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
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
    public Array<WormSegment> repulsivePairs = new Array<>();

    public Worm(Config config) {
        this.config = config;
    }

    private void createPen() {
        // Set pen positions.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // The floor.
        bodyDef.position.set(config.w / 2, config.penCenterY - config.penH / 2);
        pen.add(config.world.createBody(bodyDef));
        // The left wall.
        //bodyDef.position.set(config.penCenterX - config.penW / 2, config.penCenterY);
        //pen.add(config.world.createBody(bodyDef));
        // The right wall.
        //bodyDef.position.set(config.penCenterX + config.penW / 2, config.penCenterY);
        //pen.add(config.world.createBody(bodyDef));
        // The ceiling.
        //bodyDef.position.set(config.w / 2, config.penCenterY + config.penH / 2 + config.penThickness);
        //pen.add(config.world.createBody(bodyDef));

        // Set pen wall shape.
        FixtureDef fixture = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        fixture.shape = shape;
        fixture.friction = config.friction;
        fixture.density = 0.0f;
        fixture.filter.categoryBits = config.collisionWall;
        // The floor.
        shape.setAsBox(config.w * 0.6f, config.penThickness / 2, new Vector2(0, -config.penThickness / 2), 0);
        pen.get(0).createFixture(fixture);
        // The left wall.
        //shape.setAsBox(config.penThickness / 2, config.penH / 2, new Vector2(-config.penThickness / 2, 0), 0);
        //pen.get(1).createFixture(fixture);
        // The right wall.
        //shape.setAsBox(config.penThickness / 2, config.penH / 2, new Vector2(config.penThickness / 2, 0), 0);
        //pen.get(2).createFixture(fixture);
        // The ceiling.
        //shape.setAsBox(config.w * 0.6f, config.penThickness / 2, new Vector2(0, -config.penThickness / 2), 0);
        //pen.get(3).createFixture(fixture);
        shape.dispose();
    }

    public void createBox2dWormSegNJoint(Bone bone, WormSegment parent) {
        for (Bone childBone : bone.getChildren()) {
            // Create a new worm segment.
            WormSegment newWormSegment = new WormSegment(config, childBone.getWorldX(), childBone.getWorldY(), childBone.getWorldRotationX());
            newWormSegment.parent = parent;
            segs.add(newWormSegment);

            // Create anchor body for center anchor joint.
            BodyDef bodyCDef = new BodyDef();
            bodyCDef.type = BodyDef.BodyType.StaticBody;
            bodyCDef.position.set(childBone.getWorldX(), childBone.getWorldY());
            Body anchorCBody = config.world.createBody(bodyCDef);
            newWormSegment.anchorC = anchorCBody;

            // Create anchor body for end anchor joint.
            BodyDef bodyEDef = new BodyDef();
            bodyEDef.type = BodyDef.BodyType.StaticBody;
            Vector2 r = new Vector2(config.segMidW / 2 + config.segEndW, 0);
            r.rotateDeg(childBone.getWorldRotationX());
            bodyEDef.position.set(childBone.getWorldX() + r.x, childBone.getWorldY() + r.y);
            Body anchorEBody = config.world.createBody(bodyEDef);

            // Create all joints between the child segment and the parent segment.
            creatJointDefs(anchorCBody, anchorEBody, parent, newWormSegment);

            // Recursion.
            createBox2dWormSegNJoint(childBone, newWormSegment);
        }
    }

    public void creatJointDefs(Body anchorCBody, Body anchorEBody, WormSegment parent, WormSegment child) {
        // Create anchor joints.
        DistanceJointDef anchorCJointDef = Utils.createDistanceJointDef(config, anchorCBody, child.body);
        anchorCJointDef.length = 0;
        anchorCJointDef.localAnchorA.set(0, 0);
        anchorCJointDef.localAnchorB.set(0, 0);
        child.anchorCJointDef = anchorCJointDef;
        DistanceJointDef anchorEJointDef = Utils.createDistanceJointDef(config, anchorEBody, child.body);
        anchorEJointDef.length = 0;
        anchorEJointDef.localAnchorA.set(0, 0);
        anchorEJointDef.localAnchorB.set(config.segMidW / 2 + config.segEndW, 0);
        child.anchorEJointDef = anchorEJointDef;
        if (parent == null) return;

        // Create segment to segment joints.
        // Center to center joint.
        DistanceJointDef parentCJointDef = Utils.createDistanceJointDef(config, parent.body, child.body);
        Vector2 bodyACtr = parentCJointDef.bodyA.getPosition();
        Vector2 bodyBCtr = parentCJointDef.bodyB.getPosition();
        parentCJointDef.length = bodyACtr.dst(bodyBCtr);
        parentCJointDef.localAnchorA.set(0, 0);
        parentCJointDef.localAnchorB.set(0, 0);
        child.parentCJointDef = parentCJointDef;
        // End to end.
        // Connect the shortest ends of both, then the furthest ends of both.
        DistanceJointDef parentE1JointDef = Utils.createDistanceJointDef(config, parent.body, child.body);
        DistanceJointDef parentE2JointDef = Utils.createDistanceJointDef(config, parent.body, child.body);
        Vector2 bodyAEndUR = new Vector2(config.joinPos, config.segMidH);
        Vector2 bodyAEndLL = new Vector2(-config.joinPos, -config.segMidH);
        Vector2 bodyBEndUR = new Vector2(config.joinPos, config.segMidH);
        Vector2 bodyBEndLL = new Vector2(-config.joinPos, -config.segMidH);
        bodyAEndUR.rotateRad(parentE1JointDef.bodyA.getAngle());
        bodyAEndLL.rotateRad(parentE1JointDef.bodyA.getAngle());
        bodyBEndUR.rotateRad(parentE1JointDef.bodyB.getAngle());
        bodyBEndLL.rotateRad(parentE1JointDef.bodyB.getAngle());
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
            parentE1JointDef.length = AURBUR;
            parentE1JointDef.localAnchorA.set(config.joinPos, config.segMidH);
            parentE1JointDef.localAnchorB.set(config.joinPos, config.segMidH);
            parentE2JointDef.length = ALLBLL;
            parentE2JointDef.localAnchorA.set(-config.joinPos, -config.segMidH);
            parentE2JointDef.localAnchorB.set(-config.joinPos, -config.segMidH);
        } else {
            parentE1JointDef.length = AURBLL;
            parentE1JointDef.localAnchorA.set(config.joinPos, config.segMidH);
            parentE1JointDef.localAnchorB.set(-config.joinPos, -config.segMidH);
            parentE2JointDef.length = ALLBUR;
            parentE2JointDef.localAnchorA.set(-config.joinPos, -config.segMidH);
            parentE2JointDef.localAnchorB.set(config.joinPos, config.segMidH);
        }
        child.parentE1JointDef = parentE1JointDef;
        child.parentE2JointDef = parentE2JointDef;

        // Update repulsive body array.
        if (bodyACtr.x < bodyBCtr.x) {
            repulsivePairs.add(parent);
            repulsivePairs.add(child);
        } else {
            repulsivePairs.add(child);
            repulsivePairs.add(parent);
        }
    }

    public void createBox2dWorm(Bone rootBone) {
        createPen();
        createBox2dWormSegNJoint(rootBone, null);

        for (WormSegment seg : segs) {
            if (seg.parent == null) continue;
            seg.parent.children.add(seg);
        }
    }

    public void destroyBox2dWorm() {
        Array<Joint> allJoints = new Array<>();
        config.world.getJoints(allJoints);
        for (Joint j : allJoints) {
            config.world.destroyJoint(j);
        }

        Array<Body> allBodies = new Array<>();
        config.world.getBodies(allBodies);
        for (Body b : allBodies) {
            config.world.destroyBody(b);
        }
    }

    public void kill() {
        for (WormSegment seg : segs) {
            seg.noOfStabilizations = config.maxStabilizedSegs;
            seg.updateBoneStabilization(config.boneStabilizationTime);
        }
    }

    public void step() {
        for (WormSegment seg : segs) {
            seg.step();
        }
    }

    public void updateBones(float deltaTime) {
        if (!config.enableInputsNBoneUpdate) return;

        // Update bone countdowns. Delete bones/joints when they expire.
        for (WormSegment seg : config.worm.segs)
            seg.updateBoneStabilization(deltaTime);

        // Detect inputs and update bones/joints.
        if (!Gdx.input.justTouched()) return;

        Vector2 touchPos = config.stageBack.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        Array<WormSegment> stableSegs = new Array<>();
        for (WormSegment seg: segs) {
            // Add currently stable segments.
            if (seg.isStable()) {
                stableSegs.add(seg);
                continue;
            }

            // Can no longer stablize even if touched.
            if (seg.noOfStabilizations >= config.segMaxStabilizationChances)
                continue;

            // Add new stable segments that are touched.
            if (seg.body.getPosition().dst(touchPos) <= config.touchRadius) {
                stableSegs.add(seg);
                seg.stabilizationCountdown = config.boneStabilizationTime;
                seg.spreadoutPhase = false;
                seg.noOfStabilizations += 1;
            }

        }

        // Sort stable segments by their age in ascending order.
        //stableSegs.sort(Utils.stableBoneComparator);
        // Destroy the older bones exceeding maxStabilizedSegs.
        for (int i = 0; i < stableSegs.size - config.maxStabilizedSegs; i++) {
            // Make them expire and delete them.
            stableSegs.get(i).updateBoneStabilization(config.boneStabilizationTime + 1);
        }
        // Add new joints if needed.
        for (int i = stableSegs.size - 1; i >= Math.max(0, stableSegs.size - config.maxStabilizedSegs); i--) {
            // Make them expire and delete them.
            stableSegs.get(i).createAllJoints();
        }
    }

    public static void drawWorm(float deltaTime, Config config) {
        if (config.wormOne != null)
            drawWormOne(deltaTime, config);
        else if (config.wormSkeleton != null)
            drawWormSkeleton(deltaTime, config);
        else
            drawWormBox2d(deltaTime, config);
    }

    public static void drawWormOne(float deltaTime, Config config) {
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
        config.wormOne.animationState.update(deltaTime);
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

    public static void drawWormSkeleton(float deltaTime, Config config) {
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
        config.wormSkeleton.animationState.update(deltaTime);
        config.wormSkeleton.animationState.apply(config.wormSkeleton.skeleton);
        config.skeletonRenderer.draw(config.spriteBatch, config.wormSkeleton.skeleton);
        config.spriteBatch.end();
    }

    public static void drawWormBox2d(float deltaTime, Config config) {
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

        }
        config.spriteBatch.end();

        // Draw joints.
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < config.worm.repulsivePairs.size / 2; i++) {
            WormSegment seg1 = config.worm.repulsivePairs.get(2 * i);
            WormSegment seg2 = config.worm.repulsivePairs.get(2 * i + 1);
            boolean broken = seg1.parentBoneVisuallyBroken();
            if (seg2.parent == seg1)
                broken = seg2.parentBoneVisuallyBroken();
            boolean stable = seg1.isStable()|| seg2.isStable();

            // Center to center bone.
            Vector2 body1Ctr = seg1.body.getPosition();
            Vector2 body2Ctr = seg2.body.getPosition();
            Utils.drawBone(config.shapeRenderer, broken, stable, config.boneDashDrawLen, body1Ctr.x, body1Ctr.y, body2Ctr.x, body2Ctr.y);
            // End to end bone.
            float angle1 = seg1.body.getAngle() * 57.2958f;
            float angle2 = seg2.body.getAngle() * 57.2958f;
            Vector2 disp1 = new Vector2(config.segTexture.getWidth() / 2.2f, 0);
            Vector2 disp2 = new Vector2(-config.segTexture.getWidth() / 2.2f, 0);
            disp1.rotateDeg(angle1);
            disp2.rotateDeg(angle2);
            Vector2 ctrDisp1 = body1Ctr.add(disp1);
            Vector2 ctrDisp2 = body2Ctr.add(disp2);
            Utils.drawBone(config.shapeRenderer, broken, stable, config.boneDashDrawLen, ctrDisp1.x, ctrDisp1.y, ctrDisp2.x, ctrDisp2.y);
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
            seg.animationState.update(deltaTime);
            seg.animationState.apply(seg.skeleton);
            config.skeletonRenderer.draw(config.spriteBatch, seg.skeleton);
        }
        config.spriteBatch.end();

        // Game play aids.
        if (config.worm.segs.size > 0) {
            if (config.drawInstabilities)
                Utils.drawInstabilities(config, deltaTime);

            if (config.drawTouch)
                Utils.drawTouch(config, deltaTime);
        }
    }
}

