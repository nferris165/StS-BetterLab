package betterLab.events;

import betterLab.BetterLab;
import betterLab.relics.ChemicalYRelic;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static org.apache.commons.lang3.math.NumberUtils.max;

public class BetterLabEvent extends AbstractImageEvent {

    public static final String ID = BetterLab.makeID("BetterLab");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/lab.jpg";

    private static final String CHEM_MSG, BREW_MSG, CHEMX_MSG, UPGRADE_MSG;
    private int potSlots, relicCost;
    private CUR_SCREEN screen;

    public BetterLabEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CUR_SCREEN.INTRO;
        this.noCardsInRewards = true;
        this.potSlots = AbstractDungeon.player.potionSlots;
        this.relicCost = max((int)(AbstractDungeon.player.maxHealth * 0.2F), 6);
        if(AbstractDungeon.ascensionLevel >= 15){
            potSlots--;
            relicCost *= 1.5F;
        }
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        if(AbstractDungeon.player.hasRelic(ChemicalX.ID)){
            this.imageEventText.setDialogOption(OPTIONS[2], new ChemicalYRelic());
        }
        else{
            this.imageEventText.setDialogOption(OPTIONS[3] + this.relicCost + OPTIONS[4], new ChemicalX());
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
                        this.imageEventText.updateBodyText(BREW_MSG);
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2:
                        if(AbstractDungeon.player.hasRelic(ChemicalX.ID)){
                            AbstractDungeon.player.loseRelic(ChemicalX.ID);
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new ChemicalYRelic());
                            this.imageEventText.updateBodyText(CHEM_MSG);
                        }
                        else{
                            AbstractDungeon.player.damage(new DamageInfo(null, this.relicCost, DamageInfo.DamageType.HP_LOSS));
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new ChemicalX());
                            this.imageEventText.updateBodyText(CHEMX_MSG);
                        }
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 3:
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default:
                        break;
                }
                break;
            case COMPLETE:
                this.openMap();
        }
    }

    static {
        BREW_MSG = DESCRIPTIONS[1];
        CHEM_MSG = DESCRIPTIONS[2];
        CHEMX_MSG = DESCRIPTIONS[3];
        UPGRADE_MSG = DESCRIPTIONS[4];
    }

    private enum CUR_SCREEN {
        INTRO,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
