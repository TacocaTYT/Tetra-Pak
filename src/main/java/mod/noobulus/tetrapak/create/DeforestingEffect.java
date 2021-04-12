package mod.noobulus.tetrapak.create;

import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import mod.noobulus.tetrapak.util.EffectHelper;
import mod.noobulus.tetrapak.util.IHoloDescription;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class DeforestingEffect implements IHoloDescription {
	@SubscribeEvent
	public void deforestWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (hasEffect(event.getPlayer().getHeldItemMainhand())) {
			DeforesterItem.destroyTree(event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	public ItemEffect getEffect() {
		return EffectHelper.get("deforesting");
	}
}
