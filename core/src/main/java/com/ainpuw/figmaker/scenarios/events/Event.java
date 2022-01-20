package com.ainpuw.figmaker.scenarios.events;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public abstract class Event {
    public Config config;
    public String name;
    public int expId;
    public String expIDStr;
    public String signature;
    public boolean active = true;
    public boolean ended = false;

    public Event() {}

    public Event(Config config, String name, int expId) {
        this.config = config;
        this.name = name;
        this.expId = expId;
        this.expIDStr = Integer.toString(expId);
        this.signature = getSignature();
    }

    public String getSignature() {
        String sig = "SIG" + expIDStr;
        int totalFailed = 0;
        for (int i : config.segsDiedPerExp) {
            if (i > 0) totalFailed += 1;
        }

        int currentFailed = 0;
        if (config.segsDiedPerExp.get(expId - 1) > 0)
            currentFailed = 1;

        sig = sig + totalFailed + currentFailed;
        return sig;
    }

    public abstract void step(float deltaTime);

    public abstract void dispose();

}