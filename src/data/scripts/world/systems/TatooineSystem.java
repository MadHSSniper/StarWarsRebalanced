package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventManagerAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventPlugin;
import com.fs.starfarer.api.campaign.events.CampaignEventTarget;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Events;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.SWStoryline;
import data.scripts.world.SW_AddMarket;
import data.scripts.world.SW_PlanetTypes;
import data.scripts.world.PlanetTypes;
import data.scripts.world.SW_SystemCalculator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class TatooineSystem implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Tatooine");
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        system.setType(StarSystemGenerator.StarSystemType.BINARY_CLOSE);
        PlanetAPI tatoo1 = system.initStar("tatoo1", StarTypes.YELLOW, 400f, 300);
        tatoo1.setName("Tatoo I");

        PlanetAPI tatoo2 = system.addPlanet("tatoo2", tatoo1, "Tatoo II", StarTypes.YELLOW, 10, 300, 2000, 1000);
        system.setSecondary(tatoo2);
        system.addCorona(tatoo2, 200, 2f, 0f, 1f);
        system.setLightColor(new Color(255, 250, 250));

        system.getLocation().set(SW_SystemCalculator.calcX(9665), SW_SystemCalculator.calcY(-10099));//6400;-6700

        PlanetAPI tatooine = system.addPlanet("TatooinePlanet", tatoo1, "Tatooine", SW_PlanetTypes.TATOOINE, 25, 104, 4000, 304);
        tatooine.setFaction(Factions.INDEPENDENT);
        SW_AddMarket.SW_AddMarket(Factions.INDEPENDENT,
                tatooine,
                null,
                "Mos Eisley",
                6,
                new ArrayList<>(Arrays.asList(Conditions.HABITABLE,
                        Conditions.FARMLAND_POOR,
                        Conditions.RARE_ORE_MODERATE,
                        Conditions.SPACEPORT,
                        Conditions.ORE_COMPLEX,
                        Conditions.VOLATILES_COMPLEX,
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.POPULATION_4,
                        Conditions.HOT,
                        Conditions.ORGANIZED_CRIME,
                        Conditions.ARID)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.25f
        );

        //SWStoryline event = new SWStoryline();
        CampaignEventPlugin event = (CampaignEventPlugin) new SWStoryline();
                
        CampaignEventManagerAPI eventManager = Global.getSector().getEventManager();
        CampaignEventTarget target = new CampaignEventTarget(tatooine);
        target.setExtra(Misc.genUID());
        
        //event = (SWStoryline) eventManager.primeEvent(target, "SWStoryline", this);
        event = eventManager.primeEvent(target, "SWStoryline", this);
        eventManager.startEvent(event);

        
        JumpPointAPI tatooineJumpPoint = Global.getFactory().createJumpPoint("TatooineJumpPoint", "Tatooine Jump-Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(tatoo1, 70, 4000, 304);
        tatooineJumpPoint.setOrbit(orbit);
        tatooineJumpPoint.setRelatedPlanet(tatooine);
        tatooineJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(tatooineJumpPoint);

        PlanetAPI ohann = system.addPlanet("OhannPlanet", tatoo1, "Ohann", PlanetTypes.ICE_GIANT, 130, 280, 7500, 400);
        PlanetAPI adriana = system.addPlanet("AdrianaPlanet", tatoo1, "Adriana", PlanetTypes.GAS_GIANT, 290, 200, 10000, 500);

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
