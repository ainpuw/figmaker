package com.ainpuw.figmaker;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;

public class Worm {
    private GameConfig gameConfig;
    private UIConfig uiConfig;
    private Array<Body> pen = new Array<>();
    public Array<WormSegment> segs = new Array<>();

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
        shape.setAsBox(uiConfig.w, gameConfig.penThickness / 2);
        pen.get(0).createFixture(fixture);
        // The left wall.
        shape.setAsBox(gameConfig.penThickness / 2, gameConfig.penH / 2);
        pen.get(1).createFixture(fixture);
        // The right wall.
        shape.setAsBox(gameConfig.penThickness / 2, gameConfig.penH / 2);
        pen.get(2).createFixture(fixture);
        shape.dispose();
    }

    private void makeDebugWorm() {
       segs.add(new WormSegment(gameConfig, uiConfig, "seg_balloon", 500, 100));
    }

    public void step() {
        for (WormSegment seg : segs) {
            seg.step();
        }
    }

    public static void joinSegments(WormSegment newSeg, WormSegment.BasicSegment joinSeg, boolean isLeft) {
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
        if (isLeft) {
            // Join right side of newSeg to the left side of joinSeg.
            distanceJointDef.localAnchorA.set(newSeg.gameConfig.joinPos, 0);
            distanceJointDef.localAnchorB.set(-newSeg.gameConfig.joinPos, 0);
        } else {
            // Join left side of newSeg to the right side of joinSeg.
            distanceJointDef.localAnchorA.set(-newSeg.gameConfig.joinPos, 0);
            distanceJointDef.localAnchorB.set(newSeg.gameConfig.joinPos, 0);
        }
        newSeg.gameConfig.world.createJoint(distanceJointDef);
    }
}

