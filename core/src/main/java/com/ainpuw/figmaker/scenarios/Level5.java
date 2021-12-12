package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level5 extends Scenario {
    Config config;

    public Level5(Config config) {
        super(config);
    }

    public Scenario NextScenario() {
        return new Ending(config);
    }
}
