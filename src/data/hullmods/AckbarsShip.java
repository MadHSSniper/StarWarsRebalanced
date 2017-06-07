package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

//Class Name, called for in hull_mods.csv file. MUST MATCH
@SuppressWarnings("unchecked")
public class AckbarsShip extends BaseHullMod {

    private static final float RANGE_BONUS = 1.25f; //25% increased range
    private static final float STRENGTH_INCREASE = 1.50f; //50% increased signal strength

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponRangeBonus().modifyMult(id, RANGE_BONUS);
        stats.getSensorStrength().modifyMult(id, STRENGTH_INCREASE);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) {
            return "" + (int) ((RANGE_BONUS-1)*100) + "%";
        }
        if (index == 1) {
            return "" + (int) ((STRENGTH_INCREASE-1)*100) + "%";
        }
        return null;
    }
}
