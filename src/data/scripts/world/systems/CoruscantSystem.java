package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
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
import data.scripts.world.SW_SystemCalculator;
import data.scripts.world.PlanetTypes;
import data.scripts.world.SW_PlanetTypes;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class CoruscantSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Coruscant");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        PlanetAPI star = system.initStar("coruscantStar", StarTypes.BLUE_GIANT, 900f, 600);
        star.setName("Coruscant Prime");
        system.setLightColor(new Color(255, 250, 250));
        
        system.getLocation().set(SW_SystemCalculator.getRootX(), SW_SystemCalculator.getRootY());

        PlanetAPI revisse = system.addPlanet("Revisse", star, "Revisse", PlanetTypes.VOLCANIC, 0, 60, 2500, 60);
        PlanetAPI platoril = system.addPlanet("Platoril", star, "Platoril", PlanetTypes.BARREN_2, 90, 50, 4500, 130);
        PlanetAPI vandor1 = system.addPlanet("vandor1", star, "Vandor-1", PlanetTypes.BARREN_3, 270, 65, 6500, 200);
        PlanetAPI vandor2 = system.addPlanet("vandor2", star, "Vandor-2", PlanetTypes.BARREN_2, 180, 60, 8500, 270);
        PlanetAPI vandor3 = system.addPlanet("vandor3", star, "Vandor-3", PlanetTypes.TERRAN, 70, 90, 10500, 340);
        
        PlanetAPI coruscant = system.addPlanet("CoruscantPlanet", star, "Coruscant", SW_PlanetTypes.CORUSCANT, 25, 122, 12500, 365);
        coruscant.setFaction("galacticempire");
        SW_AddMarket.SW_AddMarket("galacticempire",
                coruscant,
                null,//new ArrayList<>(Arrays.asList(Global.getSector().getStarSystem("Coruscant").getEntityById("CoruscantPlanet"))),
                "Coruscant Trade District",
                10,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.MILITARY_BASE,
                        Conditions.SPACEPORT,
                        Conditions.TRADE_CENTER,
                        Conditions.HEADQUARTERS,
                        Conditions.REGIONAL_CAPITAL,
                        Conditions.POPULATION_10,
                        Conditions.HYDROPONICS_COMPLEX)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.GENERIC_MILITARY)),
                0.4f
        );

        JumpPointAPI coruscantJumpPoint = Global.getFactory().createJumpPoint("CoruscantJumpPoint", "Coruscant Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 42, 12000, 365);
        coruscantJumpPoint.setOrbit(orbit);
        coruscantJumpPoint.setRelatedPlanet(coruscant);
        coruscantJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(coruscantJumpPoint);

        PlanetAPI muscave = system.addPlanet("muscave", star, "Muscave", PlanetTypes.GAS_GIANT, 320, 300, 14500, 420);
        PlanetAPI muscave1 = system.addPlanet("muscave1", muscave, "Muscave-1", PlanetTypes.BARREN, 30, 100, 1000, 240);
        PlanetAPI stentat = system.addPlanet("stentat", star, "Stentat", PlanetTypes.ICE_GIANT, 20, 240, 16500, 490);
        PlanetAPI improcco = system.addPlanet("improcco", star, "Improcco", PlanetTypes.BARREN, 210, 100, 18500, 560);
        system.addRingBand(star, "misc", "rings_asteroids0", 256f, 3, Color.white, 300f, 20500, 550f);
        system.addAsteroidBelt(star, 250, 20500, 250, 500, 600);
        PlanetAPI nabatu = system.addPlanet("nabatu", star, "Nabatu", PlanetTypes.BARREN_3, 130, 90, 22500, 670);
        PlanetAPI ulabos = system.addPlanet("ulabos", star, "Ulabos", PlanetTypes.ROCKY_ICE, 12, 60, 24500, 740);
        
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
