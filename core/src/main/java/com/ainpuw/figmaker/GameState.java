package com.ainpuw.figmaker;

import com.badlogic.gdx.utils.Array;

public class GameState {
    public boolean doBox2DStep = true;
    public Array<WormSegment> wormSegs = null;
    public String dragAndDrogSourceName = "";
    public WormSegment.BasicImgSegment touchingSeg = null;
}
