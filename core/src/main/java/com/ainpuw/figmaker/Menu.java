package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;

public class Menu {
    private Config config;

    public Group contents;
    private Image tabBackground;
    private Image tabTagWorm;
    private Image tabTagStat;
    private Image tabTagUnknown;
    private HorizontalGroup tabWorm;
    public Array<ToolActor> segTools;
    private Actor tabStat;
    private Actor tabUnknown;

    // LibGDX doesn't seem to initialize everything synchronously on start up.
    // Try for 30 frames for this update to kick in. One can also force a ready().
    private int readyCounter = 0;
    private final int isReady = 30;

    public Menu(Config config) {
        this.config = config;

        // Initialize members.
        this.contents = new Group();
        this.tabBackground = new Image(config.menuBackgroundTexture);
        this.tabTagWorm = new Image(config.menuTabTagWormTexture);
        this.tabTagStat = new Image(config.menuTabTagStatTexture);
        this.tabTagUnknown = new Image(config.menuTabTagUnknownTexture);
        this.tabWorm = new HorizontalGroup();
        this.tabStat = new Actor();  // Dummy.
        this.tabUnknown = new Actor();  // Dummy.
        this.contents.setPosition(config.menuPosX, config.menuPosY);

        // Parameterize tab tags and background.
        this.tabTagWorm.setScale(config.tabTagScaleSmall);  // Hard coded.
        this.tabTagStat.setScale(config.tabTagScaleSmall);  // Hard coded.
        this.tabTagUnknown.setScale(config.tabTagScaleSmall);  // Hard coded.
        this.tabBackground.setPosition(0, -515);  // Hard coded.
        this.tabBackground.setVisible(false);
        setTagDefaultPositions();

        // Parameterize the worm tab.
        this.tabWorm.setPosition(config.toolboxX, config.toolboxY);
        this.tabWorm.setSize(config.toolboxW, config.toolboxH);
        this.tabWorm.wrap(true);
        this.tabWorm.rowAlign(Align.left);
        this.tabWorm.space(config.toolboxSpacing);
        this.tabWorm.wrapSpace(config.toolboxSpacing);
        this.segTools = new Array<>();
        for (int segID = 0; segID < config.wormSegConfigs.size(); segID++) {
            ToolActor segTool = new ToolActor(segID, config.wormSegConfigs.get(segID).texture);
            segTools.add(segTool);
            // displayActor is added into the HorizontalGroup.
            this.tabWorm.addActor(segTool.displayActor);
        }
        tabWorm.setVisible(false);

        // Parameterize the dummy tabs.
        tabStat.setVisible(false);
        tabUnknown.setVisible(false);

        // Initialize all listeners.
        initializeListeners();

        // Add all initially visible elements to group.
        this.contents.addActor(this.tabTagWorm);
        this.contents.addActor(this.tabTagStat);
        this.contents.addActor(this.tabTagUnknown);
    }

    private void initializeListeners() {
        // Initialize sources for drag and drop.
        config.toolboxDragAndDrop.clear();
        for (ToolActor tool : segTools) {
            config.toolboxDragAndDrop.addSource(new DragAndDrop.Source(tool) {
                @Null
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    ToolActor toolActor = (ToolActor) getActor();
                    // Use this global variable to mark the actor being dragged.
                    config.dragAndDrogSourceName = config.wormSegConfigs.get(toolActor.segID).name;
                    // Set the ToolActor's displayActor to half alpha.
                    toolActor.displayActor.getColor().a = 0.5f;

                    // Stop stepping Box2D when in building mode.
                    config.evolveWorld = false;
                    // Add worm display actors to stage for visualization.
                    for (WormSegment wormSeg : config.worm.segs) {
                        for (WormSegment.BasicSegment basicSeg : wormSeg.basicSegs) {
                            basicSeg.updateAndAddToStage();
                        }
                    }
                    addWormTargetsToDragAndDrop();

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    // FIXME: Is this needed?
                    payload.setObject("Payload" + config.dragAndDrogSourceName);
                    payload.setDragActor(toolActor);

                    // FIXME: This is for debug purpose.
                    config.dialogueBox.updateText(config.dragAndDrogSourceName);

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
                    config.evolveWorld = true;
                    // Remove worm actors from stage to save computation.
                    for (WormSegment wormSeg : config.worm.segs) {
                        for (WormSegment.BasicSegment basicSeg : wormSeg.basicSegs) {
                            basicSeg.removeFromStage();
                        }
                    }
                }
            });
        }

        // Initialize listeners to tab tags.
        tabTagWorm.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (tabBackground.isVisible() && tabWorm.isVisible()) {
                    resetAllTabs();
                } else {
                    resetAllTabs();  // Hiding another tab if open.
                    readyCounter = 0;
                    tabBackground.setVisible(true);
                    tabWorm.setVisible(true);
                    // tabBackground.setColor(Color.WHITE.cpy().lerp(Color.RED, .05f));
                    contents.addActor(tabBackground);
                    contents.addActor(tabWorm);
                    for (ToolActor tool : segTools)  {
                        // Add to stage directly to avoid dealing with local vs. stage coordinates.
                        config.stage.addActor(tool);
                    }
                    tabTagWorm.toFront();
                    tabTagWorm.setScale(config.tabTagScaleLarge);
                    tabTagWorm.setPosition(tabTagWorm.getX() - 5, tabTagWorm.getY() - 5);
                }
                return true;
            }
        });
        tabTagStat.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (tabBackground.isVisible() && tabStat.isVisible()) {
                    resetAllTabs();
                } else {
                    resetAllTabs();  // Hiding another tab if open.
                    tabBackground.setVisible(true);
                    tabStat.setVisible(true);
                    // tabBackground.setColor(Color.WHITE.cpy().lerp(Color.GREEN, .05f));
                    contents.addActor(tabBackground);
                    contents.addActor(tabStat);
                    tabTagStat.toFront();
                    tabTagStat.setScale(config.tabTagScaleLarge);
                    tabTagStat.setPosition(tabTagStat.getX() - 5, tabTagStat.getY() - 5);
                }
                return true;
            }
        });
        tabTagUnknown.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (tabBackground.isVisible() && tabUnknown.isVisible()) {
                    resetAllTabs();
                } else {
                    resetAllTabs();  // Hiding another tab if open.
                    tabBackground.setVisible(true);
                    tabUnknown.setVisible(true);
                    // tabBackground.setColor(Color.WHITE.cpy().lerp(Color.YELLOW, .05f));
                    contents.addActor(tabBackground);
                    contents.addActor(tabUnknown);
                    tabTagUnknown.toFront();
                    tabTagUnknown.setScale(config.tabTagScaleLarge);
                    tabTagUnknown.setPosition(tabTagUnknown.getX() - 5, tabTagUnknown.getY() - 5);
                }
                return true;
            }
        });
    }

    private void addWormTargetsToDragAndDrop() {
        for (WormSegment wormSeg : config.worm.segs) {
            for (WormSegment.BasicSegment basicSeg : wormSeg.basicSegs) {
                Array<WormSegment.BasicImgSegment> tempArray = new Array<>();
                tempArray.add(basicSeg.leftEnd);
                tempArray.add(basicSeg.rightEnd);
                for (WormSegment.BasicImgSegment basicImgSeg : tempArray) {
                    config.toolboxDragAndDrop.addTarget(new DragAndDrop.Target(basicImgSeg) {
                        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            WormSegment.BasicImgSegment indicatorImgActor = (WormSegment.BasicImgSegment) getActor();
                            indicatorImgActor.getColor().a = 1f;
                            config.touchingSeg = indicatorImgActor;
                            return true;
                        }

                        public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            WormSegment.BasicImgSegment indicatorImgActor = (WormSegment.BasicImgSegment) getActor();
                            indicatorImgActor.getColor().a = 0f;
                            config.touchingSeg = null;
                        }

                        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            if (config.touchingSeg == null) return;

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
                            spawnX += (config.segMidW / 2 + config.segEndW) / 2;
                            spawnY += config.segMidH / 2;
                            // FIXME: This shift amount needs to be adjusted. Not good enough.
                            if (indicatorImgActor2.getX() > indicatorImgActor.getX())
                                spawnX -= config.segMidW / 2;
                            else
                                spawnX += config.segMidW / 2;
                            if (indicatorImgActor2.getY() > indicatorImgActor.getY())
                                spawnX -= config.segMidH;
                            else
                                spawnX += config.segMidH;

                            WormSegment newSeg = new WormSegment(config, spawnX, spawnY, 0);
                            config.worm.segs.add(newSeg);
                            Worm.joinSegments(newSeg, config.touchingSeg.parent, isLeft, config.worm.repulsivePairs);

                            // Reset global variables.
                            config.dragAndDrogSourceName = "";
                            config.touchingSeg = null;
                        }
                    });
                }
            }
        }
    }

    private void setTagDefaultPositions() {
        tabTagWorm.setScale(config.tabTagScaleSmall);
        tabTagStat.setScale(config.tabTagScaleSmall);
        tabTagUnknown.setScale(config.tabTagScaleSmall);
        tabTagWorm.setPosition(0, 0);
        tabTagStat.setPosition(75, 0);  // Hard coded.
        tabTagUnknown.setPosition(150, 0);  // Hard coded.
    }

    private void resetAllTabs() {
        tabBackground.setVisible(false);
        tabWorm.setVisible(false);
        tabStat.setVisible(false);
        tabUnknown.setVisible(false);
        tabBackground.remove();
        tabWorm.remove();
        tabStat.remove();
        tabUnknown.remove();
        for (ToolActor tool : segTools)  tool.remove();
        setTagDefaultPositions();
    }

    public void ready(boolean force) {
        if (readyCounter < isReady || force) {
            for (ToolActor tool : segTools) {
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
