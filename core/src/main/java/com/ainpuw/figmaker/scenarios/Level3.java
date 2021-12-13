package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level3 extends Scenario {
    public Level3(Config config) {
        super(config);
    }

    public Scenario nextScenario() {
        return new Level4(config);
    }
}
