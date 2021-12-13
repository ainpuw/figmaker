package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level2 extends Scenario {
    public Level2(Config config) {
        super(config);
    }

    public Scenario nextScenario() {
        return new Level3(config);
    }
}
