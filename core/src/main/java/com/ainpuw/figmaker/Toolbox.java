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

        uiConfig.toolboxDrag.setDragActorPosition(uiConfig.getDragAndDropActorPositionX, uiConfig.getDragAndDropActorPositionY);
        uiConfig.toolboxDrag.setTouchOffset(uiConfig.dragAndDropTouchOffsetX, uiConfig.dragAndDropTouchOffsetY);

        tools = new Array<>();
        for (int segID = 0; segID < gameConfig.wormSegConfigs.size(); segID++) {
            Texture segTexture = new Texture(gameConfig.wormSegConfigs.get(segID).imgPath);
            Tool segTool = new Tool(segID, segTexture);
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
                    Tool toolActor = (Tool) getActor();
                    state.dragAndDrogSourceName = gameConfig.wormSegConfigs.get(toolActor.segID).name;

                            // Stop stepping Box2D when in building mode.
                    state.doBox2DStep = false;
                    // Add worm actors to stage for visualization.
                    for (WormSegment seg : state.wormSegs) {
                        for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                            basicSeg.updateAndAddToStage();
                        }
                    }

                    // Set the displayActor half alpha.
                    toolActor.displayActor.getColor().a = 0.5f;

                    addTargetsToToolboxDragAndDrop();

                    // FIXME: This is for debug purpose.
                    dialogueBox.updateText(state.dragAndDrogSourceName);

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject("my payload");
                    payload.setDragActor(getActor());

                    // Label validLabel = new Label("connect", uiConfig.skin);
                    // validLabel.setColor(0, 1, 0, 1);
                    // payload.setValidDragActor(validLabel);

                    return payload;
                }

                @Null
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    Tool toolActor = (Tool) getActor();

                    ready(true);
                    // Resume stepping Box2D when the building action ends.
                    state.doBox2DStep = true;
                    // Remove worm actors from stage to reduce computation.
                    for (WormSegment seg : state.wormSegs) {
                        for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                            basicSeg.removeFromStage();
                        }
                    }
                    // Set the displayActor back to full alpha.
                    toolActor.displayActor.getColor().a = 1f;
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

    private void addTargetsToToolboxDragAndDrop() {
        for (WormSegment seg : state.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                Array<WormSegment.BasicImgSegment> temp = new Array<>();
                temp.add(basicSeg.leftEnd);
                temp.add(basicSeg.rightEnd);
                for (WormSegment.BasicImgSegment imgActor : temp) {
                    uiConfig.toolboxDrag.addTarget(new DragAndDrop.Target(imgActor) {
                        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            WormSegment.BasicImgSegment imgActor = (WormSegment.BasicImgSegment) getActor();
                            imgActor.getColor().a = 1f;
                            state.touchingSeg = imgActor;
                            return true;
                        }

                        public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            WormSegment.BasicImgSegment imgActor = (WormSegment.BasicImgSegment) getActor();
                            imgActor.getColor().a = 0f;
                            state.touchingSeg = null;
                        }

                        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            if (state.touchingSeg != null) {
                                // Create a WormSegment and join it to the touched BasicSegment.
                                WormSegment.BasicImgSegment imgActor = (WormSegment.BasicImgSegment) getActor();
                                WormSegment newSeg = new WormSegment(gameConfig, uiConfig, state.dragAndDrogSourceName, imgActor.getX(), imgActor.getY());
                                state.wormSegs.add(newSeg);
                                Worm.joinSegs(newSeg, state.touchingSeg.parent, imgActor.isLeft);

                                state.dragAndDrogSourceName = "";
                                state.touchingSeg = null;
                                System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
                            }
                        }
                    });
                }
            }
        }
    }

    ////////////////////////////////////////////////////
    // Tool subclass definition
    ////////////////////////////////////////////////////

    public class Tool extends Image {
        int segID;
        // The class instance itself is the actor being dragged around.
        // displayActor is the one added to the HorizontalGroup for display purposes.
        public Image displayActor;

        public Tool(int segID, Texture texture) {
            super(texture);
            this.segID = segID;
            this.displayActor = new Image(texture);
            this.displayActor.setBounds(0, 0, texture.getWidth(), texture.getHeight());
        }

        public void alignDisplayAndDrag() {
            Vector2 actorCoords = displayActor.localToStageCoordinates(new Vector2(0, 0));
            this.setPosition(actorCoords.x, actorCoords.y);
        }
    }

}
