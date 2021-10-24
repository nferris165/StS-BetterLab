package betterLab.potions;

import betterLab.BetterLab;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

public class LabPotion extends AbstractPotion {


    public static final String POTION_ID = BetterLab.makeID("LabPotion");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    private ArrayList<AbstractPotion> potions;

    public LabPotion() {
        super(NAME, POTION_ID, PotionRarity.PLACEHOLDER, PotionSize.HEART, PotionColor.POWER);

        isThrown = false;
    }

    public LabPotion(ArrayList<AbstractPotion> pots) {
        super(NAME, POTION_ID, PotionRarity.PLACEHOLDER, PotionSize.HEART, PotionColor.POWER);

        this.potions = pots;

        isThrown = false;
        initializeData();
    }

    @Override
    public void use(AbstractCreature target) {

        AbstractCreature enemy = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
        for(AbstractPotion p : potions){
            p.use(p.targetRequired ? enemy : target);
        }
    }

    @Override
    public void initializeData() {
        this.potency = this.getPotency();

        if(this.potions == null){
            return;
        }

        potions.get(0).initializeData();
        potions.get(1).initializeData();
        potions.get(2).initializeData();

        this.description = potions.get(0).description + " NL " + potions.get(1).description + " NL " + potions.get(2).description;

        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public AbstractPotion makeCopy() {
        return new LabPotion(potions);
    }

    @Override
    public int getPotency(final int ascension) {
        int pot = 15;
        return pot;
    }
}
