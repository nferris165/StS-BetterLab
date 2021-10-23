package betterLab.relics;

import basemod.abstracts.CustomRelic;
import betterLab.BetterLab;
import betterLab.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;

import static betterLab.BetterLab.makeRelicOutlinePath;
import static betterLab.BetterLab.makeRelicPath;

public class ChemicalYRelic extends CustomRelic {

    public static final String ID = BetterLab.makeID("ChemicalYRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("ChemicalYRelic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("ChemicalYRelic.png"));

    public ChemicalYRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);

        this.counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }


    @Override
    public void atBattleStart() {
        this.flash();
        AbstractDungeon.player.obtainPotion(PotionHelper.getRandomPotion());
    }

    @Override
    public void updateDescription(AbstractPlayer.PlayerClass c) {

        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }
}
