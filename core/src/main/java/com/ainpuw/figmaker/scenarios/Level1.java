package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.Event;
import com.ainpuw.figmaker.scenarios.events.LevelBeginEvent;
import com.ainpuw.figmaker.scenarios.events.LevelEndEvent;
import com.ainpuw.figmaker.scenarios.events.LevelListenEvent;

public class Level1 extends Scenario {

    public Level1(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);

        events.add(new LevelBeginEvent(config, "levelBeginEvent",
                "dialogue/level1_intro.txt", config.wormlvl1));
    }

    public Scenario nextScenario() {
        return new Level2(config);
    }

    public void step(float deltaTime) {
        // TODO: This add new event mechanism isn't the best.
        for (int i = events.size - 1; i >= 0 ; i--) {
            Event eventi = events.get(i);
            if (eventi.ended && eventi.name.equals("levelBeginEvent")) {
                events.add(new LevelListenEvent(config, "levelListenEvent"));
            }
            else if (eventi.ended && eventi.name.equals("levelListenEvent")) {
                events.add(new LevelEndEvent(config, "levelEndEvent",
                        "dialogue/level1_intro.txt"));
            }
        }

        super.step(deltaTime);
    }

}
