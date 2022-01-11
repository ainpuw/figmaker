package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.Gdx;

public class LevelEndEvent extends Event {
    private String dialogueFile;

    public LevelEndEvent(Config config, String name, String dialogueFile) {
        super(config, name);
        this.dialogueFile = dialogueFile;
        init();
    }

    public void init() {
        config.dialogueBox.reset();
        config.dialogueBox.dialogueLines =
                Gdx.files.internal(dialogueFile).readString().split("\\r?\\n");
        config.dialogueBox.addToStage();
    }

    public void step(float deltaTime) {
        String trigger = config.dialogueBox.step();
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