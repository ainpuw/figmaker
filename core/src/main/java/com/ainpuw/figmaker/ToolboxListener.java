package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Null;

public class ToolboxListener extends InputListener {
    GameConfig gameConfig;
    UIConfig uiConfig;
    GameState state;
    DialogueActor dialogueBox;
    int segID;

    public ToolboxListener(GameConfig gameConfig, UIConfig uiConfig, GameState state, DialogueActor dialogueBox, int segID) {
        super();
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;
        this.state = state;
        this.dialogueBox = dialogueBox;
        this.segID = segID;

        // initToolboxDragAndDrop(500, 300);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        // Stop stepping Box2D when in building mode.
        state.doBox2DStep = false;
        // Add worm actors to stage for visualization.
        for (WormSegment seg : state.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                basicSeg.updateAndAddToStage();
            }
        }

        initToolboxDragAndDrop(x, y);

        // FIXME: This is for debug purpose.
        dialogueBox.updateText(gameConfig.wormSegConfigs.get(segID).name);

        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        // Resume stepping Box2D when the building action ends.
        state.doBox2DStep = true;
        // Remove worm actors from stage to reduce computation.
        for (WormSegment seg : state.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                basicSeg.removeFromStage();
            }
        }
    }

    private void initToolboxDragAndDrop(float x, float y) {
        uiConfig.toolboxDrag.clear();

        // Create a source actor. The target actors are already created and added.
        Image source = new Image(new Texture(gameConfig.wormSegConfigs.get(segID).imgPath));
        source.setPosition(x, y);
        //source.setBounds(0, 0, source.getImageWidth(), source.getImageHeight());
        uiConfig.stage.addActor(source);
        System.out.println("I am added!");

        uiConfig.toolboxDrag.addSource(new DragAndDrop.Source(source) {
            @Null
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject("Some payload!");

                payload.setDragActor(getActor());

                Label validLabel = new Label("connect", uiConfig.skin);
                validLabel.setColor(0, 1, 0, 1);
                payload.setValidDragActor(validLabel);

                return payload;
            }
        });

        /*
        for (WormSegment seg : state.wormSegs) {
            for (WormSegment.BasicSegment basicSeg : seg.basicSegs) {
                Array<Actor> temp = new Array<>();
                temp.add(basicSeg.leftEnd);
                temp.add(basicSeg.rightEnd);
                for (Actor actor : temp) {
                    uiConfig.toolboxDrag.addTarget(new DragAndDrop.Target(actor) {
                        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            getActor().getColor().a = 1f;
                            return true;
                        }

                        public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            getActor().getColor().a = 0f;
                        }

                        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
                        }
                    });
                }
            }
        }
         */

    }
}
