package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.Gdx;

public class LevelEndEvent extends Event {
    private String dialogueFile;

    public LevelEndEvent(Config config, String name, int expId) {
        super(config, name, expId);
        if (expId == 2) {
            this.dialogueFile = "dialogue/level2_end.txt";
        }
        else if (expId == 3) {
            this.dialogueFile = "dialogue/level3_end.txt";
        }
        else if (expId == 4) {
            this.dialogueFile = "dialogue/level4_end.txt";
        }
        else if (expId == 5) {
            this.dialogueFile = "dialogue/level5_end.txt";
        }
        else {  // Default to use level 1.
            this.dialogueFile = "dialogue/level1_end.txt";
        }

        config.dialogueBox.reset();
        config.dialogueBox.dialogueLines =
                Gdx.files.internal(dialogueFile).readString().split("\\r?\\n");
        config.dialogueBox.addToStage();

        if (config.segsDiedPerExp.get(expId - 1) > 0)
            config.worm.kill();
    }

    public void step(float deltaTime) {
        String trigger = config.dialogueBox.step(signature);
        if (trigger.equals("done")) {
            config.dialogueBox.removeFromStage();
            active = false;
            ended = true;
            dispose();
        }
    }

    public void dispose() {
        config.wormOne = null;
        config.wormSkeleton = null;
        config.worm.destroyBox2dWorm();
    }
}