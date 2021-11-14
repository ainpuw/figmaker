package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

public class utils {
    public static HorizontalGroup initToolbox(UIConfig uiConfig, GameConfig gameConfig,
                                              DialogueActor dialogueBox) {
        HorizontalGroup toolbox = new HorizontalGroup();
        toolbox.setPosition(uiConfig.toolboxX, uiConfig.toolboxY);
        toolbox.setSize(uiConfig.toolboxW, uiConfig.toolboxH);
        toolbox.wrap(true);
        toolbox.rowAlign(Align.left);
        toolbox.space(uiConfig.toolboxSpacing);
        toolbox.wrapSpace(uiConfig.toolboxSpacing);

        for (int segID = 0; segID < gameConfig.wormSegs.size(); segID++) {
            Texture segTexture = new Texture(gameConfig.wormSegs.get(segID).imgPath);
            Image segActor = new Image(segTexture);
            segActor.setBounds(0, 0, segTexture.getWidth(), segTexture.getHeight());
            segActor.addListener(new ToolboxListener(gameConfig, dialogueBox, segID));
            toolbox.addActor(segActor);
        }
        // FIXME: For debug purpose.
        for (int i = 0; i < gameConfig.wormSegs.size(); i++) {
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegs.get(i).imgPath)));
        }

        return toolbox;
    }

    public static DragAndDrop initToolboxDragAndDrop() {
        DragAndDrop toolboxDrag = new DragAndDrop();
        /*
        Image sourceImage = new Image(new Texture("skin/default.png"));
        sourceImage.setBounds(50, 125, 100, 100);
        stage.addActor(sourceImage);
        Image sourceImage2 = new Image(new Texture("skin/default.png"));
        sourceImage2.setBounds(50, 125, 100, 100);
        stage.addActor(sourceImage2);

        Image validTargetImage = new Image(new Texture("skin/default.png"));
        validTargetImage.setBounds(200, 50, 100, 100);
        stage.addActor(validTargetImage);

        Image invalidTargetImage = new Image(new Texture("skin/default.png"));
        invalidTargetImage.setBounds(200, 200, 100, 100);
        stage.addActor(invalidTargetImage);


        toolboxDrag.addSource(new DragAndDrop.Source(sourceImage2) {
            @Null
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject("Some payload!");

                payload.setDragActor(getActor());

                Label validLabel = new Label("Some payload!", skin);
                validLabel.setColor(0, 1, 0, 1);
                payload.setValidDragActor(validLabel);

                Label invalidLabel = new Label("Some payload!", skin);
                invalidLabel.setColor(1, 0, 0, 1);
                payload.setInvalidDragActor(invalidLabel);

                return payload;
            }
        });
        toolboxDrag.addTarget(new DragAndDrop.Target(validTargetImage) {
            public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.GREEN);
                return true;
            }

            public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
            }

            public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
            }
        });
        toolboxDrag.addTarget(new DragAndDrop.Target(invalidTargetImage) {
            public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.RED);
                return false;
            }

            public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
            }

            public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
            }
        });
        */
        return toolboxDrag;
    }
}
