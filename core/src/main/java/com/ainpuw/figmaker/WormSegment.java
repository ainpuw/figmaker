package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class WormSegment {
    public Config config;
    public Body body;
    public Skeleton skeleton;
    public AnimationState animationState;

    // Connectivity information.
    public WormSegment parent = null;
    public Array<WormSegment> children = new Array<>();
    public Joint anchorCJoint = null;  // Segment center to the segment center in the animation.
    public DistanceJointDef anchorCJointDef = null;
    public Joint anchorEJoint = null;  // Segment right end to the segment right end in the animation.
    public DistanceJointDef anchorEJointDef = null;
    public Joint parentCJoint = null;  // Center to parent center.
    public DistanceJointDef parentCJointDef = null;
    public Joint parentE1Joint = null;  // The shortest end to end joint.
    public DistanceJointDef parentE1JointDef = null;
    public Joint parentE2Joint = null;  // The longest end to end joint.
    public DistanceJointDef parentE2JointDef = null;
    public float stabilizationCountdown = -0.001f;
    public float instabilityAnimationCountdown;
    public int noOfStabilizations = 0;

    public WormSegment(Config config, float x, float y, float angle) {
        this.config = config;

        // Init for world.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);  // Body center position.
        bodyDef.angle = angle * 0.01745f;
        this.body = config.world.createBody(bodyDef);
        // Adjust fixture orientations.
        Vector2 lCenter = new Vector2(-(config.segMidW+ config.segEndW)/2, 0);
        Vector2 rCenter = new Vector2(+(config.segMidW+ config.segEndW)/2, 0);
        config.segShapeL.setAsBox(config.segEndW/2, config.segEndH/2, lCenter, 0);
        config.segShapeR.setAsBox(config.segEndW/2, config.segEndH/2, rCenter, 0);
        config.segShapeM.setAsBox(config.segMidW/2, config.segMidH/2, new Vector2(0, 0), 0);
        this.body.createFixture(config.segFixtureDefL);
        this.body.createFixture(config.segFixtureDefR);
        this.body.createFixture(config.segFixtureDefM);

        // Spine animation setup.
        Config.SpineActorConfig spineConfig = config.spineActorConfigs.get("wormseg");
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(spineConfig.atlas));
        SkeletonJson json = new SkeletonJson(atlas);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(spineConfig.skeletonJson));
        AnimationStateData stateData = new AnimationStateData(skeletonData);
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setToSetupPose();
        this.skeleton.updateWorldTransform();
        this.animationState = new AnimationState(stateData);
        this.animationState.setAnimation(0, spineConfig.defaultAnimation, true);
        animationState.update((float) Math.random());  // Random offset.
        animationState.apply(skeleton);

        // Other.
        instabilityAnimationCountdown = (float) Math.random() * config.segInstabilityAnimationTime;
    }

    public boolean boneVisuallyBroken() {
        if (parent == null) return false;

        if (body.getPosition().dst(parent.body.getPosition()) <
            parentCJointDef.length * config.boneBrokenVisualMargin)
            return false;
        return true;
    }

    public void updateBoneStabilization(float deltaTime) {
        stabilizationCountdown = Math.max(-0.001f, stabilizationCountdown - deltaTime);
        // Destroy joint to its original Spine animation location.
        if (!isStable()) {
            if (anchorCJoint != null) {
                config.world.destroyJoint(anchorCJoint);
                anchorCJoint = null;
            }
            if (anchorEJoint != null) {
                config.world.destroyJoint(anchorEJoint);
                anchorEJoint = null;
            }
        }
        // Allow one frame of parent-child asynchronization.
        // Handle this to parent bones.
        if (parent != null && !isStable() && !parent.isStable()) {
            if (parentCJoint != null) {
                config.world.destroyJoint(parentCJoint);
                parentCJoint = null;
            }
            if (parentE1Joint != null) {
                config.world.destroyJoint(parentE1Joint);
                parentE1Joint = null;
            }
            if (parentE2Joint != null) {
                config.world.destroyJoint(parentE2Joint);
                parentE2Joint = null;
            }
        }
        // Handle this to children bones.
        for (WormSegment child : children) {
            if (!isStable() && !child.isStable()) {

                if (child.parentCJoint != null) {
                    config.world.destroyJoint(child.parentCJoint);
                    child.parentCJoint = null;
                }
                if (child.parentE1Joint != null) {
                    config.world.destroyJoint(child.parentE1Joint);
                    child.parentE1Joint = null;
                }
                if (child.parentE2Joint != null) {
                    config.world.destroyJoint(child.parentE2Joint);
                    child.parentE2Joint = null;
                }
            }
        }
    }

    public void createAllJoints() {
        // Create joint to original Spine animation location.
        if (anchorCJoint == null)
            anchorCJoint = config.world.createJoint(anchorCJointDef);
        if (anchorEJoint == null)
            anchorEJoint = config.world.createJoint(anchorEJointDef);
        // Create this to parent joints.
        if (parent != null) {
            if (parentCJoint == null)
                parentCJoint = config.world.createJoint(parentCJointDef);
            /* These joints lock segments in.
            if (parentE1Joint == null)
                parentE1Joint = config.world.createJoint(parentE1JointDef);
            if (parentE2Joint == null)
                parentE2Joint = config.world.createJoint(parentE2JointDef);
            */
        }
        // Create this to children joints.
        for (WormSegment child : children) {
            if (child.parentCJoint == null)
                child.parentCJoint = config.world.createJoint(child.parentCJointDef);
            /* These joints lock segments in.
            if (child.parentE1Joint == null)
                child.parentE1Joint = config.world.createJoint(child.parentE1JointDef);
            if (child.parentE2Joint == null)
                child.parentE2Joint = config.world.createJoint(child.parentE2JointDef);
            */
        }
    }

    public boolean isStable() {
        return stabilizationCountdown >= 0;
    }

    public void step() {
        Vector2 pos = body.getPosition();
        boolean useRand = true;
        // Make sure the worm stays in the game play area.
        if (pos.y > config.h) {
            body.applyLinearImpulse(new Vector2(0, -config.randomImpulse), pos, true);
            useRand = false;
        }
        if (pos.y < config.segShadowYRangeRef.x + config.segMidW) {
            if (noOfStabilizations < config.segMaxStabilizationChances) {
                body.applyLinearImpulse(new Vector2(0, config.randomImpulse), pos, true);
            } else {
                body.applyLinearImpulse(new Vector2(0, config.randomImpulse/100), pos, true);
            }
            useRand = false;
        }
        if (pos.x > config.w) {
            body.applyLinearImpulse(new Vector2(-config.randomImpulse, 0), pos, true);
            useRand = false;
        }
        if (pos.x < 0) {
            body.applyLinearImpulse(new Vector2(config.randomImpulse, 0), pos, true);
            useRand = false;
        }

        // Apply a random impulse to the segment.
        if (useRand && noOfStabilizations < config.segMaxStabilizationChances) {
            double randNum = Math.random();
            // Random offset.
            pos.x += Math.random() * 2 - 1;
            float force = config.randomImpulse / (float) Math.pow(10, noOfStabilizations);
            if (randNum > 0.75)
                body.applyLinearImpulse(new Vector2(0, force), pos, true);
            else if (randNum > 0.5)
                body.applyLinearImpulse(new Vector2(0, -force), pos, true);
            else if (randNum > 0.25)
                body.applyLinearImpulse(new Vector2(force, 0), pos, true);
            else
                body.applyLinearImpulse(new Vector2(-force, 0), pos, true);
        }
    }

}
