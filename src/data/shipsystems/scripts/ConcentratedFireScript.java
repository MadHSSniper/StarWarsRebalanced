package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

/*
 * Ship System: Concentrated Fire

 Function: Locks Shield Arc to 180, speeds up rotation for 3 seconds, then
 locks it at whatever angle you have it at, increases shield strength 50%,
 boost fire rate by 25% and firepower 25%. Lasts 10 seconds.

 Aftereffect: Forces ShieldPlugin into effect, no matter the flux level.
 fire rate is dropped by 25%, and fire power by 25% for duration fo ShieldPlugin

 thoughts I have.

 Since the ShieldPlugin gets the time from overload timer, perhaps have
 concentrated fire force an overload by dumping a set amount of flux at its
 end. Or simply force an overload that trigger shieldplugin and a script for
 penalties from concentrated fire.

 Have fun haha
 */
public class ConcentratedFireScript implements ShipSystemStatsScript
{
    private static final float SHIELD_ROTATION_BONUS_DURING_CHARGEUP = 500f;
    private static final float SHIELD_STRENGTH_BONUS = 50f;
    private static final float SHIELD_ARC_PENALTY = 50f;
    private static final float FIRE_RATE_BONUS = 25f;
    private static final float FIREPOWER_BONUS = 25f;
    private static final float FIRE_RATE_PENALTY_DURING_CHARGEDOWN = 25f;
    private static final float FIREPOWER_PENALTY_DURING_CHARGEDOWN = 25f;
    private static final StatusData SHIELD_STRENGTH_STATUSDATA = new StatusData(
            "shield strength increased " + (int) SHIELD_STRENGTH_BONUS
            + "%, arc reduced " + (int) SHIELD_ARC_PENALTY + "%", false);
    private static final StatusData SHIELD_TURN_INCREASED_STATUSDATA = new StatusData(
            "shield turning increased "
            + (int) SHIELD_ROTATION_BONUS_DURING_CHARGEUP + "%", false);
    private static final StatusData SHIELD_TURN_DISABLED_STATUSDATA = new StatusData(
            "shield turning disabled", true);
    private static final StatusData FIREPOWER_INCREASED_STATUSDATA = new StatusData(
            "firepower increased " + (int) FIREPOWER_BONUS + "%", false);
    private static final StatusData FIREPOWER_REDUCED_STATUSDATA = new StatusData(
            "firepower reduced " + (int) FIREPOWER_PENALTY_DURING_CHARGEDOWN + "%", true);
    private static final StatusData FIRE_RATE_INCREASED_STATUSDATA = new StatusData(
            "fire rate increased " + (int) FIRE_RATE_BONUS + "%", false);
    private static final StatusData FIRE_RATE_REDUCED_STATUSDATA = new StatusData(
            "fire rate reduced " + (int) FIRE_RATE_PENALTY_DURING_CHARGEDOWN + "%", true);
    private boolean penalized;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel)
    {
        // Grant bonus to shield absorption, but reduce the shield's arc
        stats.getShieldAbsorptionMult().modifyPercent(id, SHIELD_STRENGTH_BONUS);
        stats.getShieldArcBonus().modifyPercent(id, 1f - SHIELD_ARC_PENALTY);

        // Chargeup specific stats
        if (state.equals(State.IN))
        {
            // Allow shields to be quickly rotated during chargeup
            stats.getShieldTurnRateMult().modifyPercent(id,
                    SHIELD_ROTATION_BONUS_DURING_CHARGEUP);
            // Boost energy weapon fire rate and damage while system is active
            stats.getEnergyRoFMult().modifyPercent(id, FIRE_RATE_BONUS);
            stats.getEnergyWeaponDamageMult().modifyPercent(id, FIREPOWER_BONUS);
        }
        // Active specific stats
        else if (state.equals(State.ACTIVE))
        {
            // Lock shields after chargeup
            stats.getShieldTurnRateMult().modifyPercent(id, -500f);
            // Boost energy weapon fire rate and damage while system is active
            stats.getEnergyRoFMult().modifyPercent(id, FIRE_RATE_BONUS);
            stats.getEnergyWeaponDamageMult().modifyPercent(id, FIREPOWER_BONUS);
        }
        // Chargedown specific stats
        else if (state.equals(State.OUT))
        {
            // Penalize energy weapon fire rate and damage after system ends
            stats.getEnergyRoFMult().modifyPercent(id,
                    FIRE_RATE_PENALTY_DURING_CHARGEDOWN);
            stats.getEnergyWeaponDamageMult().modifyPercent(id,
                    FIREPOWER_PENALTY_DURING_CHARGEDOWN);

            // Force shield disable when system ends (through StarWarsShieldPlugin)
            if (!penalized)
            {
                ShipAPI ship = (ShipAPI) stats.getEntity();
                ship.getFluxTracker().increaseFlux(ship.getFluxTracker().getMaxFlux()
                        / 5f, true);
                ship.getFluxTracker().forceOverload(10f);
                penalized = true;
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id)
    {
        stats.getShieldAbsorptionMult().unmodify(id);
        stats.getShieldArcBonus().unmodify(id);
        stats.getShieldTurnRateMult().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyWeaponDamageMult().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel)
    {
        if (index == 0)
        {
            return SHIELD_STRENGTH_STATUSDATA;
        }
        if (index == 1)
        {
            if (state.equals(State.IN))
            {
                return SHIELD_TURN_INCREASED_STATUSDATA;
            }
            else
            {
                return SHIELD_TURN_DISABLED_STATUSDATA;
            }
        }
        if (index == 2)
        {
            if (state.equals(State.OUT))
            {
                return FIREPOWER_REDUCED_STATUSDATA;
            }
            else
            {
                return FIREPOWER_INCREASED_STATUSDATA;
            }
        }
        if (index == 3)
        {
            if (state.equals(State.OUT))
            {
                return FIRE_RATE_REDUCED_STATUSDATA;
            }
            else
            {
                return FIRE_RATE_INCREASED_STATUSDATA;
            }
        }

        return null;
    }
}
