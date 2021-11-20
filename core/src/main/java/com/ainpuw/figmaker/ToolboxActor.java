package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;

public class ToolboxActor extends HorizontalGroup {
    GameConfig gameConfig;
    UIConfig uiConfig;
    Array<ToolActor> tools;
    // LibGDX doesn't seem to initialize everything synchronously on start up.
    // Try for 30 frames for this update to kick in. One can also force a ready().
    private int readyCounter = 0;
    private final int isReady = 30;

    public ToolboxActor(GameConfig gameConfig, UIConfig uiConfig) {
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;

        this.setPosition(uiConfig.toolboxX, uiConfig.toolboxY);
        this.setSize(uiConfig.toolboxW, uiConfig.toolboxH);
        this.wrap(true);
        this.rowAlign(Align.left);
        this.space(uiConfig.toolboxSpacing);
        this.wrapSpace(uiConfig.toolboxSpacing);

        tools = new Array<>();
        for (int segID = 0; segID < gameConfig.wormSegConfigs.size(); segID++) {
            ToolActor segTool = new ToolActor(segID, gameConfig.wormSegConfigs.get(segID).texture);
            tools.add(segTool);
            // Tool is added directly to stage, its displayActor is added into ToolboxActor,
            // which in turn is added to stage.
            uiConfig.stage.addActor(segTool);
            this.addActor(segTool.displayActor);
        }

        // Drag and drop offsets for touch screen inputs.
        uiConfig.toolboxDragAndDrop.setDragActorPosition(uiConfig.dragActorPositionX, uiConfig.dragActorPositionY);
        uiConfig.toolboxDragAndDrop.setTouchOffset(uiConfig.dragAndDropTouchOffsetX, uiConfig.dragAndDropTouchOffsetY);

        // Initialize sources for drag and drop.
        uiConfig.toolboxDragAndDrop.clear();
        for (ToolActor tool : tools) {
            uiConfig.toolboxDragAndDrop.addSource(new DragAndDrop.Source(tool) {
                @Null
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    ToolActor toolActor = (ToolActor) getActor();
                    // Use this global variable to mark the actor being dragged.
                    uiConfig.dragAndDrogSourceName = gameConfig.wormSegConfigs.get(toolActor.segID).name;
                    // Set the ToolActor's displayActor to half alpha.
                    toolActor.displayActor.getColor().a = 0.5f;

                    // Stop stepping Box2D when in building mode.
                    gameConfig.evolveWorld = false;
                    // Add worm display actors to stage for visualization.
                    for (WormSegment wormSeg : gameConfig.wormSegs) {
                        for (WormSegment.BasicSegment basicSeg : wormSeg.basicSegs) {
                            basicSeg.updateAndAddToStage();
                        }
                    }
                    addWormTargetsToDragAndDrop();

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    // FIXME: Is this needed?
                    payload.setObject("Payload" + uiConfig.dragAndDrogSourceName);
                    payload.setDragActor(toolActor);

                    // FIXME: This is for debug purpose.
                    uiConfig.dialogueBox.updateText(uiConfig.dragAndDrogSourceName);

                    return payload;
                }

                @Null
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    ToolActor toolActor = (ToolActor) getActor();

                    // Realign ToolActor and its displayActor.
                    ready(true);
                    // Set the ToolActor's displayActor to full alpha.
                    toolActor.displayActor.getColor().a = 1f;
                    // Resume stepping Box2D when the building action ends.
                    gameConfig.evolveWorld = true;
                    // Remove worm actors from stage to save computation.
                    for (WormSegment wormSeg : gameConfig.wormSegs) {
                        for (WormSegment.BasicSegment basicSeg : wormSeg.basicSegs) {
                            basicSeg.removeFromStage();
                        }
                    }
                }
            });
        }
    }

    private void addWormTargetsToDragAndDrop() {
        for (WormSegment wormSeg : gameConfig.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : wormSeg.basicSegs) {
                Array<WormSegment.BasicImgSegment> tempArray = new Array<>();
                tempArray.add(basicSeg.leftEnd);
                tempArray.add(basicSeg.rightEnd);
                for (WormSegment.BasicImgSegment basicImgSeg : tempArray) {
                    uiConfig.toolboxDragAndDrop.addTarget(new DragAndDrop.Target(basicImgSeg) {
                        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            WormSegment.BasicImgSegment indicatorImgActor = (WormSegment.BasicImgSegment) getActor();
                            indicatorImgActor.getColor().a = 1f;
                            gameConfig.touchingSeg = indicatorImgActor;
                            return true;
                        }

                        public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            WormSegment.BasicImgSegment indicatorImgActor = (WormSegment.BasicImgSegment) getActor();
                            indicatorImgActor.getColor().a = 0f;
                            gameConfig.touchingSeg = null;
                        }

                        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            if (gameConfig.touchingSeg == null) return;

                            // Create a WormSegment and join it to the touched BasicSegment.
                            WormSegment.BasicImgSegment indicatorImgActor = (WormSegment.BasicImgSegment) getActor();
                            boolean isLeft = indicatorImgActor.isLeft;
                            // Get the actor to the other end.
                            WormSegment.BasicImgSegment indicatorImgActor2 = indicatorImgActor.parent.leftEnd;
                            if (isLeft)  indicatorImgActor2 = indicatorImgActor.parent.rightEnd;

                            // Decide spawn point.
                            // indicatorImgActor lower left corner position.
                            float spawnX = indicatorImgActor.getX();
                            float spawnY = indicatorImgActor.getY();
                            // Convert to spawn body center position, ignoring indicatorImgActor rotation.
                            spawnX += (gameConfig.segMidW / 2 + gameConfig.segEndW) / 2;
                            spawnY += gameConfig.segMidH / 2;
                            // FIXME: This shift amount needs to be adjusted. Not good enough.
                            if (indicatorImgActor2.getX() > indicatorImgActor.getX())
                                spawnX -= gameConfig.segMidW / 2;
                            else
                                spawnX += gameConfig.segMidW / 2;
                            if (indicatorImgActor2.getY() > indicatorImgActor.getY())
                                spawnX -= gameConfig.segMidH;
                            else
                                spawnX += gameConfig.segMidH;

                            WormSegment newSeg = new WormSegment(gameConfig, uiConfig, uiConfig.dragAndDrogSourceName, spawnX, spawnY);
                            gameConfig.wormSegs.add(newSeg);
                            Worm.joinSegments(newSeg, gameConfig.touchingSeg.parent, isLeft);

                            // Reset global variables.
                            uiConfig.dragAndDrogSourceName = "";
                            gameConfig.touchingSeg = null;
                        }
                    });
                }
            }
        }
    }

    public void ready(boolean force) {
        if (readyCounter < isReady || force) {
            for (ToolActor tool : tools) {
                tool.toFront();
                tool.alignDisplayAndDrag();
            }
            readyCounter = Math.min(readyCounter + 1, isReady);
        }
    }

    ////////////////////////////////////////////////////
    // ToolActor subclass definition
    ////////////////////////////////////////////////////

    public class ToolActor extends Image {
        int segID;
        // The class instance itself is the actor being dragged around.
        // displayActor is the one added to the HorizontalGroup for display purposes.
        public Image displayActor;

        public ToolActor(int segID, Texture texture) {
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
