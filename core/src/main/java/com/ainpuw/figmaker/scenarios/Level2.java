package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.Gdx;

public class Level2 extends Scenario {
    public Level2(Config config) {
        super(config);
        if (config.background.getStage() == null) config.stage.addActor(config.background);
        if (config.character.getStage() == null) config.stage.addActor(config.character);
        // if (config.menu.contents.getStage() == null) config.stage.addActor(config.menu.contents);

        events.add(new Event(config, "intro_dialogue") {
            public void init() {
                config.dialogueBox.dialogueCurrentLine = 0;
                config.dialogueBox.dialogueLines =
                        Gdx.files.internal("dialogues/level2_intro.txt").readString().split("\\r?\\n");
                config.dialogueBox.addToStage();
            }

            public void step(float deltaTime) {
                boolean finished = config.dialogueBox.step();
                if (finished) {
                    active = false;
                    ended = true;
                    dispose();
                }
            }

            public void dispose() {
                config.dialogueBox.removeFromStage();
            }
        });
    }

    public Scenario nextScenario() {
        return new Level3(config);
    }
}
