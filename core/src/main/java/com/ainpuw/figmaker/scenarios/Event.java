package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;

public abstract class Event {
    public Config config;
    public String name;
    public boolean active = true;
    public boolean ended = false;

    public Event(Config config, String name) {
        this.config = config;
        this.name = name;
        init();
    }

    public abstract void init();

    public abstract void step(float deltaTime);

    public abstract void dispose();

}
