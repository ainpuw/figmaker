package com.ainpuw.figmaker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

public class Toolbox extends HorizontalGroup {
    GameConfig gameConfig;
    UIConfig uiConfig;
    GameState state;
    DialogueActor dialoguebox;

    public Toolbox(UIConfig uiConfig, GameConfig gameConfig,
                   GameState state, DialogueActor dialogueBox) {
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;
        this.state = state;
        this.dialoguebox = dialogueBox;

        this.setPosition(uiConfig.toolboxX, uiConfig.toolboxY);
        this.setSize(uiConfig.toolboxW, uiConfig.toolboxH);
        this.wrap(true);
        this.rowAlign(Align.left);
        this.space(uiConfig.toolboxSpacing);
        this.wrapSpace(uiConfig.toolboxSpacing);

        for (int segID = 0; segID < gameConfig.wormSegConfigs.size(); segID++) {
            Texture segTexture = new Texture(gameConfig.wormSegConfigs.get(segID).imgPath);
            Image segActor = new Tool(segTexture);
            segActor.addListener(new ToolboxListener(gameConfig, uiConfig, state, dialogueBox, segActor, segID));
            this.addActor(segActor);
        }

        // Initialize drag and drop.
        uiConfig.toolboxDrag.clear();
        for (Actor actor : this.getChildren()) {
            uiConfig.toolboxDrag.addSource(new DragAndDrop.Source(actor) {
                @Null
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject("Some payload!");

                    payload.setDragActor(getActor());

                    Label validLabel = new Label("connect", uiConfig.skin);
                    validLabel.setColor(0, 1, 0, 1);
                    payload.setValidDragActor(validLabel);

                    return payload;
                }
            });
        }
    }

    ////////////////////////////////////////////////////
    // Tool subclass definition
    ////////////////////////////////////////////////////

    public class Tool  extends Image {
        Image dragActor;

        public Tool(Texture texture) {
            super(texture);
            dragActor = new Image(texture);
            dragActor.setBounds(0, 0, texture.getWidth(), texture.getHeight());
        }

        public void alignDisplayAndDrag() {
            dragActor.setPosition(this.getX(), this.getY());
        }
    }

}
