package com.ainpuw.figmaker;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class AnimationManager {
    public SpineActor character;
    public final float characterMCCD = 2;  // Perform random walk every 2 seconds.
    public float characterMCReady = 0;
    public String characterMCState = "s_idle";
    // Animation Markov chain
    // The hash map keys are state names. The string values in the tuple are the names of the
    // animations in the spine file. "null" means staying in the original state.
    public final HashMap<String, Array<Utils.StrStrFloatTriple>> characterMC = new HashMap<String, Array<Utils.StrStrFloatTriple>>() {{
        put("s_idle", new Array<Utils.StrStrFloatTriple>() {{
            add(new Utils.StrStrFloatTriple("idle_to_chin", "s_chin", 0.65f));
            add(new Utils.StrStrFloatTriple("idle_to_hip", "s_hip",0.3f));
            add(new Utils.StrStrFloatTriple("null","s_idle", 0.05f));
        }});
        put("s_chin", new Array<Utils.StrStrFloatTriple>() {{
            add(new Utils.StrStrFloatTriple("chin_to_idle", "s_idle", 0.05f));
            add(new Utils.StrStrFloatTriple("null", "s_chin", 0.95f));
        }});
        put("s_hip", new Array<Utils.StrStrFloatTriple>() {{
            add(new Utils.StrStrFloatTriple("hip_to_idle", "s_idle", 0.05f));
            add(new Utils.StrStrFloatTriple("null", "s_hip", 0.95f));
        }});
    }};
    private boolean isRoutine1 = true;

    public AnimationManager(SpineActor character) {
        this.character = character;
    }

    public void setRoutine2() {
        isRoutine1 = false;
        character.animationState.setAnimation(1, "release", false);
    }

    public void update(float delta) {
        if (!isRoutine1) return;

        characterMCReady = Math.max(-1, characterMCReady - delta);

        // If off cooldown.
        if (characterMCReady <= 0) {
            characterMCReady = characterMCCD;  // Put on cooldown.
            // Get MC probabilities.
            Array<Utils.StrStrFloatTriple> probs = characterMC.get(characterMCState);
            // Roll dice.
            double rand = Math.random();
            // Determine dice roll.
            double subTotal = 0;
            String animationName = "null";
            for (Utils.StrStrFloatTriple tuple : probs) {
                subTotal += tuple.f;
                if (subTotal >= rand) {
                    // Update the next animation to play.
                    animationName = tuple.s1;
                    // Update the next state the character will be in.
                    characterMCState = tuple.s2;
                    break;
                }
            }

            // Update animation if not staying in the original state.
            if (animationName != "null") {
                character.animationState.setAnimation(1, animationName, false);
            }
        }
    }
}
