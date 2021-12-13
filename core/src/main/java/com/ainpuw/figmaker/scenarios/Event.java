package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public abstract class Event {
    public Config config;
    public String name;
    public boolean active = true;
    public boolean ended = false;
    public Array<FileHandle> files;

    public Event(Config config, String name) {
        this.config = config;
        this.name = name;
        files = new Array<>();
        init();
    }

    public abstract void init();

    public abstract void step(float deltaTime);

    public abstract void dispose();

}
