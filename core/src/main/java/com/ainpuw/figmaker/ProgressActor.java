package com.ainpuw.figmaker;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ProgressActor extends ProgressBar {
    UIConfig.ProgressConfig config;

    public ProgressActor(UIConfig.ProgressConfig config, Skin skin) {
        super(config.min, config.max, config.stepSize, config.vertical, skin);
        this.config = config;
        this.setValue(config.startValue);
        this.setPosition(config.x, config.y);
        this.setWidth(config.w);  // Not working?
        this.setHeight(config.h);  // Now working?
    }

    public void genRandomProgress() {
        // For debug purpose.
        float newValue = (float) Math.random() + this.getValue();
        if (newValue > this.getMaxValue())
            this.setValue(0);
        else
            this.setValue(newValue);
    }
}
