package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.Gdx;

public class LevelEndEvent extends Event {
    private String dialogueFile;

    public LevelEndEvent(Config config, String name, int expId) {
        super(config, name, expId);
        this.dialogueFile = "dialogue/level" + expId + "_end.txt";

        config.dialogueBox.reset();
        config.dialogueBox.dialogueLines =
                Gdx.files.internal(dialogueFile).readString().split("\\r?\\n");
        config.dialogueBox.addToStage();

        // If the worm isn't completely stabilized.
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
    }
}