package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;

@SuppressWarnings("unchecked")
public class BlockadeRunner extends BaseHullMod {

    private static final float HANDLING_BONUS = 1.1f; //10% increase
    private static final float ZEROFLUX_MULT = 0.2f; //20% max flux

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().modifyMult(id, HANDLING_BONUS);
        stats.getAcceleration().modifyMult(id, HANDLING_BONUS);
        stats.getMaxTurnRate().modifyMult(id, HANDLING_BONUS);
        stats.getTurnAcceleration().modifyMult(id, HANDLING_BONUS);
        stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, ZEROFLUX_MULT);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) {
            return "" + (int) ((HANDLING_BONUS-1)*100) + "%";
        }
        if (index == 1) {
            return "" + (int) (ZEROFLUX_MULT*100) + "%";
        }
        return null;
    }
}
