package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;

public class Toolbox extends HorizontalGroup {
    GameConfig gameConfig;
    UIConfig uiConfig;
    GameState state;
    DialogueActor dialoguebox;
    Array<Tool> tools;
    int isReady = 0;

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

        tools = new Array<>();
        for (int segID = 0; segID < gameConfig.wormSegConfigs.size(); segID++) {
            Texture segTexture = new Texture(gameConfig.wormSegConfigs.get(segID).imgPath);
            Tool segTool = new Tool(segTexture);
            segTool.addListener(new ToolboxListener(gameConfig, uiConfig, state, this, dialogueBox, segTool, segID));
            tools.add(segTool);
            this.addActor(segTool.displayActor);
            // Tool is added directly to stage, its displayActor is added into Toolbox,
            // which in turn is added to stage.
            uiConfig.stage.addActor(segTool);
        }

        // Initialize sources for drag and drop.
        uiConfig.toolboxDrag.clear();
        for (Tool tool : tools) {
            uiConfig.toolboxDrag.addSource(new DragAndDrop.Source(tool) {
                @Null
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject("my payload");
                    payload.setDragActor(getActor());

                    Label validLabel = new Label("connect", uiConfig.skin);
                    validLabel.setColor(0, 1, 0, 1);
                    payload.setValidDragActor(validLabel);

                    return payload;
                }

                @Null
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    ready(true);
                    // Resume stepping Box2D when the building action ends.
                    state.doBox2DStep = true;
                    // Remove worm actors from stage to reduce computation.
                    for (WormSegment seg : state.wormSegs) {
                        for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                            basicSeg.removeFromStage();
                        }
                    }
                }
            });
        }
    }

    public void ready(boolean force) {
        // LibGDX doesn't seem to initialize everything very fast.
        // The frame rate is 60, so allow 0.5 seconds (30 frames) for this update to kick in.
        if (isReady <= 30 || force) {
            for (Tool tool : tools) {
                tool.toFront();
                tool.alignDisplayAndDrag();
            }
            isReady = Math.min(isReady + 1, 60);
        }
    }

    ////////////////////////////////////////////////////
    // Tool subclass definition
    ////////////////////////////////////////////////////

    public class Tool extends Image {
        // The class instance itself is the actor being dragged around.
        // displayActor is the one added to the HorizontalGroup for display purposes.
        public Image displayActor;

        public Tool(Texture texture) {
            super(texture);
            this.displayActor = new Image(texture);
            this.displayActor.setBounds(0, 0, texture.getWidth(), texture.getHeight());
        }

        public void alignDisplayAndDrag() {
            Vector2 actorCoords = displayActor.localToScreenCoordinates(new Vector2(0, 0));
            actorCoords.y = uiConfig.stage.getHeight() - actorCoords.y;
            this.setPosition(actorCoords.x, actorCoords.y);
        }
    }

}
