package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;

public class InterdictorSystemScript implements ShipSystemStatsScript {

    // Range in su, public so the AI script can use this value
    public static final float SYSTEM_RANGE = 5000f;
    private static final String DEBUFF_ID = "sw_interdictor_system";
    private static final Map debuffed = new WeakHashMap();

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        // Weaken own shields
        stats.getShieldAbsorptionMult().modifyMult(id, .5f * effectLevel);

        ShipAPI ship = (ShipAPI) stats.getEntity(), enemy;
        for (Iterator enemies = AIUtils.getEnemiesOnMap(ship).iterator(); enemies.hasNext();) {
            enemy = (ShipAPI) enemies.next();

            // Only affect ships that aren't already being debuffed by another ship
            if (debuffed.containsKey(enemy) && debuffed.get(enemy) != ship) {
                continue;
            }

            // Debuff all enemies in range
            if (MathUtils.getDistance(enemy, ship) <= SYSTEM_RANGE * effectLevel) {
                applyDebuff(enemy, effectLevel);
                debuffed.put(enemy, ship);
            } // Remove debuffs from enemies that moved out of our range
            else if (debuffed.containsKey(enemy)) {
                unapplyDebuff(enemy);
                debuffed.remove(enemy);
            }
        }
    }

    public void applyDebuff(ShipAPI ship, float effectLevel) {
        // Disable zero flux speed bonus
        ship.getMutableStats().getZeroFluxMinimumFluxLevel().modifyFlat(DEBUFF_ID, 1f);
        ship.getMutableStats().getZeroFluxSpeedBoost().modifyMult(DEBUFF_ID, 0f);
        ship.getMutableStats().getMaxSpeed().modifyMult(DEBUFF_ID, 0.75f);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        // Bring shields back to full strength
        stats.getShieldAbsorptionMult().unmodify(id);

        // Remove all debuffs created by this ship
        Map.Entry toDebuff;
        for (Iterator allBuffs = debuffed.entrySet().iterator(); allBuffs.hasNext();) {
            toDebuff = (Map.Entry) allBuffs.next();
            if (toDebuff.getValue() == stats.getEntity()) {
                unapplyDebuff((ShipAPI) toDebuff.getKey());
                allBuffs.remove();
            }
        }
    }

    public void unapplyDebuff(ShipAPI ship) {
        // Re-enable zero flux speed bonus
        ship.getMutableStats().getZeroFluxMinimumFluxLevel().unmodify(DEBUFF_ID);
        ship.getMutableStats().getZeroFluxSpeedBoost().unmodify(DEBUFF_ID);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        int range = (int) (SYSTEM_RANGE * effectLevel);

        if (index == 0) {
            return new StatusData("gravity well active within " + range + " su", false);
        }
        if (index == 1) {
            return new StatusData("shields weakened", true);
        }

        return null;
    }
}
