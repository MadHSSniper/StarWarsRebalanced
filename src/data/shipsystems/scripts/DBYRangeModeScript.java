package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class DBYRangeModeScript implements ShipSystemStatsScript 
{


	private static final float BONUS_PERCENT = 50f;
	private static final float WEAPON_PENALTY = 75f;
	private static final float STAT_MODIFIER = 25f;


		//static status
	private static final StatusData WEAPON_PENALTY_STATUS = new StatusData(
		"Rate of fire and tracking speed reduced " + (int) WEAPON_PENALTY + "%", true);


		//charging status messages
	private static final StatusData	WEAPON_STATUS_CHARGEUP = new StatusData(
		"charging main guns", true);
	private static final StatusData STATS_DURING_CHARGEUP = new StatusData(
		"shield strength and manueverability reduced " + (int) 50f + "%", true);	

		//active status messages
	private static final StatusData FIREPOWER_BONUS = new StatusData(
		"damage and range increased " + (int) BONUS_PERCENT + "%", false);
	private static final StatusData STATS_ACTIVE = new StatusData(
		"shield strength and manueverability reduced " + (int) 25f + "%", true);	
		
		//charge down status messages
	private static final StatusData STATS_WHILE_RESETTING = new StatusData(
		"shield strength and manueverability boosted " + (int) STAT_MODIFIER + "%", false);
	private static final StatusData TARGETING_SYSTEM_CHARGEDOWN = new StatusData(
		"returning to fast track mode", true);


	private boolean penalized;

	@Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		stats.getWeaponTurnRateBonus().modifyPercent(id, -WEAPON_PENALTY);
		stats.getEnergyRoFMult().modifyPercent(id, -WEAPON_PENALTY);
		
		if (state.equals(State.IN))
			{
				stats.getMaxSpeed().modifyPercent(id, -50f);
				stats.getAcceleration().modifyPercent(id, -50f);
				stats.getDeceleration().modifyPercent(id, -50f);
				stats.getMaxTurnRate().modifyPercent(id, -50f);
				stats.getTurnAcceleration().modifyPercent(id, -50f);
				stats.getShieldDamageTakenMult().modifyPercent(id, 50f);
				stats.getEnergyRoFMult().modifyPercent(id, -100000f);
			}
		else if (state.equals(State.ACTIVE))
			{
				stats.getMaxSpeed().modifyPercent(id, -STAT_MODIFIER);
				stats.getAcceleration().modifyPercent(id, -STAT_MODIFIER);
				stats.getDeceleration().modifyPercent(id, -STAT_MODIFIER);
				stats.getMaxTurnRate().modifyPercent(id, -STAT_MODIFIER);
				stats.getTurnAcceleration().modifyPercent(id, -STAT_MODIFIER);
				stats.getShieldDamageTakenMult().modifyPercent(id, 25f);
				stats.getEnergyWeaponDamageMult().modifyPercent(id, BONUS_PERCENT);
				stats.getEnergyWeaponRangeBonus().modifyPercent(id, BONUS_PERCENT);
				stats.getAutofireAimAccuracy().modifyPercent(id, BONUS_PERCENT);
				stats.getSightRadiusMod().modifyPercent(id, 200f);
				stats.getEnergyRoFMult().modifyPercent(id, -WEAPON_PENALTY);
			}
		else if (state.equals(State.OUT))
		{
				stats.getMaxSpeed().modifyPercent(id, STAT_MODIFIER);
				stats.getAcceleration().modifyPercent(id, STAT_MODIFIER);
				stats.getDeceleration().modifyPercent(id, STAT_MODIFIER);
				stats.getMaxTurnRate().modifyPercent(id, STAT_MODIFIER);
				stats.getTurnAcceleration().modifyPercent(id, STAT_MODIFIER);
				stats.getShieldDamageTakenMult().modifyPercent(id, -25f);
				stats.getEnergyWeaponRangeBonus().modifyPercent(id, -STAT_MODIFIER);
				stats.getEnergyWeaponDamageMult().modifyPercent(id, -STAT_MODIFIER);
				stats.getAutofireAimAccuracy().modifyPercent(id, -STAT_MODIFIER);
				stats.getEnergyRoFMult().modifyPercent(id, -100000f);	
		}

	}

	@Override
	public void unapply(MutableShipStatsAPI stats, String id) 
		{
			stats.getMaxSpeed().unmodify(id);
			stats.getAcceleration().unmodify(id);
			stats.getDeceleration().unmodify(id);
			stats.getMaxTurnRate().unmodify(id);
			stats.getTurnAcceleration().unmodify(id);
			stats.getShieldDamageTakenMult().unmodify(id);
			stats.getEnergyWeaponDamageMult().unmodify(id);
			stats.getEnergyWeaponRangeBonus().unmodify(id);
			stats.getAutofireAimAccuracy().unmodify(id);
			stats.getEnergyRoFMult().unmodify(id);
            stats.getSightRadiusMod().unmodify(id);
		}
	
    @Override
    public StatusData getStatusData(int index, State state, float effectLevel)
    {
        if (index == 0)
        {
            return WEAPON_PENALTY_STATUS;
        }
        if (index == 1)
        {
            if (state.equals(State.IN))
            {
                return WEAPON_STATUS_CHARGEUP;
            }
            else if (state.equals(State.OUT))
            {
                return STATS_WHILE_RESETTING;
            }
            else 
            {
            	return FIREPOWER_BONUS;
            }

        }
        if (index == 2)
        {
            if (state.equals(State.IN))
            {
                return STATS_DURING_CHARGEUP;
            }
            else if (state.equals(State.OUT))
            {
                return TARGETING_SYSTEM_CHARGEDOWN;
            }
            else 
            {
            	return STATS_ACTIVE;
            }

        }
            return null;
     }
}
