package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.Gdx;

public class Level2 extends Scenario {
    public Level2(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);
        config.wormOne = config.wormhurt;

        events.add(new Event(config, "intro_dialogue") {
            public void init() {
                config.dialogueBox.reset();
                config.dialogueBox.dialogueLines =
                        Gdx.files.internal("dialogue/level1_intro.txt").readString().split("\\r?\\n");
                config.dialogueBox.addToStage();

                // Skip the first frame that has the coordinates of the setup mode.
                config.wormlvl2.animationState.update(0.1f);
                config.wormlvl2.animationState.apply(config.wormlvl2.skeleton);
            }

            public void step(float deltaTime) {
                String trigger = config.dialogueBox.step();
                boolean wormAnimationFinished = config.wormlvl2.animationState.getTracks().get(0).isComplete();
                boolean wormSetupFinished = true;

                if (trigger.equals("1")) {
                    config.wormOne = config.wormseg;
                }
                else if (trigger.equals("2")) {
                    config.wormOne = null;
                    config.wormSkeleton = config.wormlvl2;
                }
                else if (wormAnimationFinished && config.wormSkeleton != null) {
                    config.wormSkeleton = null;
                    config.worm.createBox2dWorm(config.wormlvl2.skeleton.getRootBone());
                    config.evolveWorld = true;
                }
                else if (trigger.equals("done") && wormSetupFinished) {
                    active = false;
                    ended = true;
                    dispose();
                }
            }

            public void dispose() {
                config.wormOne = null;
                config.dialogueBox.removeFromStage();
            }
        });
    }

    public Scenario nextScenario() {
        return new Level3(config);
    }
}
