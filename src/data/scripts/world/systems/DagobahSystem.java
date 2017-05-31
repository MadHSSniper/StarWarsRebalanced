package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.SW_AddMarket;
import data.scripts.world.PlanetTypes;
import data.scripts.world.SW_SystemCalculator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class DagobahSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Dagobah");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        PlanetAPI star = system.initStar("dagobahStar", StarTypes.YELLOW, 450f, 300);
        system.setLightColor(new Color(255, 250, 250));

        system.getLocation().set(SW_SystemCalculator.calcX(2460), SW_SystemCalculator.calcY(-14267));//1600;-9500

        PlanetAPI ness = system.addPlanet("NessPlanet", star, "Ness", PlanetTypes.VOLCANIC, 0, 72, 2000, 300);
        
        PlanetAPI dagobahPlanet = system.addPlanet("DagobahPlanet", star, "Dagobah", "dagobah", 90, 144, 4000, 341);
        dagobahPlanet.setFaction(Factions.INDEPENDENT);
        SW_AddMarket.SW_AddMarket(Factions.INDEPENDENT,
                dagobahPlanet,
                null,
                "Wandering Traders",
                1,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        Conditions.DENSE_ATMOSPHERE,
                        Conditions.POPULATION_1)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.2f
        );
        
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("DagobahJumpPoint", "Dagobah Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 60, 4000, 341);
        jumpPoint1.setOrbit(orbit);
        jumpPoint1.setRelatedPlanet(dagobahPlanet);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint1);

        PlanetAPI undar = system.addPlanet("UndarPlanet", star, "Undar", PlanetTypes.BARREN_BOMBARDED, 270, 36, 6000, 380);
        PlanetAPI bubbok = system.addPlanet("BubbokPlanet", star, "Bubbok", PlanetTypes.ROCKY_ICE, 120, 54, 8000, 420);
        PlanetAPI sty = system.addPlanet("StyPlanet", star, "Sty", PlanetTypes.GAS_GIANT, 180, 200, 10000, 500);
        system.addRingBand(sty, "misc", "rings_special0", 256f, 0, Color.GRAY, 256, 600, 40f);

        system.autogenerateHyperspaceJumpPoints(true, true, true);

        cleanup(system);
    }

    void cleanup(StarSystemAPI system) {
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
    }
}
