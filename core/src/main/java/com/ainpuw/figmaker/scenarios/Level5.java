package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level5 extends Scenario {
    public Level5(Config config) {
        super(config);
    }

    public Scenario nextScenario() {
        return new Ending(config);
    }
}
