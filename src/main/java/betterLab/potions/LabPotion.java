package betterLab.potions;

import betterLab.BetterLab;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

public class LabPotion extends AbstractPotion {


    public static final String POTION_ID = BetterLab.makeID("AltarPotion");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    private ArrayList<AbstractPotion> potions;
    private AbstractPotion pot1, pot2, pot3;

    public LabPotion(ArrayList<AbstractPotion> pots) {
        super(NAME, POTION_ID, PotionRarity.PLACEHOLDER, PotionSize.HEART, PotionColor.POWER);

        this.potions = pots;
        this.pot1 = pots.get(0);
        this.pot2 = pots.get(1);
        this.pot3 = pots.get(2);

        isThrown = false;
        initializeData();
    }

    @Override
    public void use(AbstractCreature target) {
        pot1.use(target);
        pot2.use(target);
        pot3.use(target);
    }

    @Override
    public void initializeData() {
        this.potency = this.getPotency();

        if(this.potions == null){
            this.tips.clear();
            this.tips.add(new PowerTip(this.name, ""));
            return;
        }

        pot1.initializeData();
        pot2.initializeData();
        pot3.initializeData();

        this.description = pot1.description + " NL " + pot2.description + " NL " + pot3.description;

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
