package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;

public class Intro extends Scenario {
    SpineActor logoActor;

    public Intro(Config config) {
        super(config);
        config.stage.clear();
        logoActor = new SpineActor(config.spineActorConfigs.get("logo"));
        logoActor.animationState.setAnimation(0, "grow", false);

        events.add(new Event(config, "logo_animation") {
            public void init() {
                // Skip the first frame that has the coordinates of the setup mode.
                logoActor.animationState.update(0.01f);
                logoActor.animationState.apply(logoActor.skeleton);

                config.stage.addActor(logoActor);
            }
            public void step(float deltaTime) {
                String animationName = logoActor.animationState.getTracks().get(0).toString();

                if (animationName.equals("grow")) {
                    if (Gdx.input.justTouched() ||  logoActor.animationState.getTracks().get(0).isComplete())
                        logoActor.animationState.setAnimation(0, "idle", true);
                } else if (animationName.equals("idle")) {
                    if (Gdx.input.justTouched())
                        logoActor.animationState.setAnimation(0, "death", false);
                } else if (animationName.equals("death")) {
                    if (Gdx.input.justTouched() || logoActor.animationState.getTracks().get(0).isComplete()) {
                        active = false;
                        ended = true;
                        // TODO: Instead of dispose(), we should add intro here!
                        dispose();
                    }
                }
            }
            public void dispose() {
                logoActor.remove();
            }
        });
    }

    public Scenario nextScenario() {
        return new Level1(config);
    }
}
