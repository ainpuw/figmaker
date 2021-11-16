package com.ainpuw.figmaker;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ToolboxListener extends InputListener {
    GameConfig gameConfig;
    UIConfig uiConfig;
    GameState state;
    Toolbox toolbox;
    DialogueActor dialogueBox;
    Toolbox.Tool segActor;
    int segID;

    public ToolboxListener(GameConfig gameConfig, UIConfig uiConfig, GameState state,
                           Toolbox toolbox, DialogueActor dialogueBox, Toolbox.Tool segActor, int segID) {
        super();
        this.gameConfig = gameConfig;
        this.uiConfig = uiConfig;
        this.state = state;
        this.toolbox = toolbox;
        this.dialogueBox = dialogueBox;
        this.segActor = segActor;
        this.segID = segID;
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

        // Set the displayActor half alpha.
        segActor.displayActor.getColor().a = 0.5f;

        initToolboxDragAndDrop(event.getStageX(), event.getStageY());

        // FIXME: This is for debug purpose.
        dialogueBox.updateText(gameConfig.wormSegConfigs.get(segID).name);

        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        // We don't put Box2D world state change here because DragAndDrop is conflicting with InputListener.
        // We handle state change in DragAndDrop instead.
    }

    private void initToolboxDragAndDrop(float x, float y) {
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
