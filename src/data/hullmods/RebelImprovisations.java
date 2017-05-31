package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Set;
import java.util.HashSet;

public class RebelImprovisations extends BaseHullMod {

    public static final float FIRE_RATE = 25f;
    public static final float WEAPON_RANGE = 25f;
    public static final float TRACK_SPEED = 35f;
    public static final float DMG_PENALTY = 15f;

    private static final Set ALLOWED_SHIPS = new HashSet();

    static {
        ALLOWED_SHIPS.add("assault_frigate");
        ALLOWED_SHIPS.add("cr90_corvette");
        ALLOWED_SHIPS.add("nebulon_b");
        ALLOWED_SHIPS.add("venator_sd");
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponDamageMult().modifyPercent(id, -DMG_PENALTY);
        stats.getEnergyRoFMult().modifyPercent(id, FIRE_RATE);
        stats.getWeaponTurnRateBonus().modifyPercent(id, TRACK_SPEED);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE);
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ALLOWED_SHIPS.contains(ship.getHullSpec().getHullId());
    }
}
