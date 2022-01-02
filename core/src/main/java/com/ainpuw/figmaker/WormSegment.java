package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

        // Init for stage.
        float segMidW = config.segMidW;
        float segMidH = config.segMidH;
        float segEndW = config.segEndW;

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

    public void step() {
        // FIXME: This is just for debug.
        // Given different segments, different forces should be applied, even periodically.
        // FIXME: Need to put force values into game config.
        if (Math.random() > 0.5)
            body.applyLinearImpulse(new Vector2(0, 10000), body.getPosition(), true);
        else
            body.applyLinearImpulse(new Vector2(0, -10000), body.getPosition(), true);


    }

}
