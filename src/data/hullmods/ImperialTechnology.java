package data.hullmods;

//Import functions and APIs
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Set;
import java.util.HashSet;

//Class Name, called for in hull_mods.csv file. MUST MATCH
public class ImperialTechnology extends BaseHullMod {

//Variable name = Variable amount
    public static final float ARMOR_BONUS = 10f;
    public static final float DMG_BONUS = 10f;
    public static final float ROF_PENALTY = 20f;
    private static final Set ALLOWED_SHIPS = new HashSet();

    static {
        ALLOWED_SHIPS.add("victory_mki");
        ALLOWED_SHIPS.add("victory_mkii");
        ALLOWED_SHIPS.add("mki_sd");
        ALLOWED_SHIPS.add("mkii_sd");
        ALLOWED_SHIPS.add("carrack_cruiser");
        ALLOWED_SHIPS.add("dreadnaught_heavy");
        ALLOWED_SHIPS.add("immobilizer_418");
        ALLOWED_SHIPS.add("lancer_frigate");
        ALLOWED_SHIPS.add("mebulon_b_imp");
        ALLOWED_SHIPS.add("strike_cruiser");
        ALLOWED_SHIPS.add("vindicator_cruiser");
        ALLOWED_SHIPS.add("venator_sd");
    }
//Apply Stats usung declared variable

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getArmorBonus().modifyPercent(id, ARMOR_BONUS);
        stats.getEnergyWeaponDamageMult().modifyPercent(id, DMG_BONUS);
        stats.getEnergyRoFMult().modifyPercent(id, -ROF_PENALTY);
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ALLOWED_SHIPS.contains(ship.getHullSpec().getHullId());
    }
}
