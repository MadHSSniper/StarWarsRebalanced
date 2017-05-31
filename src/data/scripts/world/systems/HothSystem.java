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
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.SW_AddMarket;
import data.scripts.world.SW_PlanetTypes;
import data.scripts.world.PlanetTypes;
import data.scripts.world.SW_SystemCalculator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class HothSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Hoth");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        PlanetAPI star = system.initStar("hothStar", StarTypes.BLUE_SUPERGIANT, 1200f, 800);
        system.setLightColor(new Color(255, 250, 250));

        system.getLocation().set(SW_SystemCalculator.calcX(-1421), SW_SystemCalculator.calcY(-12880));//-1000;-8600

        PlanetAPI shron = system.addPlanet("ShronPlanet", star, "Shron", PlanetTypes.VOLCANIC, 300, 50, 3000, 100);
        PlanetAPI biosh = system.addPlanet("BioshPlanet", star, "Biosh", PlanetTypes.TOXIC, 240, 30, 5000, 150);
        PlanetAPI nushk = system.addPlanet("NushkPlanet", star, "Nushk", PlanetTypes.ROCKY_METALLIC, 120, 40, 7000, 200);
        PlanetAPI jhas = system.addPlanet("JhasPlanet", star, "Jhas", PlanetTypes.ICE_GIANT, 180, 320, 12000, 300);
        PlanetAPI ordaj = system.addPlanet("OrdajPlanet", star, "Ordaj", PlanetTypes.GAS_GIANT, 0, 250, 16000, 400);

        PlanetAPI hothPlanet = system.addPlanet("HothPlanet", star, "Hoth", SW_PlanetTypes.HOTH, 25, 72, 20000, 526);
        system.addAsteroidBelt(hothPlanet, 20, 600, 100, 500, 600);
        system.addRingBand(hothPlanet, "misc", "rings_asteroids0", 256f, 0, Color.white, 120f, 600, 550f);
        hothPlanet.setFaction("rebelalliance");
        SW_AddMarket.SW_AddMarket("rebelalliance",
                hothPlanet,
                null,
                "Rebel Base",
                5,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        //Conditions.HEADQUARTERS,
                        Conditions.FARMLAND_POOR,
                        Conditions.SPACEPORT,
                        Conditions.ORE_COMPLEX,
                        Conditions.MILITARY_BASE,
                        Conditions.LIGHT_INDUSTRIAL_COMPLEX,
                        Conditions.POPULATION_3,
                        Conditions.COLD,
                        Conditions.METEOR_IMPACTS,
                        Conditions.OUTPOST)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.GENERIC_MILITARY)),
                0.3f
        );

        JumpPointAPI hothJumpPoint = Global.getFactory().createJumpPoint("HothJumpPoint", "Hoth Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 15, 23000, 526);
        hothJumpPoint.setOrbit(orbit);
        hothJumpPoint.setRelatedPlanet(hothPlanet);
        hothJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(hothJumpPoint);

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
