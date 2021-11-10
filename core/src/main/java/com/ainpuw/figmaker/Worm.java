package com.ainpuw.figmaker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;

public class Worm {
    private World world;
    private Body floor;
    private Array<Body> segs = new Array<>();

    public Worm(World world) {
        this.world = world;
        makeDebugWorm();

    }

    private void makeDebugWorm() {
        createFloor();
        createObject();
        createJoints();
    }

    private void createFloor() {
        // create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(500, 50);

        // add it to the world
        floor = world.createBody(bodyDef);

        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(500, 10);  // FIXME: WHY IS IT SO LONG?

        // create the physical object in our body)
        // without this our body would just be data in the world
        floor.createFixture(shape, 0.0f);

        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }

    private void createObject(){
        for (int i = 0; i < 3; i++) {
            //create a new body definition (type and location)
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(400 + 10 * i, 400 - 100 * i);

            // add it to the world
            segs.add(world.createBody(bodyDef));

            // set the shape (here we use a box 50 meters wide, 1 meter tall )
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(1, 50);

            // set the properties of the object ( shape, weight, restitution(bouncyness)
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 100f;

            // create the physical object in our body)
            // without this our body would just be data in the world
            segs.get(segs.size - 1).createFixture(fixtureDef);

            // we no longer use the shape object here so dispose of it.
            shape.dispose();
        }
    }

    public void createJoints() {
        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.bodyA = segs.get(0);
        distanceJointDef.bodyB= segs.get(1);
        distanceJointDef.collideConnected=false;
        distanceJointDef.length = 1.0f;
        distanceJointDef.localAnchorA.set(0, -50);
        distanceJointDef.localAnchorB.set(0, 50);
        world.createJoint(distanceJointDef);

        distanceJointDef.bodyA = segs.get(1);
        distanceJointDef.bodyB= segs.get(2);
        distanceJointDef.collideConnected=false;
        distanceJointDef.length = 1.0f;
        distanceJointDef.localAnchorA.set(0, -50);
        distanceJointDef.localAnchorB.set(0, 50);
        world.createJoint(distanceJointDef);
    }

    public void step() {
        for (int i = 0; i < 3; i++) {
            segs.get(0).applyAngularImpulse(1000 + i * 500, true);
        }

        if (Math.random() > 0.1) {
            segs.get(0).applyLinearImpulse(
                    new Vector2(0, 400),
                    new Vector2(0, 50),
                    true);
        }
    }
}

