package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.FloatArray;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SpineActor extends Actor {
    public Skeleton skeleton;
    private SkeletonRenderer skeletonRenderer;
    public AnimationState animationState;

    public SpineActor(Config.SpineActorConfig config, SkeletonRenderer skeletonRenderer) {
        this.skeletonRenderer = skeletonRenderer;

        // Spine animation setup.
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(config.atlas));
        SkeletonJson json = new SkeletonJson(atlas);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(config.skeletonJson));
        AnimationStateData stateData = new AnimationStateData(skeletonData);
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setToSetupPose();
        this.skeleton.updateWorldTransform();
        this.animationState = new AnimationState(stateData);
        this.animationState.setAnimation(0, config.defaultAnimation, true);
        this.setName(config.name);

        // Spine animation repositioning and resizing.
        this.setPosition(config.x, config.y);
        if (config.w != 0-1 && config.h != -1) {
            Vector2 offset = new Vector2();
            Vector2 size = new Vector2();
            FloatArray temp = new FloatArray();
            this.skeleton.getBounds(offset, size, temp);
            float skeletonScaleX = config.w / size.x;
            float skeletonScaleY = config.h / size.y;
            this.skeleton.setScale(skeletonScaleX, skeletonScaleY);
        }
    }

    @Override
    public void act(float delta) {
        skeleton.setX(this.getX());
        skeleton.setY(this.getY());
        skeleton.updateWorldTransform();
        animationState.update(delta);
        animationState.apply(skeleton);
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        skeleton.getColor().a = parentAlpha;
        skeletonRenderer.draw(batch, skeleton);
        super.draw(batch, parentAlpha);
    }
}