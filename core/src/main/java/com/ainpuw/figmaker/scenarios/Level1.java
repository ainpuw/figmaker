package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;

public class Level1 extends Scenario {
    SpineActor wormlvl;
    boolean playReady = false;
    boolean growAnimationFinished = false;

    public Level1(Config config) {
        super(config);
        wormlvl = config.wormlvl2;
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);

        events.add(new Event(config, "intro_dialogue") {
            public void init() {
                config.dialogueBox.reset();
                config.dialogueBox.dialogueLines =
                        Gdx.files.internal("dialogue/level1_intro.txt").readString().split("\\r?\\n");
                config.dialogueBox.addToStage();

                // Skip the first frame that has the coordinates of the setup mode.
                wormlvl.animationState.update(0.1f);
                wormlvl.animationState.apply(wormlvl.skeleton);
            }

            public void step(float deltaTime) {
                String trigger = config.dialogueBox.step();
                growAnimationFinished = growAnimationFinished || wormlvl.animationState.getTracks().get(0).isComplete();
                boolean wormSetupFinished = true;

                if (trigger.equals("showDSeg")) {
                    config.wormOne = config.wormhurt;
                }
                if (trigger.equals("showNSeg")) {
                    config.wormOne = config.wormseg;
                }
                if (trigger.equals("playGrow")) {
                    config.wormOne = null;
                    config.wormSkeleton = wormlvl;
                }
                if (trigger.equals("genB2DWorm")) {
                    playReady = true;
                }
                if (growAnimationFinished && config.wormSkeleton != null) {
                    float noPeriods = wormlvl.animationState.getTracks().get(0).getAnimationTime()/wormlvl.animationState.getTracks().get(0).getAnimation().getDuration();
                    if (!wormlvl.animationState.getTracks().get(0).getAnimation().getName().equals("idle")) {
                        wormlvl.animationState.setAnimation(0, "idle", true);
                    }
                    else if (Math.abs(noPeriods - (int)noPeriods) < 0.05 && playReady) {
                        config.wormSkeleton = null;
                        config.worm.createBox2dWorm(wormlvl.skeleton.getRootBone());
                        config.evolveWorld = true;
                    }
                }
                if (trigger.equals("done")) {
                    config.dialogueBox.removeFromStage();
                }
                if (trigger.equals("done1") && wormSetupFinished) {
                    config.worm.destroyBox2dWorm();
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
        return new Level2(config);
    }
}
