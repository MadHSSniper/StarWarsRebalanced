package data.scripts.imp;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.AnimationAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class xl_mac_Effect implements EveryFrameWeaponEffectPlugin {

	
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		if (engine.isPaused()) return;
		if (weapon.getAnimation() ==null)return;
		if (weapon.isFiring())weapon.getAnimation().play();
		//engine.addFloatingText(weapon.getLocation(), ""+weapon.getCooldown(), 100, new Color(255,255,255,255), weapon.getShip(), 0.001f, 0.001f);
		if ((weapon.getCooldownRemaining()/weapon.getCooldown())>0.5f)
		{
			if (weapon.isFiring() && weapon.getAnimation().getFrame()==44 && 0.4f>Math.random())
			{
				weapon.getAnimation().setFrame(41);
			}

			if (weapon.isFiring() && weapon.getAnimation().getFrame()==52 )
			{
				weapon.getAnimation().setFrame(41);
			}
		}
		else
		{
			if (weapon.isFiring())
			{
				Vector2f ww = weapon.getLocation();
				Vector2f fire1 = new Vector2f((float)Math.cos((weapon.getCurrAngle()+100f)*Math.PI/180),(float)Math.sin((weapon.getCurrAngle()+100)*Math.PI/180));
				Vector2f fire2 = new Vector2f((float)Math.cos((weapon.getCurrAngle()-100f)*Math.PI/180),(float)Math.sin((weapon.getCurrAngle()-100)*Math.PI/180));
				Vector2f Velocity0 = new Vector2f((float)Math.cos((weapon.getCurrAngle()-90f)*Math.PI/180),(float)Math.sin((weapon.getCurrAngle()-90)*Math.PI/180));
				fire1 = fire1.normalise(fire1);
				fire1.setX(fire1.getX()*21+ww.getX());
				fire1.setY(fire1.getY()*21+ww.getY());
				fire2 = fire2.normalise(fire2);
				fire2.setX(fire2.getX()*21+ww.getX());
				fire2.setY(fire2.getY()*21+ww.getY());
				Velocity0 = Velocity0.normalise(Velocity0);
				float o = (float)Math.random()*80-(float)Math.random()*10+10;
				float a = o+(float)Math.random()*50-20;
				float b = o+(float)Math.random()*50-20;
				float c = (float)Math.random()*25+5;
				Vector2f Velocity = new Vector2f(0,0);
				Velocity.setX(Velocity0.getX()*a);
				Velocity.setY(Velocity0.getY()*b);
				engine.addHitParticle(fire2,Velocity,5f,1,1f,new Color(25,100,155,255));
				Velocity.setX(Velocity0.getX()*-1*b);
				Velocity.setY(Velocity0.getY()*-1*a);
				engine.addHitParticle(fire1,Velocity,5f,1,1f,new Color(25,100,155,255));
			}
			if (weapon.isFiring() && weapon.getAnimation().getFrame()==0)
			{
				weapon.getAnimation().pause();
			}

		}
		/*
		if (weapon.isFiring() && weapon.getAnimation().getFrame() > 52)
		{
			weapon.getAnimation().setFrame(91- weapon.getAnimation().getFrame());
		}
		*/
	}

}
