package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.Event;
import com.ainpuw.figmaker.scenarios.events.LevelBeginEvent;
import com.ainpuw.figmaker.scenarios.events.LevelEndEvent;
import com.ainpuw.figmaker.scenarios.events.LevelListenEvent;
import com.ainpuw.figmaker.scenarios.events.LevelTransitionEvent;

public class Level1 extends Scenario {

    public Level1(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);

        // Set the "difficulty" for the level.
        config.segCtrToAnchorMargin = 20f;

        events.add(new LevelBeginEvent(config, "levelBeginEvent", 1));
    }

    public Scenario nextScenario() {
        return new Level2(config);
    }

    public void step(float deltaTime) {
        // TODO: This add new event mechanism isn't the best.
        // Since we know there will be only 1 concurrent event, we directly take index 0.
        Event currentEvent = events.get(0);
        if (currentEvent.ended && currentEvent.name.equals("levelBeginEvent")) {
            events.add(new LevelListenEvent(config, "levelListenEvent", 1));
        }
        else if (currentEvent.ended && currentEvent.name.equals("levelListenEvent")) {
            events.add(new LevelEndEvent(config, "levelEndEvent", 1));
        }
        else if (currentEvent.ended && currentEvent.name.equals("levelEndEvent")) {
            events.add(new LevelTransitionEvent(config, "levelTransitionEvent", 1));
        }


        super.step(deltaTime);
    }

}
