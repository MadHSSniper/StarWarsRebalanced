package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class TractorBeamEffect implements BeamEffectPlugin
{
    // Pull force per second
    private static final float PULL_STRENGTH = 500f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam)
    {
        CombatEntityAPI target = beam.getDamageTarget();
         if (target != null)
        {
            CombatUtils.applyForce(target,
                    VectorUtils.getDirectionalVector(beam.getTo(), beam.getFrom()),
                    PULL_STRENGTH * amount);
        }
    }
}

//Add this to weapon file:
//"beamEffect":"data.scripts.weapons.TractorBeamEffect",