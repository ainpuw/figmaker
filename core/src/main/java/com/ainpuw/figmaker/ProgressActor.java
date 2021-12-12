package com.ainpuw.figmaker;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ProgressActor extends ProgressBar {
    Config.ProgressConfig config;

    public ProgressActor(Config.ProgressConfig config, Skin skin) {
        super(config.min, config.max, config.stepSize, config.vertical, skin);
        this.config = config;
        this.setValue(config.startValue);
        this.setPosition(config.x, config.y);
        this.setWidth(config.w);  // FIXME: Not working?
        this.setHeight(config.h);  // FIXME: Now working?
    }

    public void genRandomProgressDebug() {
        float newValue = (float) Math.random() + this.getValue();
        if (newValue > this.getMaxValue())
            this.setValue(0);
        else
            this.setValue(newValue);
    }
}
