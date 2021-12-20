package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class WormSegment {
    public Config config;
    public Array<BasicSegment> basicSegs = new Array<>();

    public WormSegment(Config config, float x, float y, float angle) {
        this.config = config;
        basicSegs.add(new BasicSegment(x, y, angle, this));
    }

    public void step() {
        // FIXME: This is just for debug.
        // Given different segments, different forces should be applied, even periodically.
        // FIXME: Need to put force values into game config.
        for (BasicSegment seg : basicSegs) {
            if (Math.random() > 0.5)
                seg.body.applyLinearImpulse(new Vector2(0, 10000), seg.body.getPosition(), true);
            else
                seg.body.applyLinearImpulse(new Vector2(0, -10000), seg.body.getPosition(), true);
        }

    }

    ////////////////////////////////////////////////////
    // Subclass definitions
    ////////////////////////////////////////////////////

    public class BasicSegment {
        public Body body;
        public BasicImgSegment leftEnd;  // For drag and drop visual indications.
        public BasicImgSegment rightEnd;  // For drag and drop visual indications.
        public WormSegment parent;
        // For spine animation.
        public Skeleton skeleton;
        public AnimationState animationState;

        public BasicSegment(float x, float y, float angle, WormSegment parent) {
            this.parent = parent;

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

            // Init for stage.
            float segMidW = config.segMidW;
            float segMidH = config.segMidH;
            float segEndW = config.segEndW;
            this.leftEnd = new BasicImgSegment(config.segIndicatorLeftTexture, this, true);
            this.rightEnd = new BasicImgSegment(config.segIndicatorRightTexture, this, false);
            this.leftEnd.setSize(segMidW / 2 + segEndW, segMidH);
            this.leftEnd.setBounds(0, 0, this.leftEnd.getWidth(), this.leftEnd.getHeight());
            this.rightEnd.setSize(this.leftEnd.getWidth(), this.leftEnd.getHeight());
            this.rightEnd.setBounds(0, 0, this.leftEnd.getWidth(), this.leftEnd.getHeight());
            this.leftEnd.getColor().a = 0f;  // Make the indicator transparent.
            this.rightEnd.getColor().a = 0f;

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
        }

        public void updateAndAddToStage() {
            // Prepare displacement vectors.
            Vector2 leftCtr = new Vector2(-leftEnd.getWidth() / 2, 0);
            Vector2 rightCtr = new Vector2(rightEnd.getWidth() / 2, 0);
            Vector2 leftCorner = new Vector2(-leftEnd.getWidth() / 2, -leftEnd.getHeight() / 2);
            Vector2 rightCorner = new Vector2(-rightEnd.getWidth() / 2, -rightEnd.getHeight() / 2);
            float angle = this.body.getAngle();
            leftCtr.rotateRad(angle);
            rightCtr.rotateRad(angle);
            leftCorner.rotateRad(angle);
            rightCorner.rotateRad(angle);

            // Calculate the actor center position - Bodies use center position.
            leftCtr = leftCtr.add(this.body.getPosition());
            rightCtr = rightCtr.add(this.body.getPosition());

            // Calculate the actor lower left corner position - Actors use origin position.
            leftEnd.setPosition(leftCtr.x + leftCorner.x, leftCtr.y + leftCorner.y);
            rightEnd.setPosition(rightCtr.x + rightCorner.x, rightCtr.y + rightCorner.y);
            leftEnd.setRotation(angle * 57.2958f);  // Convert radian to angle.
            rightEnd.setRotation(angle * 57.2958f);

            // Add the actors to stage.
            config.stage.addActor(leftEnd);
            config.stage.addActor(rightEnd);

            // FIXME: For debug, show Scene2D debug bounding boxes.
            leftEnd.debug();
            rightEnd.debug();
        }

        public void removeFromStage() {
            leftEnd.remove();
            rightEnd.remove();
        }
    }

    public class BasicImgSegment extends Image {
        BasicSegment parent;
        boolean isLeft;

        public BasicImgSegment(Texture texture, BasicSegment parent, boolean isLeft) {
            super(texture);
            this.parent = parent;
            this.isLeft = isLeft;
        }
    }

}
