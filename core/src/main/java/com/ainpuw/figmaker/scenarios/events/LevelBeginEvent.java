package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.SpineActor;
import com.badlogic.gdx.Gdx;

public class LevelBeginEvent extends Event {
    private SpineActor wormlvl;
    private String dialogueFile;
    private boolean waitToAdvance = false;
    private String trigger = "";
    private boolean playReady = false;
    private boolean releaseReady = false;
    private boolean growAnimationFinished = false;

    public LevelBeginEvent(Config config, String name, int expId) {
        super(config, name, expId);
        this.dialogueFile = "dialogue/level" + expId + "_intro.txt";
        if (expId == 2)
            this.wormlvl = config.wormlvl2;
        else if (expId == 3)
            this.wormlvl = config.wormlvl3;
        else if (expId == 4)
            this.wormlvl = config.wormlvl4;
        else if (expId == 5)
            this.wormlvl = config.wormlvl5;
        else  // Default to use level 1.
            this.wormlvl = config.wormlvl1;

        config.dialogueBox.reset();
        config.dialogueBox.dialogueLines =
                Gdx.files.internal(dialogueFile).readString().split("\\r?\\n");
        config.dialogueBox.addToStage();

        // Skip the first frame that has the coordinates of the setup mode.
        wormlvl.animationState.apply(wormlvl.skeleton);

        // Reset this animation.
        config.wormhurt.animationState.setAnimation(0, "idle", true);
    }

    public void step(float deltaTime) {
        if (!waitToAdvance) trigger = config.dialogueBox.step(signature);
        growAnimationFinished = growAnimationFinished || wormlvl.animationState.getTracks().get(0).isComplete();
        config.drawWormSkeletonJointCounter = Math.min(1, config.drawWormSkeletonJointCounter + deltaTime);

        // For level 5 only.
        if (trigger.equals("pain")) {
            config.dialogueBox.portrait.remove();
        }
        if (trigger.equals("recover")) {
            config.dialogueBox.addToStage();
        }
        if (trigger.equals("showDSeg")) {
            config.wormOne = config.wormhurt;
        }
        if (trigger.equals("showNSeg")) {
            releaseReady = true;
            config.dialogueBox.removeFromStage();
            if (expId == 5) {
                config.amanager.setRoutine2();
            }
            else {
                config.wormhurt.animationState.setAnimation(0, "release", false);
            }
        }
        if (releaseReady) {
            if (expId != 5 && config.wormhurt.animationState.getTracks().get(0).isComplete()) {
                config.wormOne = config.wormseg;
                releaseReady = false;
                config.dialogueBox.addToStage();
            }
            else if (expId == 5 && config.character.animationState.getTracks().get(1).isComplete()) {
                releaseReady = false;
                config.dialogueBox.addToStage();
            }
        }
        if (trigger.equals("playGrow")) {
            trigger = "";
            config.wormOne = null;
            config.wormSkeleton = wormlvl;
            config.drawWormSkeletonJointCounter = 0;
            // Special treatment for level 5.
            if (expId == 5) {
                config.character.remove();
                config.dialogueBox.portrait.remove();
                // config.day.stop();  do not stop the forest sound.
                config.boss.play();
            }

            playReady = true;
            waitToAdvance = true;
            config.dialogueBox.removeFromStage();
        }
        if (trigger.equals("genB2DWorm")) {
            // Deprecated flag.
        }
        if (growAnimationFinished && config.wormSkeleton != null) {
            float noPeriods = wormlvl.animationState.getTracks().get(0).getAnimationTime()/wormlvl.animationState.getTracks().get(0).getAnimation().getDuration();
            if (!wormlvl.animationState.getTracks().get(0).getAnimation().getName().equals("idle")) {
                wormlvl.animationState.setAnimation(0, "idle", true);
            }
            else if (Math.abs(noPeriods - Math.round(noPeriods)) < 0.05 && noPeriods >= 0.9f && playReady) {
                config.wormSkeleton = null;
                config.worm.createBox2dWorm(wormlvl.skeleton.getRootBone());
                config.evolveWorld = true;
                waitToAdvance = false;
                config.dialogueBox.addToStage();
                if (expId == 5) {
                    config.dialogueBox.portrait.remove();
                }
            }
        }
        if (trigger.equals("showInsta")) {
            config.drawInstabilities = true;
        }
        if (trigger.equals("allowInput")) {
            config.enableInputsNBoneUpdate = true;
            config.drawTouch = true;
        }
        if (trigger.equals("done")) {
            config.dialogueBox.removeFromStage();
            config.wormOne = null;
            config.wormSkeleton = null;
            active = false;
            ended = true;
            dispose();
        }
    }

    public void dispose() {}
}