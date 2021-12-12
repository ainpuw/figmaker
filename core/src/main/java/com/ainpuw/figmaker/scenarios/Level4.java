package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level4 extends Scenario {
    Config config;

    public Level4(Config config) {
        super(config);
    }

    public Scenario NextScenario() {
        return new Level5(config);
    }
}
