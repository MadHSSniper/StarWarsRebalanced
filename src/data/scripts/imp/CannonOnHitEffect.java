package data.scripts.imp;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class CannonOnHitEffect
  implements OnHitEffectPlugin
{
  public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine)
  {

	 Vector2f Location = projectile.getLocation();
	 Vector2f Velocity = projectile.getVelocity();
     /*
	 engine.applyDamage(target, Location, 
					 500f, DamageType.KINETIC, 0f,
					 false, false, null);
					 */
	 Velocity = Velocity.normalise(Velocity);
	 float x = Velocity.getX();
	 float y = Velocity.getY();
	 if(shieldHit)
		 {
		 for(int i = 0;i <= 10; i++)
			 {
				 float a = (float)Math.random()*80-40;
				 float b = (float)Math.random()*80-40;
				 float c = (float)Math.random()*0.1f;
				 Velocity.setX(a);
				 Velocity.setY(b);
				 engine.addSmoothParticle(Location,Velocity,30f,1,2f,new Color(25,100,155,255));
			 }
		 for(int i = 0;i <= 20; i++)
			 {
				 float a = (float)Math.random()*80-40;
				 float b = (float)Math.random()*80-40;
				 float c = (float)Math.random()*0.1f;
				 Velocity.setX(a);
				 Velocity.setY(b);
				 engine.addHitParticle(Location,Velocity,10f,1,3f,new Color(25,100,155,255));
			 }

		 }
	 else
		 {
		 for(int i = 0;i <= 30; i++)
			 {
				 float o = (float)Math.random()*80-(float)Math.random()*10+10;
				 float a = o+(float)Math.random()*50-20;
				 float b = o+(float)Math.random()*50-20;
				 float c = (float)Math.random()*25+5;
				 Velocity.setX(-x*a);
				 Velocity.setY(-y*b);
				 engine.addSmoothParticle(Location,Velocity,30f,0.8f,3f,new Color(255,90,0,150));
			 }
		 }


  }
}