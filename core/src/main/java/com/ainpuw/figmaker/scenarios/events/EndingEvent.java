package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class EndingEvent extends Event {
    private String dialogueFile;
    private final float darkOutLen = 2.5f;
    private float darkOutCounter = 0;
    private boolean darken = true;
    private float eventEndCountdown = 3;
    private boolean dialogueDone = false;
    SpineActor logoActor;

    public EndingEvent(Config config, String name, int expId) {
        super(config, name, expId);
        this.dialogueFile = "dialogue/level" + expId + "_end.txt";

        config.dialogueBox.reset();
        config.dialogueBox.dialogueLines =
                Gdx.files.internal(dialogueFile).readString().split("\\r?\\n");
        config.dialogueBox.addToStageTextOnly();
        config.dialogueBox.label.setWidth(config.dialogueBox.labelConfig.w * 2);

        // If the worm isn't completely stabilized.
        if (config.segsDiedPerExp.get(expId - 1) > 0) {
            config.worm.kill();
        }
        // If the worm is saved.
        else {
            config.wormSkeleton = config.wormlvl5;
            config.wormlvl5.animationState.setAnimation(0, "reverse_grow", false);
            config.worm.destroyBox2dWorm();
        }
    }

    public void step(float deltaTime) {
        if (config.segsDiedPerExp.get(expId - 1) == 0 && config.wormSkeleton != null &&
                config.wormlvl5.animationState.getTracks().get(0).isComplete()) {
            config.stageBack.addActor(config.character);
            config.wormSkeleton = null;
        }

        // Darkening out.
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        config.shapeRenderer.setColor(config.screenR, config.screenG, config.screenB, darkOutCounter/darkOutLen);
        config.shapeRenderer.rect(-0.5f*config.w, -0.5f*config.h, 2*config.w, 2*config.h);
        config.shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);

        // Darken the screen.
        if (darken) {
            darkOutCounter += deltaTime;
            if (darkOutCounter >= darkOutLen) {
                darken = false;
                config.dialogueBox.addToStageTextOnly();
            }
        }
        else {
            config.boss.setVolume(0.2f);
            config.day.setVolume(0.1f);

            // Play the dialogue.
            if (!dialogueDone) {
                String trigger = config.dialogueBox.step(signature);

                if (trigger.equals("done")) {
                    config.dialogueBox.removeFromStage();
                    config.background.remove();
                    config.character.remove();
                    config.wormOne = null;
                    config.wormSkeleton = null;
                    config.worm.destroyBox2dWorm();
                    config.drawTouch = false;
                    config.drawInstabilities = false;
                    config.enableInputsNBoneUpdate = false;
                    dialogueDone = true;

                    logoActor = new SpineActor(config.spineActorConfigs.get("logo"), config.skeletonRenderer);
                    logoActor.animationState.setAnimation(0, "idle", true);
                    logoActor.animationState.apply(logoActor.skeleton);
                    config.stageBack.addActor(logoActor);
                }
            }
            // Wait for a bit before a sudden transition.
            else if (eventEndCountdown > 0) {
                eventEndCountdown -= deltaTime;
            }
            // Brighten up the screen again.
            else {
                if (config.boss.isPlaying()) {
                    config.day.stop();
                    config.boss.stop();
                    config.theme.setVolume(1);
                    config.theme.play();
                }

                darkOutCounter -= deltaTime;
                // The event is over.
                if (darkOutCounter < 0) {
                    active = false;
                    ended = true;
                    dispose();
                }
            }
        }
    }

    public void dispose() {
    }
}