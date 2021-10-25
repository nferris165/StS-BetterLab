package betterLab.events;

import betterLab.BetterLab;
import betterLab.potions.LabPotion;
import betterLab.relics.ChemicalYRelic;
import betterLab.util.AbstractEventDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.Cauldron;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.relics.OddMushroom;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.scene.EventBgParticle;

import java.util.ArrayList;

import static org.apache.commons.lang3.math.NumberUtils.max;

public class BetterLabEvent extends AbstractImageEvent {

    public static final String ID = BetterLab.makeID("BetterLab");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/lab.jpg";
    public AbstractEventDialog EventText = new AbstractEventDialog();


    private static final String CHEM_MSG, BREW_MSG, CHEMX_MSG, ADDPOT_MSG, MUSH_MSG;
    private static final ArrayList<String> COMMONS, UNCOMMONS, RARES, exclusions;
    private int potSlots, relicCost;
    private ArrayList<AbstractPotion> potions;
    private ArrayList<AbstractPotion> potOptions;
    private CUR_SCREEN screen;

    public BetterLabEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.EventText.loadImage(IMG);

        this.screen = CUR_SCREEN.INTRO;
        this.noCardsInRewards = true;
        this.potions = new ArrayList<>();
        this.potOptions = new ArrayList<>();
        this.potSlots = AbstractDungeon.player.potionSlots;
        this.relicCost = max((int)(AbstractDungeon.player.maxHealth * 0.2F), 6);
        if(AbstractDungeon.ascensionLevel >= 15){
            potSlots--;
            relicCost *= 1.5F;
        }
        this.EventText.setDialogOption(OPTIONS[0]);
        this.EventText.setDialogOption(OPTIONS[1]);
        if(AbstractDungeon.player.hasRelic(ChemicalX.ID)){
            this.EventText.setDialogOption(OPTIONS[2], new ChemicalYRelic());
        }
        else{
            this.EventText.setDialogOption(OPTIONS[3] + this.relicCost + OPTIONS[4], new ChemicalX());
        }
        if(AbstractDungeon.player.hasRelic(OddMushroom.ID)){
            this.EventText.setDialogOption(OPTIONS[10]);
        }
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_LAB");
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                switch(buttonPressed) {
                    case 0:
                        GenericEventDialog.hide();
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        for(int i = 0; i < potSlots; i++){
                            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                        }
                        this.screen = CUR_SCREEN.COMPLETE;
                        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 1:
                        this.EventText.updateBodyText(ADDPOT_MSG);
                        this.screen = CUR_SCREEN.BREW1;
                        setBrewOptions(AbstractPotion.PotionRarity.COMMON);
                        break;
                    case 2:
                        if(AbstractDungeon.player.hasRelic(ChemicalX.ID)){
                            AbstractDungeon.player.loseRelic(ChemicalX.ID);
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new ChemicalYRelic());
                            this.EventText.updateBodyText(CHEM_MSG);
                        }
                        else{
                            AbstractDungeon.player.damage(new DamageInfo(null, this.relicCost, DamageInfo.DamageType.HP_LOSS));
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new ChemicalX());
                            this.EventText.updateBodyText(CHEMX_MSG);
                        }
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.EventText.updateDialogOption(0, OPTIONS[5]);
                        this.EventText.clearRemainingOptions();
                        break;
                    case 3:
                        AbstractDungeon.player.loseRelic(OddMushroom.ID);
                        int slots = AbstractDungeon.player.potionSlots;
                        for(int i = 0; i < slots; i++){
                            AbstractDungeon.player.obtainPotion(AbstractDungeon.returnRandomPotion(AbstractPotion.PotionRarity.RARE, true));
                        }
                        this.EventText.updateBodyText(MUSH_MSG);
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.EventText.updateDialogOption(0, OPTIONS[5]);
                        this.EventText.clearRemainingOptions();
                        break;
                    default:
                        break;
                }
                break;
            case BREW1:
                this.EventText.updateBodyText(ADDPOT_MSG);
                this.screen = CUR_SCREEN.BREW2;
                this.potions.add(this.potOptions.get(buttonPressed));
                setBrewOptions(AbstractPotion.PotionRarity.UNCOMMON);
                break;
            case BREW2:
                this.EventText.updateBodyText(ADDPOT_MSG);
                this.screen = CUR_SCREEN.BREW3;
                this.potions.add(this.potOptions.get(buttonPressed));
                setBrewOptions(AbstractPotion.PotionRarity.RARE);
                break;
            case BREW3:
                this.potions.add(this.potOptions.get(buttonPressed));

                //add cust pot
                AbstractDungeon.player.obtainPotion(new LabPotion(potions));

                this.EventText.updateBodyText(BREW_MSG);
                this.screen = CUR_SCREEN.COMPLETE;
                this.EventText.clearAllDialogs();
                this.EventText.setDialogOption(OPTIONS[5]);
                this.EventText.setDialogOption(
                        AbstractDungeon.player.hasRelic(Cauldron.ID) ? OPTIONS[6] : OPTIONS[7],
                        !AbstractDungeon.player.hasRelic(Cauldron.ID));
                break;
            case COMPLETE:
                switch(buttonPressed){
                    case 0:
                        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                        this.openMap();
                        break;
                    case 1:
                        this.EventText.updateBodyText(ADDPOT_MSG);
                        AbstractDungeon.player.loseRelic(Cauldron.ID);
                        this.screen = CUR_SCREEN.BREW1;
                        this.potions.clear();
                        setBrewOptions(AbstractPotion.PotionRarity.COMMON);
                        break;
                }

        }
    }

    private void setBrewOptions(AbstractPotion.PotionRarity rarity){
        this.potOptions.clear();
        for(int i = 0; i < 3; i++){
            AbstractPotion temp;
            do {
                temp = BetterLab.moddedPotions
                        ? AbstractDungeon.returnRandomPotion(rarity, false)
                        : getPotion(rarity);
            } while(checkUnique(temp) || exclusions.contains(temp.ID));
            potOptions.add(temp);
        }
        this.EventText.clearAllDialogs();
        this.EventText.setDialogOption(OPTIONS[8] + potOptions.get(0).name + OPTIONS[9], potOptions.get(0));
        this.EventText.setDialogOption(OPTIONS[8] + potOptions.get(1).name + OPTIONS[9], potOptions.get(1));
        this.EventText.setDialogOption(OPTIONS[8] + potOptions.get(2).name + OPTIONS[9], potOptions.get(2));
    }

    private boolean checkUnique(AbstractPotion potion){
        boolean retVal = false;

        for(AbstractPotion p: potOptions){
            if(p.ID.equals(potion.ID)){
                retVal = true;
            }
        }

        return retVal;
    }

    private AbstractPotion getPotion(AbstractPotion.PotionRarity rarity){
        ArrayList<String> retVal = new ArrayList<>();

        switch(rarity){
            case COMMON:
                retVal = COMMONS;
                break;
            case UNCOMMON:
                retVal = UNCOMMONS;
                break;
            case RARE:
                retVal = RARES;
                break;
        }

        return PotionHelper.getPotion(retVal.get(AbstractDungeon.potionRng.random(retVal.size() - 1)));
    }

    @Override
    public void update() {
        if (!this.combatTime) {
            this.hasFocus = true;
            if (MathUtils.randomBoolean(0.1F)) {
                AbstractDungeon.effectList.add(new EventBgParticle());
            }

            if (this.waitTimer > 0.0F) {
                this.waitTimer -= Gdx.graphics.getDeltaTime();
                if (this.waitTimer < 0.0F) {
                    this.EventText.show(this.title, this.body);
                    this.waitTimer = 0.0F;
                }
            }

            if (!GenericEventDialog.waitForInput) {
                this.buttonEffect(GenericEventDialog.getSelectedOption());
            }
        }
    }

    @Override
    public void updateDialog() {
        this.EventText.update();
        this.roomEventText.update();
    }

    @Override
    public void renderText(SpriteBatch sb) {
        this.roomEventText.render(sb);
        this.EventText.render(sb);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.EventText.clear();
    }

    static {
        BREW_MSG = DESCRIPTIONS[1];
        CHEM_MSG = DESCRIPTIONS[2];
        CHEMX_MSG = DESCRIPTIONS[3];
        ADDPOT_MSG = DESCRIPTIONS[4];
        MUSH_MSG = DESCRIPTIONS[5];

        COMMONS = new ArrayList<>();
        COMMONS.add("Block Potion");
        COMMONS.add("Dexterity Potion");
        COMMONS.add("Energy Potion");
        COMMONS.add("Explosive Potion");
        COMMONS.add("Fire Potion");
        COMMONS.add("Strength Potion");
        COMMONS.add("Swift Potion");
        COMMONS.add("Weak Potion");
        COMMONS.add("FearPotion");
        COMMONS.add("AttackPotion");
        COMMONS.add("SkillPotion");
        COMMONS.add("PowerPotion");
        COMMONS.add("ColorlessPotion");
        COMMONS.add("SteroidPotion");
        COMMONS.add("SpeedPotion");
        COMMONS.add("BlessingOfTheForge");
        UNCOMMONS = new ArrayList<>();
        UNCOMMONS.add("Ancient Potion");
        UNCOMMONS.add("DistilledChaos");
        UNCOMMONS.add("DuplicationPotion");
        UNCOMMONS.add("EssenceOfSteel");
        UNCOMMONS.add("GamblersBrew");
        UNCOMMONS.add("LiquidBronze");
        UNCOMMONS.add("LiquidMemories");
        UNCOMMONS.add("Regen Potion");
        RARES = new ArrayList<>();
        RARES.add("CultistPotion");
        RARES.add("EntropicBrew");
        RARES.add("Fruit Juice");
        RARES.add("SmokeBomb");
        RARES.add("SneckoOil");

        //excluded potions
        exclusions = new ArrayList<>();
        exclusions.add("FairyPotion");
    }

    private enum CUR_SCREEN {
        INTRO,
        BREW1,
        BREW2,
        BREW3,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
