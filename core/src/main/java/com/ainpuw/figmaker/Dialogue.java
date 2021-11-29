package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class Dialogue {
    private UIConfig config;
    private UIConfig.DialogueConfig labelConfig;
    public TypingLabel label;
    public Image background;
    public SpineActor portrait;

    public Dialogue(UIConfig config) {
        this.config = config;
        this.labelConfig = config.dialogueActorConfigs.get("dialogueLabel");

        // Label.
        this.label = new TypingLabel("", config.skin);
        this.label.setFontScale(1.7f);
        this.label.setWrap(true);
        this.label.setWidth(this.labelConfig.w);
        this.label.setHeight(this.label.getPrefHeight());
        this.label.setPosition(config.dialogueOffset.x + labelConfig.x,
                config.dialogueOffset.y + labelConfig.y - label.getHeight());

        // Background.
        background = new Image(config.dialogueBackgroundTexture);
        background.setSize(background.getWidth() * config.dialogueScale, background.getHeight() * config.dialogueScale);
        background.setPosition(config.dialogueOffset.x, config.dialogueOffset.y);

        // Portrait.
        portrait = new SpineActor(config.spineActorConfigs.get("portrait"));
        portrait.skeleton.setScale(config.dialogueScale, config.dialogueScale);
        portrait.setPosition(config.dialogueOffset.x + 391, config.dialogueOffset.y + 231);

        // FIXME: Refactor.
        portrait.animationState.setAnimation(1, "blink", true);
        portrait.animationState.setAnimation(2, "armeye", true);
        updateText("{COLOR=GREEN}Hello,{WAIT} world! Interesting specimen... it duplicates at astonishing speed.");
        //updateText("i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i i ");

    }

    public void addToStage() {
        config.stage.addActor(background);
        config.stage.addActor(portrait);
        config.stage.addActor(label);
    }

    public void updateText(String text) {
        label.setText(text);
        label.restart();
        label.setHeight(label.getPrefHeight());
        label.setY(config.dialogueOffset.y + labelConfig.y - label.getHeight());
    }

    public void genRandomTextDebug() {
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
