package data.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.systems.*;

@SuppressWarnings("unchecked")
public class StarWarsGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        //sector generation code
        new CoruscantSystem().generate(sector);     //Imperial HQ
        new HothSystem().generate(sector);          //Rebel Base
        new TatooineSystem().generate(sector);      //Story related
        new DagobahSystem().generate(sector);       //Story related
        new EndorSystem().generate(sector);         //Imperial outpost
        new YavinSystem().generate(sector);         //Rebel HQ
        new MonCalamariSystem().generate(sector);   //Mon Calamari

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("galacticempire");
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("rebelalliance");
        
        setVanillaRelationships(sector);
    }

    private void setVanillaRelationships(SectorAPI sector) {

        FactionAPI galacticempire = sector.getFaction("galacticempire");
        FactionAPI rebelalliance = sector.getFaction("rebelalliance");
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI persean = sector.getFaction(Factions.PERSEAN);

        galacticempire.setRelationship(rebelalliance.getId(), -0.75f);
        galacticempire.setRelationship(player.getId(), -0.05f); //-0.05f
        galacticempire.setRelationship(hegemony.getId(), 0.5f);
        galacticempire.setRelationship(tritachyon.getId(), 0.15f);
        galacticempire.setRelationship(pirates.getId(), -0.75f);
        galacticempire.setRelationship(independent.getId(), -0.2f);
        galacticempire.setRelationship(church.getId(), -0.4f);
        galacticempire.setRelationship(path.getId(), -0.7f);
        galacticempire.setRelationship(diktat.getId(), -0.45f);
        galacticempire.setRelationship(persean.getId(), -0.4f);

        rebelalliance.setRelationship(galacticempire.getId(), -0.75f);
        rebelalliance.setRelationship(player.getId(), 0.1f); //0.1f
        rebelalliance.setRelationship(hegemony.getId(), -0.5f);
        rebelalliance.setRelationship(tritachyon.getId(), -0.1f);
        rebelalliance.setRelationship(pirates.getId(), 0.1f);
        rebelalliance.setRelationship(independent.getId(), 0.25f);
        rebelalliance.setRelationship(church.getId(), -0.4f);
        rebelalliance.setRelationship(path.getId(), -0.7f);
        rebelalliance.setRelationship(diktat.getId(), -0.45f);
        rebelalliance.setRelationship(persean.getId(), 0.3f);

        setModRelationships(galacticempire);
        setModRelationships(rebelalliance);
    }

    private void setModRelationships(FactionAPI faction) {
        //same generic relationship between most modded factions
        faction.setRelationship("exigency", -0.2f);
        faction.setRelationship("shadow_industry", -0.7f);
        faction.setRelationship("mayorate", -0.3f);
        faction.setRelationship("blackrock", 0.05f);
        faction.setRelationship("tiandong", 0.2f);
        faction.setRelationship("citadel", 0.1f);
        faction.setRelationship("SCY", 0.1f);
        faction.setRelationship("ORA", -0.25f);
        faction.setRelationship("neutrinocorp", -0.1f);
        faction.setRelationship("interstellarimperium", -0.4f);
        faction.setRelationship("diableavionics", 0.01f);
    }
}
