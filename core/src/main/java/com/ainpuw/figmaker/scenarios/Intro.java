package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;

public class Intro extends Scenario {
    SpineActor logoActor;
    SpineActor introActor;

    public Intro(Config config) {
        super(config);
        config.stage.clear();
        logoActor = new SpineActor(config.spineActorConfigs.get("logo"), config.skeletonRenderer);
        logoActor.animationState.setAnimation(0, "grow", false);
        introActor = new SpineActor(config.spineActorConfigs.get("intro"), config.skeletonRenderer);

        events.add(new Event(config, "intro_animation") {
            public void init() {
                // Skip the first frame that has the coordinates of the setup mode.
                logoActor.animationState.update(0.01f);
                logoActor.animationState.apply(logoActor.skeleton);

                config.stage.addActor(logoActor);
            }
            public void step(float deltaTime) {
                String animationName = logoActor.animationState.getTracks().get(0).toString();

                if (introActor.getStage() != null) {
                    if (Gdx.input.justTouched() || introActor.animationState.getTracks().get(0).isComplete()) {
                        config.stage.addActor(config.background);
                        config.stage.addActor(config.character);
                        introActor.remove();
                    }
                } else if (config.background.getStage() != null) {
                    if (config.background.animationState.getTracks().get(0).isComplete()) {
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
                        config.stage.addActor(introActor);
                    }
                }
            }
            public void dispose() {
                config.character.animationState.setAnimation(0, "idle", true);
                config.background.animationState.setAnimation(0, "idle", true);
                logoActor.remove();
                introActor.remove();
            }
        });
    }

    public Scenario nextScenario() {
        return new Level1(config);
    }
}
