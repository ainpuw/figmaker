package com.ainpuw.figmaker;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ToolboxListener extends InputListener {
    GameConfig gameConfig;
    DialogueActor dialogueBox;
    int segID;

    public ToolboxListener(GameConfig gameConfig, DialogueActor dialogueBox, int segID) {
        super();
        this.gameConfig = gameConfig;
        this.dialogueBox = dialogueBox;
        this.segID = segID;
    }

    @Override
    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        // FIXME: This is for debug purpose.
        dialogueBox.updateText(gameConfig.wormSegs.get(segID).name);
        return true;
    }
}
