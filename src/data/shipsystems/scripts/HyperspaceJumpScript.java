package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class HyperspaceJumpScript implements ShipSystemStatsScript {

    private static final String CHARGEUP_SOUND = "system_hyperspacejump_voice";

    //Local variables, don't touch these
    private boolean isActive = false;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        // instanceof also acts as a null check
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();
        // Chargeup, show particle inhalation effect
        if (state == State.IN) {
            Vector2f loc = new Vector2f(ship.getLocation());
            loc.x -= 70f * FastTrig.cos(ship.getFacing() * Math.PI / 180f);
            loc.y -= 70f * FastTrig.sin(ship.getFacing() * Math.PI / 180f);

            // Everything in this block is only done once per chargeup
            if (!isActive) {
                isActive = true;
                if(engine.getPlayerShip() == ship){
                    Global.getSoundPlayer().playSound(CHARGEUP_SOUND, 1f, 1f, ship.getLocation(), ship.getVelocity());
                }
                //graphic effects
            } else {
                //graphic effects
            }

        } // Cooldown, explode once system is finished
        else if (state == State.OUT) {
            // Everything in this section is only done once per cooldown
            if (isActive) {

                Vector2f loc = new Vector2f(ship.getLocation());
                loc.x -= 70f * FastTrig.cos(ship.getFacing() * Math.PI / 180f);
                loc.y -= 70f * FastTrig.sin(ship.getFacing() * Math.PI / 180f);
//graphic effects

                isActive = false;
            }
        }

    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (state == State.IN) {
            if (index == 0) {
                return new StatusData("Preparing for hyperspace jump", false);
            }
        }

        return null;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        isActive = false;
    }
}
