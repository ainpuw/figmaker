package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;

public class IntroEvent extends Event {
    SpineActor logoActor;
    SpineActor introActor;
    public IntroEvent(Config config, String name) {
        super(config, name);
        init();
    }

    public void init() {
        logoActor = new SpineActor(config.spineActorConfigs.get("logo"), config.skeletonRenderer);
        logoActor.animationState.setAnimation(0, "grow", false);
        introActor = new SpineActor(config.spineActorConfigs.get("intro"), config.skeletonRenderer);

        // Skip the first frame that has the coordinates of the setup mode.
        logoActor.animationState.update(0.01f);
        logoActor.animationState.apply(logoActor.skeleton);

        config.stageBack.addActor(logoActor);
    }

    public void step(float deltaTime) {
        String animationName = logoActor.animationState.getTracks().get(0).toString();

        if (introActor.getStage() != null) {
            if (Gdx.input.justTouched() || introActor.animationState.getTracks().get(0).isComplete()) {
                config.stageBack.addActor(config.background);
                config.stageBack.addActor(config.character);
                introActor.remove();
            }
        } else if (config.background.getStage() != null) {
            if (Gdx.input.justTouched() || config.background.animationState.getTracks().get(0).isComplete()) {
                dispose();
                active = false;
                ended = true;
            }
        } else if (animationName.equals("grow")) {
            if (Gdx.input.justTouched() ||  logoActor.animationState.getTracks().get(0).isComplete())
                logoActor.animationState.setAnimation(0, "idle", true);
        } else if (animationName.equals("idle")) {
            if (Gdx.input.justTouched())
                logoActor.animationState.setAnimation(0, "death", false);
        } else if (animationName.equals("death")) {
            if (Gdx.input.justTouched() || logoActor.animationState.getTracks().get(0).isComplete()) {
                logoActor.remove();
                config.stageBack.addActor(introActor);
            }
        }
    }

    public void dispose() {
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        logoActor.remove();
        introActor.remove();
    }
}