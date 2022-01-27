package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.Event;
import com.ainpuw.figmaker.scenarios.events.LevelBeginEvent;
import com.ainpuw.figmaker.scenarios.events.EndingEvent;
import com.ainpuw.figmaker.scenarios.events.LevelListenEvent;

public class Level5 extends Scenario {

    public Level5(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);

        // Set character location for level 5.
        config.character.skeleton.setScale(1.9f, 1.9f);
        config.character.setPosition(110, 395);

        // Set the "difficulty" for the level.
        config.segCtrToAnchorMargin = 30f;
        config.boneStabilizationTimeCurrent = 10f;
        config.boneStabilizationTimeMin = 10f;

        events.add(new LevelBeginEvent(config, "levelBeginEvent", 5));
    }

    public Scenario nextScenario() {
        return null;
    }

    public void step(float deltaTime) {
        // Since we know there will be only 1 concurrent event, we directly take index 0.
        Event currentEvent = events.get(0);
        if (currentEvent.ended && currentEvent.name.equals("levelBeginEvent")) {
            events.add(new LevelListenEvent(config, "levelListenEvent", 5));
        }
        else if (currentEvent.ended && currentEvent.name.equals("levelListenEvent")) {
            events.add(new EndingEvent(config, "ending", 5));
        }

        super.step(deltaTime);
    }

}
