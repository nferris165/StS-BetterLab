package betterLab.util;

import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;

public class AbstractEventDialog extends GenericEventDialog {

    public void setDialogOption(String text, AbstractPotion previewPotion) {
        this.optionList.add(new AbstractLargeDialogOptionButton(this.optionList.size(), text, previewPotion));

        for (LargeDialogOptionButton b : this.optionList) {
            b.calculateY(this.optionList.size());
        }

    }
}
