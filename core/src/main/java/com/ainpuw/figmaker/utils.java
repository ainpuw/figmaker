package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

public class utils {
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
