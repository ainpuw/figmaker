package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Ending extends Scenario {
    Config config;

    public Ending(Config config) {
        super(config);
    }

    public Scenario NextScenario() {
        return new Intro(config);
    }
}
