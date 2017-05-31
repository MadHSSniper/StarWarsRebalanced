package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.lazywizard.lazylib.combat.CombatUtils;

/*
 Ship System: Long Range Bombardment

 Function: Star Destroyer Main Guns in bombardment mode, 300% Dmg, 25% fire
 rate, 50% turn rate, full map range. all other weapons offline. shields
 offline while in use. Generates X amount flux/sec while in use.

 Aftereffect: main guns oofline, other weapons online again. Main guns remain
 offline for X amount of time.
 */
public class StarDestroyerBombardmentScript implements ShipSystemStatsScript
{
    private static final String BOMBARDMENT_WEAPON = "bombardment_battery";
    private static final String BOMBARDMENT_CHARGE_LOOP = "system_high_energy_focus_loop";
    private static final String BOMBARDMENT_FIRE_SOUND = "explosion_flak";
    private static final float TIME_BETWEEN_BOMBARDMENTS = 12f;
    private static final float WEAPON_TURN_RATE_PENALTY = 50f;
    private static final Set BOMBARDMENT_CAPABLE_WEAPONS = new HashSet();
    private Set wepsToFire = new HashSet();
    private float nextFire = 0f;
    private boolean hasChecked = false;

    static
    {
        BOMBARDMENT_CAPABLE_WEAPONS.add("imp_main_battery");
        BOMBARDMENT_CAPABLE_WEAPONS.add("imp_barbette_turbo");
    }

    private void scanForBombardmentCapableWeapons(ShipAPI ship)
    {
        WeaponAPI wep;
        for (Iterator weps = ship.getAllWeapons().iterator(); weps.hasNext();)
        {
            wep = (WeaponAPI) weps.next();

            if (BOMBARDMENT_CAPABLE_WEAPONS.contains(wep.getId()))
            {
                wepsToFire.add(wep);
            }
        }
    }

    public static void main(String[] args)
    {
        float offset = 20;
        float nextFire = offset + TIME_BETWEEN_BOMBARDMENTS;

        for (float curTime = offset; curTime <= nextFire; curTime += 1f)
        {
            float chargeLevel = 1.0f - ((nextFire - curTime) / TIME_BETWEEN_BOMBARDMENTS);
            System.out.println("Charge level: " + chargeLevel);
        }
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel)
    {
        stats.getWeaponTurnRateBonus().modifyPercent(id, -WEAPON_TURN_RATE_PENALTY);

        ShipAPI ship = (ShipAPI) stats.getEntity();
        //float curTime = CombatUtils.getElapsedCombatTime();
        float curTime = Global.getCombatEngine().getElapsedInContactWithEnemy();
        
        if (!hasChecked)
        {
            nextFire = curTime + TIME_BETWEEN_BOMBARDMENTS;
            scanForBombardmentCapableWeapons(ship);
            hasChecked = true;
        }

        float chargeLevel = 1.0f - ((nextFire - curTime) / TIME_BETWEEN_BOMBARDMENTS);
        Global.getSoundPlayer().playLoop(BOMBARDMENT_CHARGE_LOOP, ship,
                1f + chargeLevel, 1f + chargeLevel, ship.getLocation(), ship.getVelocity());

        if (curTime > nextFire)
        {
            nextFire = curTime + TIME_BETWEEN_BOMBARDMENTS;
            WeaponAPI wep;
            for (Iterator toFire = wepsToFire.iterator(); toFire.hasNext();)
            {
                wep = (WeaponAPI) toFire.next();
                wep.setRemainingCooldownTo(wep.getCooldown() * chargeLevel);
                /*CombatUtils*/Global.getCombatEngine().spawnProjectile(
                        ship, wep, BOMBARDMENT_WEAPON, wep.getLocation(),
                        wep.getCurrAngle(), ship.getVelocity());
                Global.getSoundPlayer().playSound(BOMBARDMENT_FIRE_SOUND,
                        1f, 1f, wep.getLocation(), ship.getVelocity());
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id)
    {
        hasChecked = false;

        WeaponAPI wep;
        for (Iterator weps = wepsToFire.iterator(); weps.hasNext();)
        {
            wep = (WeaponAPI) weps.next();
            wep.setRemainingCooldownTo(wep.getCooldown());
        }
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel)
    {
        if (index == 0)
        {
            return new StatusData("bombardment active", false);
        }
        if (index == 1)
        {
            return new StatusData("next bombardment in "
                    + (int) (nextFire - Global.getCombatEngine().getElapsedInContactWithEnemy())//CombatUtils.getElapsedCombatTime())
                    + " seconds", false);
        }

        return null;
    }
}
