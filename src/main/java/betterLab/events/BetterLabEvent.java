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


    private static final String CHEM_MSG, BREW_MSG, CHEMX_MSG, ADDPOT_MSG;
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
        this.EventText.setDialogOption(
                AbstractDungeon.player.hasRelic(Cauldron.ID) ? OPTIONS[6] : OPTIONS[7],
                AbstractDungeon.player.hasRelic(Cauldron.ID));
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
                        AbstractDungeon.player.loseRelic(Cauldron.ID);
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
                this.EventText.updateDialogOption(0, OPTIONS[5]);
                this.EventText.clearRemainingOptions();
                break;
            case COMPLETE:
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                this.openMap();
        }
    }

    private void setBrewOptions(AbstractPotion.PotionRarity rarity){
        this.potOptions.clear();
        for(int i = 0; i < 3; i++){
            AbstractPotion temp;
            do {
                temp = BetterLab.optionLimit
                        ? getPotion(rarity)
                        : AbstractDungeon.returnRandomPotion(rarity, false);
            } while(potOptions.contains(temp));
            potOptions.add(temp);
        }
        this.EventText.clearAllDialogs();
        this.EventText.setDialogOption(OPTIONS[8] + potOptions.get(0).name + OPTIONS[9], potOptions.get(0));
        this.EventText.setDialogOption(OPTIONS[8] + potOptions.get(1).name + OPTIONS[9], potOptions.get(1));
        this.EventText.setDialogOption(OPTIONS[8] + potOptions.get(2).name + OPTIONS[9], potOptions.get(2));
    }

    private AbstractPotion getPotion(AbstractPotion.PotionRarity rarity){
        ArrayList<String> retVal = new ArrayList<>();

        switch(rarity){
            case COMMON:
                retVal.add("Block Potion");
                retVal.add("Dexterity Potion");
                retVal.add("Energy Potion");
                retVal.add("Explosive Potion");
                retVal.add("Fire Potion");
                retVal.add("Strength Potion");
                retVal.add("Swift Potion");
                retVal.add("Weak Potion");
                retVal.add("FearPotion");
                retVal.add("AttackPotion");
                retVal.add("SkillPotion");
                retVal.add("PowerPotion");
                retVal.add("ColorlessPotion");
                retVal.add("SteroidPotion");
                retVal.add("SpeedPotion");
                retVal.add("BlessingOfTheForge");
                break;
            case UNCOMMON:
                retVal.add("Ancient Potion");
                retVal.add("DistilledChaos");
                retVal.add("DuplicationPotion");
                retVal.add("EssenceOfSteel");
                retVal.add("GamblersBrew");
                retVal.add("LiquidBronze");
                retVal.add("LiquidMemories");
                retVal.add("Regen Potion");
                break;
            case RARE:
                retVal.add("CultistPotion");
                retVal.add("EntropicBrew");
                retVal.add("Fruit Juice");
                retVal.add("SmokeBomb");
                retVal.add("SneckoOil");
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
