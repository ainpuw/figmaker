package com.ainpuw.figmaker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

    public static void drawWorm(GameConfig config, SpriteBatch spriteBatch) {
        spriteBatch.begin();

        // Draw shadow.
        for (WormSegment seg : config.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                float angle = basicSeg.body.getAngle() * 57.2958f;
                Vector2 ctrPos = new Vector2(basicSeg.body.getPosition().x, basicSeg.body.getPosition().y);
                Vector2 disp = new Vector2(- config.segTexture.getWidth()/2, - config.segTexture.getHeight()/2);
                disp.rotateDeg(angle);
                Vector2 corPos = ctrPos.add(disp);

                spriteBatch.draw(config.shadowTextureRegions[0][0],
                        corPos.x, config.segShadowOffsetY + basicSeg.body.getPosition().y * 0.2f,
                        0, 0,
                        Math.abs(disp.x) * 2 / basicSeg.body.getPosition().y * 100, config.segTexture.getHeight() / basicSeg.body.getPosition().y * 100,
                        1, 1,
                        0);
            }
        }

        // Draw joints.
        // ...

        // Draw segments.
        for (WormSegment seg : config.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                float angle = basicSeg.body.getAngle() * 57.2958f;
                Vector2 ctrPos = new Vector2(basicSeg.body.getPosition().x, basicSeg.body.getPosition().y);
                Vector2 disp = new Vector2(- config.segTexture.getWidth()/2, - config.segTexture.getHeight()/2);
                disp.rotateDeg(angle);
                Vector2 corPos = ctrPos.add(disp);

                spriteBatch.draw(config.segTextureRegions[0][0],
                        corPos.x, corPos.y,
                        0, 0,
                        config.segTexture.getWidth(), config.segTexture.getHeight(),
                        1, 1,
                        angle);
            }
        }

        spriteBatch.end();
    }
}

