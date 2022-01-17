package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.WormSegment;

public class LevelListenEvent extends Event {
    private float eventEndCountdown = 3;  // Can be put into config.

    public LevelListenEvent(Config config, String name, int expId) {
        super(config, name, expId);
        config.totalSegsPerExp.set(expId - 1, config.worm.segs.size);
    }

    public void step(float deltaTime) {
        int noSegDied = 0;
        boolean wormEventFinished = true;
        for (WormSegment seg : config.worm.segs) {
            if (seg.isDead()) noSegDied += 1;
            if (!((seg.segReachedPosition() && seg.isStable()) || seg.isDead())) wormEventFinished = false;
        }

        if (wormEventFinished) {
            eventEndCountdown -= deltaTime;

            config.segsDiedPerExp.set(expId - 1, noSegDied);
            config.enableInputsNBoneUpdate = false;
            config.drawTouch = false;
            config.drawInstabilities = false;

            // Done with the smooth transition.
            if (eventEndCountdown < 0) {
                active = false;
                ended = true;
                dispose();
            }
        }
    }

    public void dispose() {}
}