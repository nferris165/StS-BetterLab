package betterLab.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.GoldShrine;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="initializeCardPools"
)
public class RemoveEventPatch {

    public static void Prefix(AbstractDungeon dungeon_instance) {
        /* // USING REPLACE INSTEAD OF REMOVE AND ADD
        AbstractDungeon.eventList.remove(Lab.ID);
        BetterLab.logger.info("Removing base events.");
        */

    }
}
