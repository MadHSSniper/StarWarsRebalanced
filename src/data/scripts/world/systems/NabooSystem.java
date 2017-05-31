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
import data.scripts.world.SW_SystemCalculator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class NabooSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Naboo");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        PlanetAPI star = system.initStar("nabooStar", StarTypes.YELLOW, 400f, 200);
        star.setName("Naboo");
        system.setLightColor(new Color(255, 250, 250));

        system.getLocation().set(SW_SystemCalculator.calcX(5016), SW_SystemCalculator.calcY(-10608));

        PlanetAPI naboo = system.addPlanet("NabooPlanet", star, "Naboo", "coruscant", 25, 122, 6000, 365);
        naboo.setFaction("galacticempire");
        SW_AddMarket.SW_AddMarket("galacticempire",
                naboo,
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

        JumpPointAPI nabooJumpPoint = Global.getFactory().createJumpPoint("NabooJumpPoint", "Naboo Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 42, 6000, 365);
        nabooJumpPoint.setOrbit(orbit);
        nabooJumpPoint.setRelatedPlanet(naboo);
        nabooJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(nabooJumpPoint);

        StarSystemGenerator.addOrbitingEntities(system, star, StarAge.OLD, 5, 5, 1000, 1, false);

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
