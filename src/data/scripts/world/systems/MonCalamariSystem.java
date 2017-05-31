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

public class MonCalamariSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Mon Calamari");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        system.setType(StarSystemGenerator.StarSystemType.BINARY_FAR);
        PlanetAPI daca1 = system.initStar("daca1", StarTypes.ORANGE_GIANT, 1000f, 700);
        daca1.setName("Daca");
        PlanetAPI daca2 = system.addPlanet("daca2", daca1, "Daca II", StarTypes.BROWN_DWARF, 100, 500, 2500, 900);
        system.setSecondary(daca2);
        system.addCorona(daca2, 350, 2f, 0f, 1f);
        system.setLightColor(new Color(255, 250, 250));
        
        system.getLocation().set(SW_SystemCalculator.calcX(13608), SW_SystemCalculator.calcY(4951));

        PlanetAPI dac = system.addPlanet("Dac", daca1, "Dac", SW_PlanetTypes.MON_CALAMARI, 200, 110, 6000, 398);

        dac.setFaction("rebelalliance");
        SW_AddMarket.SW_AddMarket("rebelalliance",
                dac,
                null,
                "Mon Calamari Shipyards",
                7,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.SPACEPORT,
                        Conditions.AQUACULTURE,
                        Conditions.POPULATION_9,
                        Conditions.MILITARY_BASE,
                        Conditions.WATER_SURFACE,
                        Conditions.SHIPBREAKING_CENTER)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_OPEN)),
                0.3f
        );

        JumpPointAPI dacJumpPoint = Global.getFactory().createJumpPoint("DacJumpPoint", "Dac Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(daca1, 150, 7500, 398);
        dacJumpPoint.setOrbit(orbit);
        dacJumpPoint.setRelatedPlanet(dac);
        dacJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(dacJumpPoint);

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
