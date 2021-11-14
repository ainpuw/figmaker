package com.ainpuw.figmaker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Worm {
    private GameConfig gameConfig;
    private UIConfig uiConfig;
    private Stage stage;
    private World world;
    private Array<Body> pen = new Array<>();
    public Array<Segment> segs = new Array<>();
    private PolygonShape shape1;  // End left.
    private PolygonShape shape2;  // End right.
    private PolygonShape shape3;  // Middle piece.
    private FixtureDef fixtureDef1;
    private FixtureDef fixtureDef2;
    private FixtureDef fixtureDef3;

    public Worm(GameConfig gameConfig, UIConfig uiConfig, Stage stage, World world) {
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;
        this.stage = stage;
        this.world = world;

        // Create segment definition.
        float segMidW = gameConfig.segMidW;
        float segMidH = gameConfig.segMidH;
        float segEndW = gameConfig.segEndW;
        float segEndH = gameConfig.segEndH;
        float segDensity = gameConfig.segDensity;
        this.shape1 = new PolygonShape();
        this.shape2 = new PolygonShape();
        this.shape3 = new PolygonShape();
        this.shape1.setAsBox(segEndW/2, segEndH/2, new Vector2(-(segMidW+segEndW)/2, 0), 0);
        this.shape2.setAsBox(segEndW/2, segEndH/2, new Vector2((segMidW+segEndW)/2, 0), 0);
        this.shape3.setAsBox(segMidW/2, segMidH/2, new Vector2(0, 0), 0);
        this.fixtureDef1 = new FixtureDef();
        this.fixtureDef1.shape = this.shape1;
        this.fixtureDef1.density = segDensity;
        this.fixtureDef2 = new FixtureDef();
        this.fixtureDef2.shape = this.shape2;
        this.fixtureDef2.density = segDensity;
        this.fixtureDef3 = new FixtureDef();
        this.fixtureDef3.shape = this.shape3;
        this.fixtureDef3.density = segDensity;

        createPen();
        makeDebugWorm();
    }

    private void makeDebugWorm() {
        createSegments();
    }

    private void createPen() {
        // Set pen positions.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // The floor.
        bodyDef.position.set(uiConfig.w / 2, uiConfig.penCenterY - uiConfig.penH / 2);
        pen.add(world.createBody(bodyDef));
        // The left wall.
        bodyDef.position.set(uiConfig.penCenterX - uiConfig.penW / 2, uiConfig.penCenterY);
        pen.add(world.createBody(bodyDef));
        // The right wall.
        bodyDef.position.set(uiConfig.penCenterX + uiConfig.penW / 2, uiConfig.penCenterY);
        pen.add(world.createBody(bodyDef));

        // Set pen wall shape.
        FixtureDef fixture = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        fixture.shape = shape;
        fixture.friction = gameConfig.friction;
        fixture.density = 0.0f;
        // The floor.
        shape.setAsBox(uiConfig.w, uiConfig.penThickness / 2);
        pen.get(0).createFixture(fixture);
        // The left wall.
        shape.setAsBox(uiConfig.penThickness / 2, uiConfig.penH / 2);
        pen.get(1).createFixture(fixture);
        // The right wall.
        shape.setAsBox(uiConfig.penThickness / 2, uiConfig.penH / 2);
        pen.get(2).createFixture(fixture);
        shape.dispose();
    }

    private void createSegments(){
        float jPos = gameConfig.segMidW + gameConfig.segEndW;

        for (int i = 0; i < 20; i++) {
            Segment newSeg = new Segment(500, 1100 - (gameConfig.segMidW + 10) * i);
            segs.add(newSeg);
        }
        for (int i = 0; i < 10; i++) {
            Segment newSeg = new Segment(25 + (gameConfig.segMidW + 10) * i, 525);
            segs.add(newSeg);
        }
        for (int i = 10; i < 20; i++) {
            Segment newSeg = new Segment(125 + (gameConfig.segMidW + 10) * i, 525);
            segs.add(newSeg);
        }

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.collideConnected = false;
        distanceJointDef.length = gameConfig.jointLen;
        for (int i = 0; i < 19; i++) {
            distanceJointDef.bodyA = segs.get(i).body;
            distanceJointDef.bodyB = segs.get(i+1).body;
            distanceJointDef.localAnchorA.set(-jPos/2, 0);
            distanceJointDef.localAnchorB.set(jPos/2, 0);
            world.createJoint(distanceJointDef);
        }
        for (int i = 0; i < 9; i++) {
            distanceJointDef.bodyA = segs.get(i+21).body;
            distanceJointDef.bodyB = segs.get(i+20).body;
            distanceJointDef.localAnchorA.set(-jPos/2, 0);
            distanceJointDef.localAnchorB.set(jPos/2, 0);
            world.createJoint(distanceJointDef);
        }
        for (int i = 0; i < 9; i++) {
            distanceJointDef.bodyA = segs.get(i+31).body;
            distanceJointDef.bodyB = segs.get(i+30).body;
            distanceJointDef.localAnchorA.set(-jPos/2, 0);
            distanceJointDef.localAnchorB.set(jPos/2, 0);
            world.createJoint(distanceJointDef);
        }
        distanceJointDef.bodyA = segs.get(29).body;
        distanceJointDef.bodyB = segs.get(11).body;
        distanceJointDef.localAnchorA.set(jPos/2, 0);
        distanceJointDef.localAnchorB.set(-jPos/2, 0);
        world.createJoint(distanceJointDef);
        distanceJointDef.bodyA = segs.get(11).body;
        distanceJointDef.bodyB = segs.get(30).body;
        distanceJointDef.localAnchorA.set(-jPos/2, 0);
        distanceJointDef.localAnchorB.set(-jPos/2, 0);
        world.createJoint(distanceJointDef);
    }

    public void step() {
        for (Segment seg : segs) {
            // Make sure the Actor positions are up to date with the Body position.
            seg.updateEndPositions();
        }

        segs.get(0).body.applyLinearImpulse(new Vector2(0, 10000), segs.get(0).body.getPosition(), true);
        segs.get(21).body.applyLinearImpulse(new Vector2(0, 10000), segs.get(21).body.getPosition(), true);
        segs.get(38).body.applyLinearImpulse(new Vector2(0, 10000), segs.get(38).body.getPosition(), true);
        if (Math.random() > 1)
            segs.get(20).body.applyAngularImpulse(-10000, true);
        else
            segs.get(20).body.applyAngularImpulse(10000, true);
    }

    public void dispose() {
        shape1.dispose();
        shape2.dispose();
        shape3.dispose();
    }

    ////////////////////////////////////////////////////
    // Subclass definitions
    ////////////////////////////////////////////////////

    class Segment {
        Body body;
        Actor leftEnd;
        Actor rightEnd;

        public Segment(float x, float y) {
            // Init for world.
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            this.body = world.createBody(bodyDef);
            this.body.createFixture(fixtureDef1);
            this.body.createFixture(fixtureDef2);
            this.body.createFixture(fixtureDef3);

            // Init for stage.
            float scale = gameConfig.segEndHitboxScale;
            float segEndW = gameConfig.segEndW;
            float segEndH = gameConfig.segEndH;
            this.leftEnd = new Actor();
            this.rightEnd = new Actor();
            this.leftEnd.setSize(segEndW, segEndH);
            this.leftEnd.setBounds(0, 0, segEndW * scale, segEndH * scale);
            this.rightEnd.setSize(segEndW, segEndH);
            this.rightEnd.setBounds(0, 0, segEndW * scale, segEndH * scale);
            this.updateEndPositions();
            stage.addActor(leftEnd);
            stage.addActor(rightEnd);

            leftEnd.debug();
            rightEnd.debug();

        }

        public void updateEndPositions() {
            Vector2 left = new Vector2(-(gameConfig.segEndW + gameConfig.segMidW) / 2, 0);
            Vector2 right = new Vector2((gameConfig.segEndW + gameConfig.segMidW) / 2, 0);
            float angle = this.body.getAngle();
            left.rotateRad(angle);
            right.rotateRad(angle);
            left = left.add(this.body.getPosition());
            right = right.add(this.body.getPosition());

            leftEnd.setPosition(left.x - gameConfig.segEndW/2, left.y - gameConfig.segEndH/2);
            rightEnd.setPosition(right.x - gameConfig.segEndW/2, right.y - gameConfig.segEndH/2);

        }
    }
}

