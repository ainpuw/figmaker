package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.Event;
import com.ainpuw.figmaker.scenarios.events.LevelBeginEvent;
import com.ainpuw.figmaker.scenarios.events.LevelEndEvent;
import com.ainpuw.figmaker.scenarios.events.LevelListenEvent;
import com.ainpuw.figmaker.scenarios.events.LevelTransitionEvent;

public class Level2 extends Scenario {

    public Level2(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);

        events.add(new LevelBeginEvent(config, "levelBeginEvent", 2));
    }

    public Scenario nextScenario() {
        return new Level3(config);
    }

    public void step(float deltaTime) {
        // Since we know there will be only 1 concurrent event, we directly take index 0.
        Event currentEvent = events.get(0);
        if (currentEvent.ended && currentEvent.name.equals("levelBeginEvent")) {
            events.add(new LevelListenEvent(config, "levelListenEvent", 2));
        }
        else if (currentEvent.ended && currentEvent.name.equals("levelListenEvent")) {
            events.add(new LevelEndEvent(config, "levelEndEvent", 2));
        }
        else if (currentEvent.ended && currentEvent.name.equals("levelEndEvent")) {
            events.add(new LevelTransitionEvent(config, "levelTransitionEvent", 2));
        }


        super.step(deltaTime);
    }

}
