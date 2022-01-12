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
        wormlvl.animationState.update(0.1f);
        wormlvl.animationState.apply(wormlvl.skeleton);
    }

    public void step(float deltaTime) {
        if (!waitToAdvance) trigger = config.dialogueBox.step(signature);
        growAnimationFinished = growAnimationFinished || wormlvl.animationState.getTracks().get(0).isComplete();

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
            waitToAdvance = true;
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
                waitToAdvance = false;
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