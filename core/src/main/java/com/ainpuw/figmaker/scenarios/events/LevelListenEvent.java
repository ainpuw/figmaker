package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.WormSegment;

public class LevelListenEvent extends Event {
    public int noSegDied = 0;

    public LevelListenEvent(Config config, String name) {
        super(config, name);
        init();
    }

    public void init() {}

    public void step(float deltaTime) {
        noSegDied = 0;
        boolean wormEventFinished = true;
        boolean wormFallsIntoPlace = true;
        for (WormSegment seg : config.worm.segs) {
            if (seg.isDead()) noSegDied += 1;

            boolean visuallyBroken = seg.parentBoneVisuallyBroken();
            boolean strictBroken = seg.parentBoneStrictBroken();
            if (seg.parent != null &&
                ((visuallyBroken && !seg.isDead() && !seg.parent.isDead()) ||
                 (!seg.isStable() && !seg.isStable()))) {
                wormEventFinished = false;
                wormFallsIntoPlace = false;
            }
            else if (seg.parent != null &&
                     ((strictBroken && !seg.isDead() && !seg.parent.isDead()) ||
                     (!seg.isStable() && !seg.isStable()))) {
                wormFallsIntoPlace = false;
            }
        }

        if (wormEventFinished) {
            config.enableInputsNBoneUpdate = false;
            config.drawTouch = false;
        }
        if (wormFallsIntoPlace) {
            active = false;
            ended = true;
            dispose();
        }
    }

    public void dispose() {}
}