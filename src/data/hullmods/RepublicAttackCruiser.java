package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

@SuppressWarnings("unchecked")
public class RepublicAttackCruiser extends BaseHullMod {

    private static final float REFIT_BONUS = 1.25f; //25% increase

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFighterRefitTimeMult().modifyMult(id, REFIT_BONUS);
        stats.getNumFighterBays().modifyFlat(id, 2f);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return "" + (int) ((REFIT_BONUS-1)*100) + "%";
        }
        if (index == 1) {
            return "" + 2 + "";
        }
        return null;
    }
}
