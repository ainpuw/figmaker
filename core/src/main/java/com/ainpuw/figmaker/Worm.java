package com.ainpuw.figmaker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;

public class Worm {
    private GameConfig gameConfig;
    private UIConfig uiConfig;
    private World world;
    private Array<Body> pen = new Array<>();
    private Array<Body> segs = new Array<>();

    public Worm(GameConfig gameConfig, UIConfig uiConfig, World world) {
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;
        this.world = world;
        makeDebugWorm();
    }

    private void makeDebugWorm() {
        createPen();
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
        float segLen = 40;
        float segWid = 10;
        float endLen = 5;

        PolygonShape shape1 = new PolygonShape();
        PolygonShape shape2 = new PolygonShape();
        PolygonShape shape3 = new PolygonShape();
        shape1.setAsBox(endLen/2, 1, new Vector2(0, -segLen/2), 0);
        shape2.setAsBox(endLen/2, 1, new Vector2(0, segLen/2), 0);
        shape3.setAsBox(segWid/2, segLen/2, new Vector2(0, 0), 0);
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = shape1;
        fixtureDef1.density = 5f;
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape2;
        fixtureDef2.density = 5f;
        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = shape3;
        fixtureDef3.density = 5f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        for (int i = 0; i < 20; i++) {
            bodyDef.position.set(500, 1100 - (segLen + 10) * i);
            segs.add(world.createBody(bodyDef));
            segs.get(segs.size - 1).createFixture(fixtureDef1);
            segs.get(segs.size - 1).createFixture(fixtureDef2);
            segs.get(segs.size - 1).createFixture(fixtureDef3);
        }

        shape1.setAsBox(1, endLen/2, new Vector2(-segLen/2, 0), 0);
        shape2.setAsBox(1, endLen/2, new Vector2(segLen/2,0), 0);
        shape3.setAsBox(segLen/2, segWid/2, new Vector2(0, 0), 0);
        for (int i = 0; i < 20; i++) {
            bodyDef.position.set(25 + (segLen + 10) * i, 525);
            segs.add(world.createBody(bodyDef));
            segs.get(segs.size - 1).createFixture(fixtureDef1);
            segs.get(segs.size - 1).createFixture(fixtureDef2);
            segs.get(segs.size - 1).createFixture(fixtureDef3);
        }

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.collideConnected=false;
        distanceJointDef.length = 10.0f;
        for (int i = 0; i < 19; i++) {
            distanceJointDef.bodyA = segs.get(i);
            distanceJointDef.bodyB = segs.get(i+1);
            distanceJointDef.localAnchorA.set(0, -segLen/2);
            distanceJointDef.localAnchorB.set(0, segLen/2);
            world.createJoint(distanceJointDef);
        }
        for (int i = 0; i < 9; i++) {
            distanceJointDef.bodyA = segs.get(i+21);
            distanceJointDef.bodyB = segs.get(i+20);
            distanceJointDef.localAnchorA.set(-segLen/2, 0);
            distanceJointDef.localAnchorB.set(segLen/2, 0);
            world.createJoint(distanceJointDef);
        }
        for (int i = 0; i < 9; i++) {
            distanceJointDef.bodyA = segs.get(i+31);
            distanceJointDef.bodyB = segs.get(i+30);
            distanceJointDef.localAnchorA.set(-segLen/2, 0);
            distanceJointDef.localAnchorB.set(segLen/2, 0);
            world.createJoint(distanceJointDef);
        }
        distanceJointDef.bodyA = segs.get(29);
        distanceJointDef.bodyB = segs.get(11);
        distanceJointDef.localAnchorA.set(segLen/2, 0);
        distanceJointDef.localAnchorB.set(0, -segLen/2);
        world.createJoint(distanceJointDef);
        distanceJointDef.bodyA = segs.get(11);
        distanceJointDef.bodyB = segs.get(30);
        distanceJointDef.localAnchorA.set(0, -segLen/2);
        distanceJointDef.localAnchorB.set(-segLen/2, 0);
        world.createJoint(distanceJointDef);

        shape1.dispose();
        shape2.dispose();
        shape3.dispose();
    }

    public void step() {
        // segs.get(0).applyAngularImpulse(1000, true);
        segs.get(0).applyLinearImpulse(new Vector2(0, 10000), segs.get(0).getPosition(), true);
        segs.get(21).applyLinearImpulse(new Vector2(0, 10000), segs.get(21).getPosition(), true);
        segs.get(38).applyLinearImpulse(new Vector2(0, 10000), segs.get(38).getPosition(), true);

    }
}

