package betterLab.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;

public class AbstractLargeDialogOptionButton extends LargeDialogOptionButton {

    private AbstractPotion potionToPreview;

    public AbstractLargeDialogOptionButton(int slot, String msg, AbstractPotion previewPotion) {
        super(slot, msg);
        this.potionToPreview = previewPotion;
    }

    public void renderPotionPreview(SpriteBatch sb) {
        if (!Settings.isControllerMode && this.potionToPreview != null && this.hb.hovered) {
            TipHelper.queuePowerTips(470.0F * Settings.scale, (float) InputHelper.mY + TipHelper.calculateToAvoidOffscreen(this.potionToPreview.tips, (float)InputHelper.mY), this.potionToPreview.tips);
        }
    }

    @Override
    public void renderRelicPreview(SpriteBatch sb) {
        if (!Settings.isControllerMode && this.potionToPreview != null && this.hb.hovered) {
            TipHelper.queuePowerTips(470.0F * Settings.scale, (float) InputHelper.mY + TipHelper.calculateToAvoidOffscreen(this.potionToPreview.tips, (float)InputHelper.mY), this.potionToPreview.tips);
        }
        super.renderRelicPreview(sb);
    }
}
