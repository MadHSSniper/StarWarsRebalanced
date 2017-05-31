package data.scripts.imp;

import com.fs.starfarer.api.combat.BoundsAPI.SegmentAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.BoundsAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class MM3500OnHitEffect
  implements OnHitEffectPlugin
{
  private static Map range  = new HashMap();
	static {
		range.put("3500MM_shot", 500f);
		range.put("4800MM_shot", 800f);
	}

  public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine)
  {
	 float maxrange = (float)((Float)range.get(projectile.getProjectileSpecId()));
	 Vector2f Velocity = projectile.getVelocity();
	 Velocity = Velocity.normalise(Velocity);
	 float x = Velocity.getX();
	 float y = Velocity.getY();
	 Vector2f zero = new Vector2f(0f,0f);
	 Vector2f Location = projectile.getLocation();
	 Vector2f targetLocation = target.getLocation();
     engine.spawnExplosion(Location, zero, new Color(255,158,0,150), maxrange, 1.5f);
	 for(int i = 0;i <= 15; i++)
	{
				 float a = (float)Math.random()*80-40;
				 float b = (float)Math.random()*80-40;
				 float c = (float)Math.random()*0.1f;
				 Velocity.setX(a);
				 Velocity.setY(b);
				 engine.addSmoothParticle(Location,Velocity,40f,1,3f,new Color(255,158,0,150));
	 }
	 for(int i = 0;i <= 30; i++)
	{
				 float a = (float)Math.random()*80-40;
				 float b = (float)Math.random()*80-40;
				 float c = (float)Math.random()*0.1f;
				 Velocity.setX(a);
				 Velocity.setY(b);
				 engine.addSmoothParticle(Location,Velocity,15f,1,4f,new Color(255,158,0,150));
	 }

	 List ship = engine.getShips();
	 List Missile = engine.getMissiles();
	 for (int a = 0;a<ship.size() ;a++ )
	 {
		 if (ship.size()== 0)
		 {
			 break;
		 }
		ShipAPI shipn = (ShipAPI)(ship.get(a));
	 	Vector2f shipLocation = shipn.getLocation();
		float Range = (float)Math.sqrt((Location.getX() - shipLocation.getX()) * (Location.getX() - shipLocation.getX()) + (Location.getY() - shipLocation.getY()) * (Location.getY() - shipLocation.getY()));
		float ran = (float)Math.sqrt((targetLocation.getX() - shipLocation.getX()) * (targetLocation.getX() - shipLocation.getX()) + (targetLocation.getY() - shipLocation.getY()) * (targetLocation.getY() - shipLocation.getY()));
		if (Range <= maxrange && ran >=5)
		{
			if ( shipn.isDrone() || shipn.isFighter())
			{
				engine.applyDamage(shipn, shipn.getLocation(), 
					 projectile.getDamageAmount()*(Range/maxrange), DamageType.HIGH_EXPLOSIVE, 0f,
					 false, false, 
					 projectile.getSource());

			}
			else
			{
				damage(projectile, engine, Location, shipn,(Range/maxrange));
			}
		}
	 }
	 for (int a = 0;a<Missile.size();a++ )
	 {
		 if (Missile.size() == 0)
		 {
			 break;
		 }
		MissileAPI Missilen = (MissileAPI)Missile.get(a);
	 	Vector2f MissileLocation = Missilen.getLocation();
		float Range = (float)Math.sqrt((Location.getX() - MissileLocation.getX()) * (Location.getX() - MissileLocation.getX()) + (Location.getY() - MissileLocation.getY()) * (Location.getY() - MissileLocation.getY()));
		if (Range <= maxrange)
		{
			engine.applyDamage(Missilen, MissileLocation, 
					 projectile.getDamageAmount(), DamageType.HIGH_EXPLOSIVE, 0f,
					 false, false, 
					 projectile.getSource());
		}
	 }
  }
  public void damage(DamagingProjectileAPI projectile, CombatEngineAPI engine, Vector2f Location, ShipAPI ship,float mult) 
	{
		float range = 10000f;
		float thisrange = 0f;
		Vector2f shipLocation = ship.getLocation();

		Vector2f hitLocation = new Vector2f(Location.getX(), Location.getY());
		List Segments = ship.getExactBounds().getSegments();
		double xx= 0;
		double yy= 0;
		double x1= 0;
		double y1= 0;
		double x2= 0;
		double y2= 0;
		double x3= 0;
		double y3= 0;
		double x4= 0;
		double y4= 0;
		x1 = (double)Location.getX();
		y1 = (double)Location.getY();
		x2 = (double)shipLocation.getX();
		y2 = (double)shipLocation.getY();
		for(int a=0;a < Segments.size(); a++)
		{
			SegmentAPI ss = (SegmentAPI)Segments.get(a);
			x3 = (double)ss.getP1().getX();
			y3 = (double)ss.getP1().getY();
			x4 = (double)ss.getP2().getX();
			y4 = (double)ss.getP2().getY();
			xx = ((x2 - x1) * (x3 - x4) * (y3 - y1) -  x3 * (x2 - x1) * (y3 - y4) + x1 * (y2 - y1) * (x3 - x4)) / ((y2 - y1) * (x3 - x4) - (x2 - x1) * (y3 - y4));  
			yy = ((y2 - y1) * (y3 - y4) * (x3 - x1) - y3  * (y2 - y1) * (x3 - x4) + y1 * (x2 - x1) * (y3 - y4)) / ((x2 - x1) * (y3 - y4) - (y2 - y1) * (x3 - x4));  

			if(ruln(x3,x4,xx))
			{
				if(ruln(y3,y4,yy))
				{
					thisrange = (float)Math.sqrt((Location.getX() - xx) * (Location.getX() - xx) + (Location.getY() - yy) * (Location.getY() - yy));
					if(thisrange <= range)
					{
						hitLocation.setX((float)xx);
						hitLocation.setY((float)yy);
						range = thisrange;
					}
				}
			}
		}
		//engine.addFloatingText(hitLocation, "HIT!!", 60f, new Color(255,255,255,255), ship, 4f, 4f);
		engine.applyDamage(ship, hitLocation, 
					 projectile.getDamageAmount()*mult, DamageType.HIGH_EXPLOSIVE, 0f,
					 false, false, 
					 projectile.getSource());
	}

	public boolean ruln(double a1, double a2, double b)
	{
		double c = 0f;
		if(a1 > a2)
		{
			c=a1;
			a1=a2;
			a2=c;
		}
		if(a1<= b && a2>=b)
		{
			return true;
		}
		return false;
	}

}