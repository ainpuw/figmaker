package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.utils.Array;

public abstract class Scenario {
    public Config config;
    public Array<Event> events;

    public int spawnAnimation; // Not int.
    public int wormCoords;  // Not int.

    public Scenario(Config config) {
        this.config = config;
        this.events = new Array<>();
    }

    public abstract Scenario nextScenario();

    public void step(float deltaTime) {
        // Remove finished events. TODO: Can this cause memory leak?
        for (int i = events.size - 1; i >= 0 ; i--) {
            if (events.get(i).ended) events.removeIndex(i);
        }
        // Make a step for all ongoing events.
        for (int i = 0; i < events.size; i++) {
            Event event = events.get(i);
            if (event.active) event.step(deltaTime);
        }
    }

    public boolean isOver() {
        return events.isEmpty();
    }

}
