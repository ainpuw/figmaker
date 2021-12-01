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

    public static class StrStrFloatTriple {
        public final String s1;
        public final String s2;
        public final float f;

        public StrStrFloatTriple(String s1, String s2, float f) {
            this.s1 = s1;
            this.s2 = s2;
            this.f = f;
        }
    }
}
