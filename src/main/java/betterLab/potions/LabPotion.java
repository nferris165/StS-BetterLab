package betterLab.potions;

import basemod.abstracts.CustomSavable;
import betterLab.BetterLab;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

public class LabPotion extends AbstractPotion implements CustomSavable<String> {


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
        return 1;
    }

    @Override
    public String onSave() {
        return potions.get(0).ID + "///" + potions.get(1).ID + "///" + potions.get(2).ID;
    }

    @Override
    public void onLoad(String s) {
        String[] strings = s.split("///");
        potions = new ArrayList<>();
        potions.add(0, PotionHelper.getPotion(strings[0]));
        potions.add(1, PotionHelper.getPotion(strings[1]));
        potions.add(2, PotionHelper.getPotion(strings[2]));

        initializeData();
    }
}
