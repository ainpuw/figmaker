package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class BackgroundActor extends Actor {
    UIConfig config;
    Animation<TextureRegion> animation;
    Image sky;
    Image floor;

    public BackgroundActor(UIConfig config) {
        this.config = config;

        TextureRegion[][] textureRegions = TextureRegion.split(config.backgroundSkyTexture,
                (int) config.backgroundFrameSize.x, (int) config.backgroundFrameSize.y);
        Array<TextureRegion> frames = new Array<>();

        // Forward.
        for (int i = 0; i < config.backgroundFrames; i++)
            frames.add(textureRegions[i][0]);
        // Backward.
        for (int i = config.backgroundFrames - 2; i > 0; i--) {
            frames.add(textureRegions[i][0]);
        }

        Animation<TextureRegion> animation = new Animation(config.backgroundFrameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        this.animation = animation;
        this.sky = new Image(config.backgroundFloorTexture);  // Just a placeholder.
        this.floor = new Image(config.backgroundFloorTexture);
        this.sky.setSize(config.backgroundFrameSize.x, config.backgroundFrameSize.y);
        this.floor.setSize(config.backgroundFrameSize.x, config.backgroundFrameSize.y);
        this.sky.setPosition(-config.backgroundFrameSize.x/4, -config.backgroundFrameSize.y/2 + config.backgroundFrameSize.y);
        this.floor.setPosition(-config.backgroundFrameSize.x/4, -config.backgroundFrameSize.y/2);
    }

    @Override
    public void act(float delta) {
        config.backgroundStateTime = Math.min(config.backgroundStateTime + delta, 1000000);
        TextureRegion frame = animation.getKeyFrame(config.backgroundStateTime);
        ((TextureRegionDrawable)this.sky.getDrawable()).setRegion(frame);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sky.draw(batch, parentAlpha);
        floor.draw(batch, parentAlpha);
    }
}
