package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;

public class IntroEvent extends Event {
    SpineActor logoActor;
    SpineActor introActor;
    private boolean tuneDownTheme = false;
    private float tuneDownThemeTime = 0;

    public IntroEvent(Config config, String name, int expId) {
        super(config, name, expId);
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

        // Play music.
        config.theme.play();
    }

    public void step(float deltaTime) {
        String animationName = logoActor.animationState.getTracks().get(0).toString();

        // Fade drive audio.
        if (config.drive.isPlaying()) {
            config.drive.setVolume(Math.max(0, config.drive.getVolume() - 0.1f * deltaTime));
        }
        if (config.theme.isPlaying() && tuneDownTheme) {
            tuneDownThemeTime += deltaTime;
            // The tree death animation is 1.66 seconds.
            config.theme.setVolume(Math.max(0, 1.6f-tuneDownThemeTime) / 1.6f);
        }

        if (introActor.getStage() != null) {
            if (introActor.animationState.getTracks().get(0).isComplete()) {
                config.stageBack.addActor(config.background);
                config.stageBack.addActor(config.character);
                introActor.remove();
                config.drive.stop();
                config.day.play();
            }
        } else if (config.background.getStage() != null) {
            if ( config.background.animationState.getTracks().get(0).isComplete()) {
                dispose();
                active = false;
                ended = true;
            }
        } else if (animationName.equals("grow")) {
            if (Gdx.input.justTouched() || logoActor.animationState.getTracks().get(0).isComplete())
                logoActor.animationState.setAnimation(0, "idle", true);
        } else if (animationName.equals("idle")) {
            if (Gdx.input.justTouched()) {
                logoActor.animationState.setAnimation(0, "death", false);
                tuneDownTheme = true;
            }
        } else if (animationName.equals("death")) {
            if (logoActor.animationState.getTracks().get(0).isComplete()) {
                logoActor.remove();
                config.stageBack.addActor(introActor);
                // Change to intro animation audio.
                config.theme.stop();
                config.drive.play();
                config.drive.setVolume(1);
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