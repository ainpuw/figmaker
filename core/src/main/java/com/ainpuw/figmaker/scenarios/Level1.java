package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public class Level1 extends Scenario {
    public Level1(Config config) {
        super(config);
        /*
        uiConfig.stage.addActor(background);
        uiConfig.stage.addActor(character);
        uiConfig.stage.addActor(menu.contents);
        dialogueBox.addToStage();
         */
    }

    public Scenario nextScenario() {
        return new Level2(config);
    }
}
