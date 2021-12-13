package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level1 extends Scenario {
    public Level1(Config config) {
        super(config);
    }

    public Scenario nextScenario() {
        return new Level2(config);
    }
}
