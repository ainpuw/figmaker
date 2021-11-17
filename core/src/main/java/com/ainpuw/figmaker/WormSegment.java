package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class WormSegment {
    public GameConfig gameConfig;
    private UIConfig uiConfig;
    private String name;  // Unique identifier.
    public Array<BasicSegment> basicSegs = new Array<>();

    public WormSegment(GameConfig gameConfig, UIConfig uiConfig, String name, float x, float y) {
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;
        this.name = name;

        if (name == "seg_balloon") {
            basicSegs.add(new BasicSegment(x, y, 0, this));
        } else if (name == "seg_arm") {
            basicSegs.add(new BasicSegment(x, y, 0, this));
        } else if (name == "seg_pendulum") {
            basicSegs.add(new BasicSegment(x, y, 0, this));
        } else if (name == "seg_fan") {
            basicSegs.add(new BasicSegment(x, y, 0, this));
        } else if (name == "seg_leg") {
            basicSegs.add(new BasicSegment(x, y, 0, this));
        } else if (name == "seg_wing") {
            basicSegs.add(new BasicSegment(x, y, 0, this));
            /*
            DistanceJointDef distanceJointDef = new DistanceJointDef();
            distanceJointDef.collideConnected = true;
            distanceJointDef.length = gameConfig.jointLen;
            for (int i = 0; i < 19; i++) {
                distanceJointDef.bodyA = segs.get(i).body;
                distanceJointDef.bodyB = segs.get(i+1).body;
                distanceJointDef.localAnchorA.set(-jPos/2, 0);
                distanceJointDef.localAnchorB.set(jPos/2, 0);
                world.createJoint(distanceJointDef);
            }
             */
        } else {
            basicSegs.add(new BasicSegment(x, y, 0, this));
        }
    }

    public void step() {
        // FIXME: This is just for debug.
        // FIXME: Need to put forces into game config?
        for (BasicSegment seg : basicSegs) {
            if (Math.random() > 0.8)
                seg.body.applyLinearImpulse(new Vector2(0, 10000), seg.body.getPosition(), true);
            seg.body.applyLinearImpulse(new Vector2(0, -5000 * Math.min(1, seg.body.getPosition().y / 500f)), seg.body.getPosition(), true);
        }

    }

    ////////////////////////////////////////////////////
    // Subclass definitions
    ////////////////////////////////////////////////////

    public class BasicSegment {
        public Body body;
        public BasicImgSegment leftEnd;
        public BasicImgSegment rightEnd;
        public WormSegment parent;

        public BasicSegment(float x, float y, float angle, WormSegment parent) {
            this.parent = parent;

            // Init for world.
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);  // Body center position.
            this.body = gameConfig.world.createBody(bodyDef);
            // Adjust fixture orientations.
            angle *= 0.01745f;  // Degrees to radians.
            Vector2 lCenter = new Vector2(-(gameConfig.segMidW+ gameConfig.segEndW)/2, 0);
            Vector2 rCenter = new Vector2(+(gameConfig.segMidW+ gameConfig.segEndW)/2, 0);
            lCenter.rotateRad(angle);
            rCenter.rotateRad(angle);
            gameConfig.segShapeL.setAsBox(gameConfig.segEndW/2, gameConfig.segEndH/2, lCenter, angle);
            gameConfig.segShapeR.setAsBox(gameConfig.segEndW/2, gameConfig.segEndH/2, rCenter, angle);
            gameConfig.segShapeM.setAsBox(gameConfig.segMidW/2, gameConfig.segMidH/2, new Vector2(0, 0), 0);
            this.body.createFixture(gameConfig.segFixtureDefL);
            this.body.createFixture(gameConfig.segFixtureDefR);
            this.body.createFixture(gameConfig.segFixtureDefM);

            // Init for stage.
            float segMidW = gameConfig.segMidW;
            float segMidH = gameConfig.segMidH;
            float segEndW = gameConfig.segEndW;
            this.leftEnd = new BasicImgSegment(gameConfig.segIndicatorLeftTexture, this, true);
            this.rightEnd = new BasicImgSegment(gameConfig.segIndicatorRightTexture, this, false);
            this.leftEnd.setSize(segMidW / 2 + segEndW, segMidH);
            this.leftEnd.setBounds(0, 0, this.leftEnd.getWidth(), this.leftEnd.getHeight());
            this.rightEnd.setSize(this.leftEnd.getWidth(), this.leftEnd.getHeight());
            this.rightEnd.setBounds(0, 0, this.leftEnd.getWidth(), this.leftEnd.getHeight());
            this.leftEnd.getColor().a = 0f;  // Make the indicator transparent.
            this.rightEnd.getColor().a = 0f;
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
            uiConfig.stage.addActor(leftEnd);
            uiConfig.stage.addActor(rightEnd);

            // Show Scene2D debug bounding boxes.
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
