package com.ainpuw.figmaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class Dialogue {
    private Config config;
    private Config.DialogueConfig labelConfig;
    public TypingLabel label;
    public Image background;
    public SpineActor portrait;
    public int dialogueCurrentLine = 0;
    public String[] dialogueLines = null;
    private final String endMarker = "{COLOR=#999999}{WAVE=0.05;-1}_{ENDWAVE}";

    public Dialogue(Config config) {
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
        portrait = new SpineActor(config.spineActorConfigs.get("portrait"), config.skeletonRenderer);
        portrait.skeleton.setScale(config.dialogueScale, config.dialogueScale);
        portrait.setPosition(config.dialogueOffset.x + 392, config.dialogueOffset.y + 230);

        // Default animation.
        portrait.animationState.setAnimation(1, "blink", true);
        portrait.animationState.setAnimation(2, "armeye", true);
    }

    public String step() {
        if (dialogueLines == null) return "done";

        if ((Gdx.input.justTouched() && label.hasEnded()) || label.textEquals("")) {
            if (dialogueCurrentLine >= dialogueLines.length) return "done";

            String lineSplit[] = dialogueLines[dialogueCurrentLine].split("###");
            String currentCharacter = lineSplit[0];  // TODO: Utilize this attribute.
            String emote = lineSplit[1];  // TODO: Utilize this attribute.
            String trigger = lineSplit[2];
            String words = lineSplit[3];

            updateText(words + endMarker);
            dialogueCurrentLine++;

            return trigger;
        }
        else if (Gdx.input.justTouched()) {
            label.skipToTheEnd();
        }

        return "";
    }

    public void addToStage() {
        config.stage.addActor(background);
        config.stage.addActor(portrait);
        config.stage.addActor(label);
    }

    public void removeFromStage() {
        background.remove();
        portrait.remove();
        label.remove();
    }

    public void updateText(String text) {
        label.setText(text);
        label.restart();
        label.setHeight(label.getPrefHeight());
        label.setY(config.dialogueOffset.y + labelConfig.y - label.getHeight());
    }

    public void reset() {
        dialogueCurrentLine = 0;
    }
}
