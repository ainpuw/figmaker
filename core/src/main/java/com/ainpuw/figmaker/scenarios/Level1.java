package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.LevelBeginEvent;

public class Level1 extends Scenario {

    public Level1(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);

        events.add(new LevelBeginEvent(config, "intro_dialogue", config.wormlvl1));
    }

    public Scenario nextScenario() {
        return new Level2(config);
    }
}
