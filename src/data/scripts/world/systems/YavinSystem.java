package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.SW_AddMarket;
import data.scripts.world.SW_PlanetTypes;
import data.scripts.world.SW_SystemCalculator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class YavinSystem implements SectorGeneratorPlugin {
    
    @Override
    public void generate(SectorAPI sector) {
        
        StarSystemAPI system = sector.createStarSystem("Yavin");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
        
        PlanetAPI yavin = system.initStar("yavinStar", StarTypes.ORANGE, 600f, 400);
        system.setLightColor(new Color(255, 250, 250));
        
        system.getLocation().set(SW_SystemCalculator.calcX(6875), SW_SystemCalculator.calcY(5980)); //4600;4000

        PlanetAPI yavinGas = system.addPlanet("YavinGas", yavin, "Yavin", SW_PlanetTypes.YAVIN_GAS, 50, 600, 7000, 360); //yavin_gas custom planet type
        
        PlanetAPI yavin4 = system.addPlanet("Yavin4", yavinGas, "Yavin 4", SW_PlanetTypes.YAVIN, 0, 102, 1500, 481);
        yavin4.setFaction("rebelalliance");
        SW_AddMarket.SW_AddMarket("rebelalliance",
                yavin4,
                null,
                "Rebel Alliance Headquarters",
                8,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.SPACEPORT,
                        Conditions.POPULATION_5,
                        Conditions.MILITARY_BASE,
                        Conditions.HEADQUARTERS,
                        Conditions.REGIONAL_CAPITAL,
                        Conditions.FARMLAND_ADEQUATE)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_OPEN,
                        Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.GENERIC_MILITARY)),
                0.3f
        );
        
        JumpPointAPI yavin4JumpPoint = Global.getFactory().createJumpPoint("Yavin4JumpPoint", "Yavin 4 Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(yavinGas, 35, 1500, 481);
        yavin4JumpPoint.setOrbit(orbit);
        yavin4JumpPoint.setRelatedPlanet(yavin4);
        yavin4JumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(yavin4JumpPoint);
        
        system.addRingBand(yavin, "misc", "rings_dust0", 256f, 0, Color.white, 300f, 3000, 1000f);
        system.addRingBand(yavin, "misc", "rings_dust0", 256f, 0, Color.white, 300f, 3000, 1100f);
        system.addRingBand(yavin, "misc", "rings_dust0", 256f, 0, Color.white, 300f, 3000, 1050f);
        system.addAsteroidBelt(yavinGas, 100, 3000, 256, 1000, 1100);
        
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
