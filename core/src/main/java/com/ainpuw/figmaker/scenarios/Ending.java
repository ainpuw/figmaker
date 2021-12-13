package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Ending extends Scenario {
    public Ending(Config config) {
        super(config);
    }

    public Scenario nextScenario() {
        // return new Intro(config);
        return null;
    }
}
