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
import data.scripts.world.PlanetTypes;
import data.scripts.world.SW_SystemCalculator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class EndorSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Endor");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        system.setType(StarSystemGenerator.StarSystemType.BINARY_CLOSE);
        PlanetAPI endor1 = system.initStar("endor1", StarTypes.WHITE_DWARF, 300f, 200);
        endor1.setName("Endor I");
        PlanetAPI endor2 = system.addPlanet("endor2", endor1, "Endor II", StarTypes.RED_DWARF, 10, 300, 2000, 1000);
        system.setSecondary(endor2);
        system.addCorona(endor2, 200, 2f, 0f, 1f);
        system.setLightColor(new Color(255, 250, 250));
        
        system.getLocation().set(SW_SystemCalculator.calcX(-5148), SW_SystemCalculator.calcY(-10245)); //-2400;-6800

        PlanetAPI endorGas = system.addPlanet("EndorPlanet", endor1, "Endor", PlanetTypes.ICE_GIANT, 25, 300, 5000, 256);
        PlanetAPI endor = system.addPlanet("EndorMoon", endorGas, "Endor", SW_PlanetTypes.ENDOR, 0, 49, 900, 402);

        endor.setFaction("galacticempire");
        SW_AddMarket.SW_AddMarket("galacticempire",
                endor,
                null,
                "Imperial Outpost",
                4,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.SPACEPORT,
                        Conditions.LIGHT_INDUSTRIAL_COMPLEX,
                        Conditions.POPULATION_3,
                        Conditions.MILITARY_BASE,
                        Conditions.OUTPOST)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.GENERIC_MILITARY)),
                0.35f
        );

        JumpPointAPI endorJumpPoint = Global.getFactory().createJumpPoint("EndorJumpPoint", "Endor Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(endorGas, 120, 800, 450);
        endorJumpPoint.setOrbit(orbit);
        endorJumpPoint.setRelatedPlanet(endor);
        endorJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(endorJumpPoint);

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
