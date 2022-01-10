package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.ainpuw.figmaker.scenarios.events.IntroEvent;

public class Intro extends Scenario {
    public Intro(Config config) {
        super(config);
        config.stageBack.clear();

        events.add(new IntroEvent(config, "intro_animation"));
    }

    public Scenario nextScenario() {
        return new Level1(config);
    }
}
