package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.Event;
import com.badlogic.gdx.Gdx;

public class Level2 extends Scenario {
    public Level2(Config config) {
        super(config);
        config.character.animationState.setAnimation(0, "idle", true);
        config.background.animationState.setAnimation(0, "idle", true);
        if (config.background.getStage() == null) config.stageBack.addActor(config.background);
        if (config.character.getStage() == null) config.stageBack.addActor(config.character);
        config.wormOne = config.wormhurt;

    }

    public Scenario nextScenario() {
        return new Level3(config);
    }
}
