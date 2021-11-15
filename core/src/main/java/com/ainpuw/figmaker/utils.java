package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;

public class utils {
    public static HorizontalGroup initToolbox(UIConfig uiConfig, GameConfig gameConfig,
                                              GameState state, DialogueActor dialogueBox) {
        HorizontalGroup toolbox = new HorizontalGroup();
        toolbox.setPosition(uiConfig.toolboxX, uiConfig.toolboxY);
        toolbox.setSize(uiConfig.toolboxW, uiConfig.toolboxH);
        toolbox.wrap(true);
        toolbox.rowAlign(Align.left);
        toolbox.space(uiConfig.toolboxSpacing);
        toolbox.wrapSpace(uiConfig.toolboxSpacing);

        for (int segID = 0; segID < gameConfig.wormSegConfigs.size(); segID++) {
            Texture segTexture = new Texture(gameConfig.wormSegConfigs.get(segID).imgPath);
            Image segActor = new Image(segTexture);
            segActor.setBounds(0, 0, segTexture.getWidth(), segTexture.getHeight());
            segActor.addListener(new ToolboxListener(gameConfig, uiConfig, state, dialogueBox, segID));
            toolbox.addActor(segActor);
        }
        // FIXME: For debug purpose.
        for (int i = 0; i < gameConfig.wormSegConfigs.size(); i++) {
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegConfigs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegConfigs.get(i).imgPath)));
            toolbox.addActor(new Image(new Texture(gameConfig.wormSegConfigs.get(i).imgPath)));
        }

        return toolbox;
    }

    public static void drawGameBoundingBox (UIConfig uiConfig, SpriteBatch spriteBatch,
                                            ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(0, 0, uiConfig.w, uiConfig.h);
        shapeRenderer.line(0, uiConfig.h, uiConfig.w, 0);
        shapeRenderer.line(0, 0, uiConfig.w, 0);
        shapeRenderer.line(0, 0, 0, uiConfig.h);
        shapeRenderer.line(0, uiConfig.h, uiConfig.w, uiConfig.h);
        shapeRenderer.line(uiConfig.w, 0, uiConfig.w, uiConfig.h);
        shapeRenderer.end();
    }
}
