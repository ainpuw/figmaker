package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level3 extends Scenario {
    Config config;

    public Level3(Config config) {
        super(config);
    }

    public Scenario NextScenario() {
        return new Level4(config);
    }
}
