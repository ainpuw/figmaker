package com.ainpuw.figmaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Utils {
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
