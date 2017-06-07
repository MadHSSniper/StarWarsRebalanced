package data.scripts.campaign;

import java.lang.String;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseOnMessageDeliveryScript;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CommDirectoryEntryAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OnMessageDeliveryScript;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.comm.MessagePriority;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventManagerAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventTarget;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.JumpPointInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.events.BaseEventPlugin;
import static com.fs.starfarer.api.impl.campaign.events.BaseEventPlugin.addPersonTokens;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Events;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.impl.campaign.tutorial.CampaignTutorialScript;
import com.fs.starfarer.api.impl.campaign.tutorial.RogueMinerMiscFleetManager;
import com.fs.starfarer.api.impl.campaign.tutorial.SaveNagScript;
import com.fs.starfarer.api.impl.campaign.tutorial.TutorialLeashAssignmentAI;
import data.scripts.campaign.SWStoryline.SWStorylineStage;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.HintPanelAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SWStoryline extends BaseEventPlugin {

    public static final String SWS_STAGE = "$sws_stage";

    public static final String REASON = "SW_Story";

    public static enum SWStorylineStage {
        CONTACT,
        GO_GET_DATA,
        GOT_DATA,
        GO_GET_AI_CORE,
        GOT_AI_CORE,
        GO_RECOVER_SHIPS,
        RECOVERED_SHIPS,
        GO_STABILIZE,
        STABILIZED,
        DELIVER_REPORT,
        DONE,;
    }

    protected float elapsedDays = 0;
    protected boolean ended = false;

    //Planets
    protected StarSystemAPI tatooineSystem;
    protected StarSystemAPI yavinSystem;
    protected PlanetAPI tatooine;
    protected PlanetAPI yavin;

    protected PlanetAPI pontus;
    protected PlanetAPI tetra;

    //
    protected SectorEntityToken derinkuyu;
    protected SectorEntityToken probe;
    protected SectorEntityToken inner;
    protected SectorEntityToken fringe;
    protected SectorEntityToken detachment;
    protected SectorEntityToken relay;

    //Persons
    protected PersonAPI mainContact;
    protected PersonAPI rebelContact;
    protected PersonAPI swampContact;

    protected SWStorylineStage stage = SWStorylineStage.CONTACT;

    @Override
    public void init(String type, CampaignEventTarget eventTarget) {
        super.init(type, eventTarget, false);
    }

    @Override
    public void setParam(Object param) {
        //data = (SWStorylineStageData) param;
    }

    @Override
    public void startEvent() {
        super.startEvent();

        tatooineSystem = Global.getSector().getStarSystem("Tatooine");
        yavinSystem = Global.getSector().getStarSystem("Yavin");
        tatooine = (PlanetAPI) tatooineSystem.getEntityById("TatooinePlanet");
        yavin = (PlanetAPI) yavinSystem.getEntityById("Yavin4");

        mainContact = createMainContact(tatooine);
        rebelContact = createRebelContact(yavin);

        String stageId = "start";
        Global.getSector().reportEventStage(this, stageId, Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tatooine));

        mainContact.getMemoryWithoutUpdate().set("$sws_mainContact", true);
        mainContact.getMemoryWithoutUpdate().set("$sws_eventRef", this);
        Misc.makeImportant(mainContact, REASON);

        updateStage(SWStorylineStage.CONTACT);
    }

    public static PersonAPI createMainContact(PlanetAPI planet) {
        PersonAPI contact = planet.getFaction().createRandomPerson(); //base char with all random data
        FullName name = new FullName("Han", "Solo", FullName.Gender.MALE);

        //Set custom data
        contact.setName(name);
        contact.setFaction(planet.getFaction().getId());
        contact.setRankId(Ranks.SPACE_CAPTAIN);
        contact.setPostId(Ranks.POST_SMUGGLER);
        
        contact.setPortraitSprite("graphics/SWRebalanced/portraits/han_solo.png");
        
        planet.getMarket().getCommDirectory().addPerson(contact);
        planet.getMarket().addPerson(contact);

        return contact;
    }

    public PersonAPI getMainContact() {
        return mainContact;
    }

    public static PersonAPI createRebelContact(PlanetAPI planet) {
        PersonAPI contact = planet.getFaction().createRandomPerson(); //base char with all random data
        FullName name = new FullName("Mon", "Mothma", FullName.Gender.FEMALE);

        //Set custom data
        contact.setName(name);
        contact.setFaction(planet.getFaction().getId());
        contact.setRankId(Ranks.CITIZEN);
        contact.setPostId(Ranks.POST_BASE_COMMANDER);
        contact.setPortraitSprite("graphics/SWRebalanced/portraits/mon_mathma.png");

        planet.getMarket().getCommDirectory().addPerson(contact);

        return contact;
    }

    public PersonAPI getRebelContact() {
        return rebelContact;
    }

    public static PersonAPI createSwampContact(PlanetAPI planet) {
        PersonAPI contact = planet.getFaction().createRandomPerson(); //base char with all random data
        FullName name = new FullName("Master", "Yoda", FullName.Gender.MALE);

        //Set custom data
        contact.setName(name);
        contact.setFaction(planet.getFaction().getId());
        contact.setRankId(Ranks.CITIZEN);
        contact.setPostId(Ranks.POST_CITIZEN);
        contact.setPortraitSprite("graphics/SWRebalanced/portraits/master_yoda.png");

        planet.getMarket().getCommDirectory().addPerson(contact);

        return contact;
    }

    public PersonAPI getSwampContact() {
        return swampContact;
    }

    protected void updateStage(SWStorylineStage stage) {
        this.stage = stage;
        Global.getSector().getMemoryWithoutUpdate().set(SWS_STAGE, stage.name());
    }

    protected void endEvent() {
        ended = true;
        Global.getSector().getMemoryWithoutUpdate().unset(SWS_STAGE);
    }

    @Override
    public void advance(float amount) {
        if (!isEventStarted()) {
            return;
        }
        if (isDone()) {
            return;
        }

        float days = Global.getSector().getClock().convertToDays(amount);

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) {
            return;
        }

        //memory.advance(days);
        elapsedDays += days;

        if (probe == null) {
            probe = tatooineSystem.getEntityById("galatia_probe");
        }
        if (tetra == null) {
            tetra = (PlanetAPI) tatooineSystem.getEntityById("tetra");
        }
        if (derinkuyu == null) {
            derinkuyu = tatooineSystem.getEntityById("derinkuyu_station");
        }
        if (inner == null) {
            inner = tatooineSystem.getEntityById("galatia_jump_point_alpha");
        }
        if (fringe == null) {
            fringe = tatooineSystem.getEntityById("galatia_jump_point_fringe");
        }
        if (detachment == null) {
            detachment = tatooineSystem.getEntityById("tutorial_security_detachment");
        }

        if (stage == SWStorylineStage.GO_GET_AI_CORE) {
            int cores = (int) player.getCargo().getCommodityQuantity(Commodities.GAMMA_CORE);
            float distToProbe = Misc.getDistance(player.getLocation(), probe.getLocation());
            if (cores > 0 && (!probe.isAlive() || distToProbe < 300)) {
                Global.getSector().reportEventStage(this, "salvage_core_end", Global.getSector().getPlayerFleet(),
                        MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tatooine));
                Misc.makeImportant(mainContact, REASON);
                updateStage(SWStorylineStage.GOT_AI_CORE);
            }
        }

        if (stage == SWStorylineStage.GO_RECOVER_SHIPS) {
            int count = 0;
            for (FleetMemberAPI member : player.getFleetData().getMembersListCopy()) {
                //if (member.getVariant().getHullSpec().isDHull()) count++;
                count++;
            }

            int wrecks = 0;
            for (SectorEntityToken entity : tatooineSystem.getEntitiesWithTag(Tags.SALVAGEABLE)) {
                String id = entity.getCustomEntityType();
                if (id == null) {
                    continue;
                }
                if (Entities.WRECK.equals(id)) {
                    wrecks++;
                }
            }

            if (count >= 5 || wrecks < 3) {
                Global.getSector().reportEventStage(this, "ship_recovery_end", Global.getSector().getPlayerFleet(),
                        MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tatooine));
                Misc.makeImportant(mainContact, REASON);
                Misc.makeUnimportant(tetra, REASON);
                updateStage(SWStorylineStage.RECOVERED_SHIPS);
            }
        }

        if (stage == SWStorylineStage.GO_STABILIZE) {
            boolean innerStable = inner.getMemoryWithoutUpdate().getExpire(JumpPointInteractionDialogPluginImpl.UNSTABLE_KEY) > 0;
            boolean fringeStable = fringe.getMemoryWithoutUpdate().getExpire(JumpPointInteractionDialogPluginImpl.UNSTABLE_KEY) > 0;

            if (innerStable || fringeStable) {
                Global.getSector().reportEventStage(this, "stabilize_jump_point_done", Global.getSector().getPlayerFleet(),
                        MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tatooine));
                Misc.makeImportant(mainContact, REASON);
                Misc.makeUnimportant(inner, REASON);
                updateStage(SWStorylineStage.STABILIZED);
            }

        }

    }

    @Override
    public boolean callEvent(String ruleId, final InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = params.get(0).getString(memoryMap);

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        CargoAPI cargo = playerFleet.getCargo();

        if (action.equals("startGetData")) {
            Global.getSector().reportEventStage(this, "sneak_start", Global.getSector().getPlayerFleet(),
                    MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(derinkuyu));

            rebelContact.getMemoryWithoutUpdate().set("$sws_rebelContact", true);
            rebelContact.getMemoryWithoutUpdate().set("$sws_eventRef", this);
            Misc.makeImportant(rebelContact, REASON);
            Misc.makeUnimportant(mainContact, REASON);

            detachment.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_ALLOW_TOFF, true);

            updateStage(SWStorylineStage.GO_GET_DATA);

            saveNag();
        } else if (action.equals("endGetData")) {

            Global.getSector().reportEventStage(this, "sneak_end", Global.getSector().getPlayerFleet(),
                    MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tatooine));
            Misc.cleanUpMissionMemory(rebelContact.getMemoryWithoutUpdate(), "tut_");

            Misc.makeUnimportant(rebelContact, REASON);
            Misc.makeImportant(mainContact, REASON);

            updateStage(SWStorylineStage.GOT_DATA);

        } else if (action.equals("goSalvage")) {
            Global.getSector().reportEventStage(this, "salvage_core_start", Global.getSector().getPlayerFleet(),
                    MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(pontus));
            Misc.makeUnimportant(mainContact, REASON);
            Misc.makeImportant(probe, REASON);

            updateStage(SWStorylineStage.GO_GET_AI_CORE);

            saveNag();
        } else if (action.equals("goRecover")) {
            Global.getSector().reportEventStage(this, "ship_recovery_start", Global.getSector().getPlayerFleet(),
                    MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tetra));
            Misc.makeUnimportant(mainContact, REASON);
            Misc.makeImportant(tetra, REASON);

            FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "mudskipper_Standard");
            playerFleet.getFleetData().addFleetMember(member);
            AddRemoveCommodity.addFleetMemberGainText(member, dialog.getTextPanel());

            updateStage(SWStorylineStage.GO_RECOVER_SHIPS);
        } else if (action.equals("goStabilize")) {
            Global.getSector().reportEventStage(this, "stabilize_jump_point", Global.getSector().getPlayerFleet(),
                    MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(inner));
            Misc.makeUnimportant(mainContact, REASON);
            Misc.makeImportant(inner, REASON);

            addWeaponsToStorage();

            inner.getMemoryWithoutUpdate().set(JumpPointInteractionDialogPluginImpl.CAN_STABILIZE, true);
            fringe.getMemoryWithoutUpdate().set(JumpPointInteractionDialogPluginImpl.CAN_STABILIZE, true);

            updateStage(SWStorylineStage.GO_STABILIZE);

            saveNag();
        } else if (action.equals("reportDelivered")) {
            Global.getSector().reportEventStage(this, "end", Global.getSector().getPlayerFleet(),
                    MessagePriority.DELIVER_IMMEDIATELY, createSetMessageLocationScript(tatooine));

            Misc.makeUnimportant(mainContact, REASON);
            Misc.cleanUpMissionMemory(mainContact.getMemoryWithoutUpdate(), REASON + "_");

            updateStage(SWStorylineStage.DONE);

            CampaignEventManagerAPI eventManager = Global.getSector().getEventManager();
            MarketAPI jangala = Global.getSector().getEconomy().getMarket("jangala");
            if (jangala != null) {
                eventManager.startEvent(new CampaignEventTarget(jangala), Events.SYSTEM_BOUNTY, null);
            }

            endEvent();
        } else if (action.equals("printRefitHint")) {
            String refit = Global.getSettings().getControlStringForEnumName("CORE_REFIT");
            String autofit = Global.getSettings().getControlStringForEnumName("REFIT_MANAGE_VARIANTS");
            String transponder = "";
            if (!playerFleet.isTransponderOn()) {
                transponder = "\n\nAlso: you'll need to re-dock with your transponder turned on to take advantage of Ancyra's facilities.";
            }
            dialog.getTextPanel().addPara("(Once this conversation is over, press %s to open the refit screen. "
                    + "After selecting a specific ship, you can press %s to %s - pick a desired loadout, "
                    + "and the ship will be automatically refitted to match it, using what weapons are available."
                    + transponder + ")",
                    Misc.getHighlightColor(), refit, autofit, "\"autofit\"");
        }

        return true;
    }

    public static void endGalatiaPortionOfMission() {

        StarSystemAPI system = Global.getSector().getStarSystem("galatia");
        PlanetAPI ancyra = (PlanetAPI) system.getEntityById("ancyra");
        PlanetAPI pontus = (PlanetAPI) system.getEntityById("pontus");
        PlanetAPI tetra = (PlanetAPI) system.getEntityById("tetra");
        SectorEntityToken derinkuyu = system.getEntityById("derinkuyu_station");
        SectorEntityToken probe = system.getEntityById("galatia_probe");
        SectorEntityToken inner = system.getEntityById("galatia_jump_point_alpha");
        SectorEntityToken fringe = system.getEntityById("galatia_jump_point_fringe");
        SectorEntityToken relay = system.getEntityById("ancyra_relay");

        relay.getMemoryWithoutUpdate().unset(MemFlags.COMM_RELAY_NON_FUNCTIONAL);

        Global.getSector().getCharacterData().addAbility(Abilities.TRANSPONDER);
        Global.getSector().getCharacterData().addAbility(Abilities.GO_DARK);
        Global.getSector().getCharacterData().addAbility(Abilities.SENSOR_BURST);
        Global.getSector().getCharacterData().addAbility(Abilities.EMERGENCY_BURN);
        Global.getSector().getCharacterData().addAbility(Abilities.SUSTAINED_BURN);
        Global.getSector().getCharacterData().addAbility(Abilities.SCAVENGE);
        Global.getSector().getCharacterData().addAbility(Abilities.DISTRESS_CALL);

        FactionAPI hegemony = Global.getSector().getFaction(Factions.HEGEMONY);
        if (hegemony.getRelToPlayer().getRel() < 0) {
            hegemony.getRelToPlayer().setRel(0);
        }

        Global.getSector().getEconomy().addMarket(ancyra.getMarket());
        Global.getSector().getEconomy().addMarket(derinkuyu.getMarket());

        HintPanelAPI hints = Global.getSector().getCampaignUI().getHintPanel();
        if (hints != null) {
            hints.clearHints(false);
        }

        CampaignEventManagerAPI eventManager = Global.getSector().getEventManager();
        eventManager.startEvent(new CampaignEventTarget(ancyra.getMarket()), Events.SYSTEM_BOUNTY, null);

        RogueMinerMiscFleetManager script = new RogueMinerMiscFleetManager(derinkuyu);
        for (int i = 0; i < 20; i++) {
            script.advance(1f);
        }
        system.addScript(script);

        for (CampaignFleetAPI fleet : system.getFleets()) {
            if (Factions.PIRATES.equals(fleet.getFaction().getId())) {
                fleet.removeScriptsOfClass(TutorialLeashAssignmentAI.class);
            }
        }

        inner.getMemoryWithoutUpdate().unset(JumpPointInteractionDialogPluginImpl.UNSTABLE_KEY);
        inner.getMemoryWithoutUpdate().unset(JumpPointInteractionDialogPluginImpl.CAN_STABILIZE);

        fringe.getMemoryWithoutUpdate().unset(JumpPointInteractionDialogPluginImpl.UNSTABLE_KEY);
        fringe.getMemoryWithoutUpdate().unset(JumpPointInteractionDialogPluginImpl.CAN_STABILIZE);
    }

    protected void saveNag() {
        if (!Global.getSector().hasScript(SaveNagScript.class)) {
            Global.getSector().addScript(new SaveNagScript(10f));
        }
    }

    public void addWeaponsToStorage() {
        StoragePlugin plugin = ((StoragePlugin) tatooine.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getPlugin());
        plugin.setPlayerPaidToUnlock(true);

        CargoAPI cargo = plugin.getCargo();

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        for (FleetMemberAPI member : player.getFleetData().getMembersListCopy()) {
            //if (member.getVariant().getHullSpec().isDHull()) {
            for (WeaponSlotAPI slot : member.getVariant().getHullSpec().getAllWeaponSlotsCopy()) {
                //if (member.getVariant().getWeaponId(slot.getId()) == null) {
                String weaponId = getWeaponForSlot(slot);
                if (weaponId != null) {
                    cargo.addWeapons(weaponId, 1);
                }
                //}
            }
            //}
        }

        cargo.addFighters("broadsword_wing", 1);
        cargo.addFighters("piranha_wing", 1);

        cargo.addSupplies(50);
        cargo.sort();
    }

    public String getWeaponForSlot(WeaponSlotAPI slot) {
        switch (slot.getWeaponType()) {
            case BALLISTIC:
            case COMPOSITE:
            case HYBRID:
            case UNIVERSAL:
                switch (slot.getSlotSize()) {
                    case LARGE:
                        return pick("mark9", "hephag", "hellbore");
                    case MEDIUM:
                        return pick("arbalest", "heavymortar", "shredder");
                    case SMALL:
                        return pick("lightmg", "lightac", "lightmortar");
                }
                break;
            case MISSILE:
            case SYNERGY:
                switch (slot.getSlotSize()) {
                    case LARGE:
                        return pick("hammerrack");
                    case MEDIUM:
                        return pick("pilum", "annihilatorpod");
                    case SMALL:
                        return pick("harpoon", "sabot", "annihilator");
                }
                break;
            case ENERGY:
                switch (slot.getSlotSize()) {
                    case LARGE:
                        return pick("autopulse", "hil");
                    case MEDIUM:
                        return pick("miningblaster", "gravitonbeam", "pulselaser");
                    case SMALL:
                        return pick("mininglaser", "taclaser", "pdlaser", "ioncannon");
                }
                break;
        }

        return null;
    }

    public String pick(String... strings) {
        return strings[new Random().nextInt(strings.length)];
    }

    public OnMessageDeliveryScript createSetMessageLocationScript(final SectorEntityToken entity) {
        return new BaseOnMessageDeliveryScript() {
            public void beforeDelivery(CommMessageAPI message) {
                if (entity != null && entity.getContainingLocation() instanceof StarSystemAPI) {
                    message.setStarSystemId(entity.getContainingLocation().getId());
                } else {
                    message.setStarSystemId(tatooineSystem.getId());
                }
                message.setCenterMapOnEntity(entity);
            }
        };
    }

    public Map<String, String> getTokenReplacements() {

        Map<String, String> map = super.getTokenReplacements();

        addPersonTokens(map, "mainContact", mainContact);

        if (rebelContact != null) {
            addPersonTokens(map, "rebelContact", rebelContact);
        }

        if (mainContact != null) {
            addPersonTokens(map, "mainContact", mainContact);
        }

        //map.put("$sender", "Ancyra Research Facility");
        map.put("$systemName", tatooineSystem.getNameWithLowercaseType());

        return map;
    }

    @Override
    public String[] getHighlights(String stageId) {
        List<String> result = new ArrayList<String>();

        if ("posting".equals(stageId)) {
        } else if ("success".equals(stageId)) {
        } else {
            //addTokensToList(result, "$rewardCredits");
        }

        return result.toArray(new String[0]);
    }

    @Override
    public Color[] getHighlightColors(String stageId) {
        return super.getHighlightColors(stageId);
    }

    public boolean isDone() {
        return ended;
    }

    public String getEventName() {
        if (stage == SWStorylineStage.CONTACT) {
            return "Contact " + mainContact.getPost() + " " + mainContact.getName().getLast();
        }
        if (stage == SWStorylineStage.DELIVER_REPORT) {
            return "Deliver Report to Jangala";
        }
        if (stage == SWStorylineStage.DONE) {
            return "Deliver Report to Jangala - completed";
        }
        return "Stabilize the Jump-points";
    }

    @Override
    public CampaignEventCategory getEventCategory() {
        return CampaignEventCategory.MISSION;
    }

    @Override
    public String getEventIcon() {
        return Global.getSettings().getSpriteName("campaignMissions", "SW_StoryIcon");
    }

    @Override
    public String getCurrentImage() {
        return tatooine.getFaction().getLogo();
    }

}
