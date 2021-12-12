package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level2 extends Scenario {
    Config config;

    public Level2(Config config) {
        super(config);
    }

    public Scenario NextScenario() {
        return new Level3(config);
    }
}
