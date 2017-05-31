package data.scripts.imp;

import com.fs.starfarer.api.combat.BoundsAPI.SegmentAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import java.util.List;
import java.util.ArrayList;

public class DMACOnHitEffect
  implements OnHitEffectPlugin
{
  public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine)
  {
	 ShipAPI ships = null; 
	 Vector2f Vel = projectile.getVelocity();
	 Vector2f Velocity = Vel.normalise(Vel);
	 float x = Velocity.getX();
	 float y = Velocity.getY();
	 Vector2f zero = new Vector2f(0f,0f);
	 Vector2f Location = projectile.getLocation();
	 Vector2f targetLocation = target.getLocation();

	 if (((target instanceof ShipAPI)) && (!shieldHit))
	 {
		  for(int i = 0;i <= 30; i++)
			 {
				 float o = (float)Math.random()*80-(float)Math.random()*10+10;
				 float a = o+(float)Math.random()*50-20;
				 float b = o+(float)Math.random()*50-20;
				 float c = (float)Math.random()*25+5;
				 Velocity.setX(-x*a);
				 Velocity.setY(-y*b);
				 engine.addSmoothParticle(Location,Velocity,40f,0.8f,2.5f,new Color(25,100,155,255));
			 }
		 ships = (ShipAPI)target;
 		 float angle = projectile.getFacing();
 		 ArmorGridAPI armor = ships.getArmorGrid();
		 float maxcell = armor.getMaxArmorInCell();
		 float cell = getArmorGridHit(ships, point, angle, engine);
		 if (cell <= 2000 || (cell/maxcell)<=0.15f )
			 {
				 Vector2f hitLocation = cross(projectile, engine, Location, target, 0);
				 Vector2f morehit = new Vector2f(0,0);
				 float xi = (hitLocation.getX()-point.getX())/6f;
				 float yi = (hitLocation.getY()-point.getY())/6f;
				 for (int index = 1;index < 6 ;index++ )
				 {
					 morehit.set((point.getX()+xi*(float)index),(point.getY()+yi*(float)index));
					 engine.applyDamage(target, morehit, projectile.getDamageAmount()*(0.8f-(0.1f*(float)index)), DamageType.HIGH_EXPLOSIVE, 2000f,
							 true, false, 
							 projectile.getSource());
					 for(int i = 0;i <= 30; i++)
					 {
						 float a = (float)Math.random()*80-40;
						 float b = (float)Math.random()*80-40;
						 float c = (float)Math.random()*0.1f;
						 Velocity.setX(a);
						 Velocity.setY(b);
						 engine.addSmoothParticle(morehit,Velocity,20f,0.8f,4f,new Color(25,100,155,155));
					 }

				 }
				 engine.applyDamage(target, hitLocation, 
							 projectile.getDamageAmount()*0.5f, DamageType.KINETIC, 2000f,
							 true, false, 
							 projectile.getSource());
				 for(int i = 0;i <= 30; i++)
				 {
					 float a = (float)Math.random()*80-40;
					 float b = (float)Math.random()*80-40;
					 float c = (float)Math.random()*0.1f;
					 Velocity.setX(a);
					 Velocity.setY(b);
					 engine.addSmoothParticle(hitLocation,Velocity,80f,1,1f,new Color(25,100,155,155));
				 }

				 for(int i = 0;i <= 60; i++)
					 {
						 float o = (float)Math.random()*160-(float)Math.random()*20+10;
						 float a = o+(float)Math.random()*50-20;
						 float b = o+(float)Math.random()*50-20;
						 float c = (float)Math.random()*25+5;
						 Velocity.setX(x*a);
						 Velocity.setY(y*b);
						 engine.addSmoothParticle(hitLocation,Velocity,30f,0.8f,3f,new Color(25,100,155,255));
					 }

				 engine.spawnProjectile(ships, null, "DMAC",
								 hitLocation, angle, target.getVelocity());	
			 }
		 return;

	 }
	 if (((target instanceof ShipAPI)) && (shieldHit))
	 {
		 ships = (ShipAPI)target;
		 Vector2f hitLocation = cross(projectile, engine, Location, target, 1);
		 engine.applyDamage(target, hitLocation, 
					 projectile.getDamageAmount()*0.5f, DamageType.KINETIC, 2000f,
					 true, false, 
					 projectile.getSource());
 		 for(int i = 0;i <= 10; i++)
		 {
		     float a = (float)Math.random()*80-40;
			 float b = (float)Math.random()*80-40;
  			 float c = (float)Math.random()*0.1f;
			 Velocity.setX(a);
			 Velocity.setY(b);
			 engine.addSmoothParticle(Location,Velocity,30f,1,1f,new Color(25,100,155,155));
		 }
		 for(int i = 0;i <= 20; i++)
			 {
				 float a = (float)Math.random()*80-40;
				 float b = (float)Math.random()*80-40;
				 float c = (float)Math.random()*0.1f;
				 Velocity.setX(a);
				 Velocity.setY(b);
				 engine.addHitParticle(Location,Velocity,10f,1,2f,new Color(25,100,155,255));
			 }


		 for(int i = 0;i <= 30; i++)
			 {
				 float o = (float)Math.random()*80-(float)Math.random()*10+10;
				 float a = o+(float)Math.random()*50-20;
				 float b = o+(float)Math.random()*50-20;
				 float c = (float)Math.random()*25+5;
				 Velocity.setX(-x*a);
				 Velocity.setY(-y*b);
				 engine.addSmoothParticle(hitLocation,Velocity,30f,0.8f,3f,new Color(25,100,155,255));
			 }
		 

	 }
	 float angle = projectile.getFacing();
	 engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), "MAC",
			  			 point, angle, target.getVelocity());	
	 return;
  }
  public Vector2f cross(DamagingProjectileAPI projectile, CombatEngineAPI engine, Vector2f Location, CombatEntityAPI ship ,int i) 
	{
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
		float range = 0f;
		if (i == 1)
		{
			range = 999999f;
		}
		float thisrange = 0f;
		Vector2f shipLocation = projectile.getVelocity();
		Vector2f hitLocation = new Vector2f(Location.getX(), Location.getY());
		x1 = (double)Location.getX();
		y1 = (double)Location.getY();
		x2 = (double)Location.getX()+(double)shipLocation.getX();
		y2 = (double)Location.getY()+(double)shipLocation.getY();
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
					if(checkrange(thisrange, range, i))
					{
						hitLocation.setX((float)xx);
						hitLocation.setY((float)yy);
						range = thisrange;
					}
				}
			}
		}
		Vector2f adjust = hitLocation.normalise(new Vector2f((ship.getLocation().getX() - hitLocation.getX()),(ship.getLocation().getY() - hitLocation.getY())));
		adjust.setX(adjust.getX()*3f);
		adjust.setY(adjust.getY()*3f);
		hitLocation.setX(hitLocation.getX()+adjust.getX());
		hitLocation.setY(hitLocation.getY()+adjust.getY());
		return hitLocation;
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
	//getArmorGridHit() code by xangle13
	private float getArmorGridHit(ShipAPI targetShip, Vector2f hitPoint, float hitAngle, CombatEngineAPI engine){
		ArmorGridAPI targetArmorGrid = targetShip.getArmorGrid();
		float armorCellSize = targetArmorGrid.getCellSize();
		float[][] armorCells = targetArmorGrid.getGrid();
		Vector2f targetShipLocation = targetShip.getLocation();
		float targetShipFacing = targetShip.getFacing();
		
		float rangeDivineValue = (float)Math.sqrt((hitPoint.getX() - targetShipLocation.getX()) * (hitPoint.getX() - targetShipLocation.getX()) + (hitPoint.getY() - targetShipLocation.getY()) * (hitPoint.getY() - targetShipLocation.getY()));
		float cellDivineAngle = hitAngle - targetShipFacing;
		float cellDivineX = rangeDivineValue * (float)Math.cos(cellDivineAngle);
		float cellDivineY = rangeDivineValue * (float)Math.sin(cellDivineAngle);
		
		//engine.addFloatingText(new Vector2f(hitPoint.getX(),hitPoint.getY()+30f), "" + (int)cellDivineX+"=="+(int)cellDivineY+"=="+(int)armorCellSize, 30, new Color(255,0,255,255), targetShip, 1f, 2f);
		
		int hitX = 0;
		int hitY = 0;
		boolean cellGet = false;
		for(int i = 0;i < armorCells.length && !cellGet;i++){
			for(int j = 0;j < armorCells[i].length && !cellGet;j++){
				float cellX = ((float)j - (float)armorCells[i].length / 2 + 0.5f) * armorCellSize;
				float cellY = ((float)armorCells.length / 2 - 0.5f - (float)i) * armorCellSize;
				
				//engine.addFloatingText(new Vector2f((cellX + targetShipLocation.getX()),(cellY + targetShipLocation.getY())), "" + (int)targetArmorGrid.getArmorValue(i, j), 20, new Color(0,255,0,155), targetShip, 0.5f, 0.5f);
				
				if(-armorCellSize/2 <= (cellX - cellDivineX) && (cellX - cellDivineX) <= armorCellSize/2){
					if(-armorCellSize/2 <= (cellY - cellDivineY) && (cellY - cellDivineY) <= armorCellSize/2){
						hitX = i;
						hitY = j;
						cellGet = true;
					}
				}
			}
		}
		
		return targetArmorGrid.getArmorValue(hitX, hitY);
	}


	public boolean checkrange(float thisrange,float range, int i)
	{
		if(i==0)
		{
			return (thisrange >= range);
		}
		else
		{
			return (thisrange <= range);
		}
	}

}