package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class DialogueActor extends TypingLabel {
    private UIConfig.DialogueConfig config;

    public DialogueActor(UIConfig.DialogueConfig config, Skin skin) {
        super("", skin);
        this.config = config;
        this.setWrap(true);
        this.setWidth(config.w);
        this.setHeight(this.getPrefHeight());
        this.setPosition(config.x, config.y - this.getHeight());

        Pixmap labelColor = new Pixmap((int)config.w, (int)this.getPrefHeight(), Pixmap.Format.Alpha);
        labelColor.setColor(Color.alpha(200f));
        labelColor.fill();
        this.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
    }

    public void updateText(String text) {
        this.setText(text, true);
        this.restart();
        this.setHeight(this.getPrefHeight());
        this.setY(config.y - this.getHeight());
    }

    public void genRandomText() {
        // For debug purpose.
        if (Math.random() > 0.995) {
            String text = "";
            int words = (int) (Math.random() * 30);
            for (int i = 0; i < words; i++) {
                int letters = (int) (Math.random() * 10);
                for (int j = 0; j < letters; j++) {
                    text += (char) Math.max(60, (int) (Math.random() * 122));
                }
                text += " ";
            }
            this.updateText(text);
        }

    }
}
