/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.missions.swr_hansretreat;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

/**
 *
 * @author MadHSSniper
 */
public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        //Setting up fleets
        //One retreating and other attacking
        api.initFleet(FleetSide.PLAYER, "", FleetGoal.ESCAPE, false);
        api.initFleet(FleetSide.ENEMY, "INS", FleetGoal.ATTACK, true);

        // Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "Han Solo");
        api.setFleetTagline(FleetSide.ENEMY, "Imperial Pursuit Fleet");

        // These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("Retreat or fight your way out of this mess.");

        // Set up the player's fleet. Use "variant" names
        api.addToFleet(FleetSide.PLAYER, "millenium_falcon", FleetMemberType.SHIP, "Millenium Falcon", true);

        api.addToFleet(FleetSide.ENEMY, "mki_vic", FleetMemberType.SHIP, "Vigilant", false);
        api.addToFleet(FleetSide.ENEMY, "mki_vic", FleetMemberType.SHIP, "Victorious", false);
        api.addToFleet(FleetSide.ENEMY, "mkii_vic", FleetMemberType.SHIP, "Defender", false);
        api.addToFleet(FleetSide.ENEMY, "acclamator_mkii", FleetMemberType.SHIP, "Guardian", false);
        api.addToFleet(FleetSide.ENEMY, "carrack_light", FleetMemberType.SHIP, "Knight", false);
        api.addToFleet(FleetSide.ENEMY, "carrack_light", FleetMemberType.SHIP, "Crusader", false);
        api.addToFleet(FleetSide.ENEMY, "fighter_control", FleetMemberType.SHIP, "Gladiator", false);
        api.addToFleet(FleetSide.ENEMY, "fighter_control", FleetMemberType.SHIP, "Spearhead", false);
        api.addToFleet(FleetSide.ENEMY, "strike_med", FleetMemberType.SHIP, "Lancer", false);
        api.addToFleet(FleetSide.ENEMY, "strike_med", FleetMemberType.SHIP, "Striker", false);

        // Set up the map.
        // 12000x8000 is actually somewhat small, making for a faster-paced mission.
        float width = 16000f;
        float height = 24000f;
        api.initMap((float) -width / 2f, (float) width / 2f, (float) -height / 2f, (float) height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        // All the addXXX methods take a pair of coordinates followed by data for
        // whatever object is being added.
        // Add two big nebula clouds
        api.addNebula(minX + width * 0.65f, minY + height * 0.35f, 1800);
        api.addNebula(minX + width * 0.35f, minY + height * 0.65f, 1200);

        // And a few random ones to spice up the playing field.
        // A similar approach can be used to randomize everything
        // else, including fleet composition.
        for (int i = 0; i < 8; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 400f;
            api.addNebula(x, y, radius);
        }

        // Add objectives. These can be captured by each side
        // and provide stat bonuses and extra command points to
        // bring in reinforcements.
        // Reinforcements only matter for large fleets - in this
        // case, assuming a 100 command point battle size,
        // both fleets will be able to deploy fully right away.
        /*api.addObjective(minX + width * 0.75f, minY + height * 0.25f, "sensor_array");
        api.addObjective(minX + width * 0.75f, minY + height * 0.75f, "nav_buoy");
        api.addObjective(minX + width * 0.5f, minY + height * 0.6f, "comm_relay");
        api.addObjective(minX + width * 0.25f, minY + height * 0.75f, "sensor_array");
        api.addObjective(minX + width * 0.25f, minY + height * 0.25f, "nav_buoy");
         */
    }
}
