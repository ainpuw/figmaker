package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Intro extends Scenario {
    public Intro(Config config) {
        super(config);
    }

    public Scenario NextScenario() {
        return new Level1(config);
    }
}
