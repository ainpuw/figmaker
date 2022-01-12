package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LevelTransitionEvent  extends Event {
    private final float darkOutLen = 2;  // In seconds. This could be put in the config.
    private float darkOutCounter = 0;
    private boolean darken = true;
    private boolean dialogueDone = false;
    private String dialogueFile;

    public LevelTransitionEvent(Config config, String name, int expId) {
        super(config, name, expId);
        this.dialogueFile = "dialogue/level" + expId + "_transition.txt";
        config.dialogueBox.reset();
        config.dialogueBox.dialogueLines =
                Gdx.files.internal(dialogueFile).readString().split("\\r?\\n");
    }

    public void step(float deltaTime) {
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        config.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        config.shapeRenderer.setColor(0, 0, 0, darkOutCounter/darkOutLen);
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
            // Play the dialogue.
            if (!dialogueDone) {
                String trigger = config.dialogueBox.step(signature);
                if (trigger.equals("done")) {
                    config.dialogueBox.removeFromStage();
                    config.wormOne = null;
                    config.wormSkeleton = null;
                    config.worm.destroyBox2dWorm();
                    config.drawTouch = false;
                    config.drawInstabilities = false;
                    config.enableInputsNBoneUpdate = false;
                    dialogueDone = true;
                }
            }
            // Brighten up the screen again.
            else {
                darkOutCounter -= deltaTime;
                // The event is over.
                if (darkOutCounter < 0) {

                    active = false;
                    ended = true;
                }
            }

        }

    }

    public void dispose() {}

}
