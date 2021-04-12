package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.EffectHelper;
import mod.noobulus.tetrapak.util.IClientInit;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import javax.annotation.Nullable;

public class MoonstrikeEffect implements IClientInit {
	private static final ItemEffect MOONSTRIKE_EFFECT = EffectHelper.get("moonstrike");

	@SubscribeEvent
	public static void moonstrikeToolsBreakBlocksFaster(PlayerEvent.BreakSpeed event) {
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (!(heldItemMainhand.getItem() instanceof ModularItem))
			return;
		ModularItem item = (ModularItem) heldItemMainhand.getItem();
		if (ItemHelper.getEffectLevel(heldItemMainhand, MOONSTRIKE_EFFECT) > 0) {
			IWorld moonPhaseWorld = event.getPlayer().getEntityWorld();
			float efficiency = (float) item.getEffectEfficiency(heldItemMainhand, MOONSTRIKE_EFFECT);
			event.setNewSpeed(event.getOriginalSpeed() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	@SubscribeEvent
	public static void moonstrikeCausesBonusDamage(LivingHurtEvent event) {
		if (shouldMoonstrikeAffect(DamageBufferer.getLastActiveDamageSource())) {
			Entity source = event.getSource().getImmediateSource();
			if (source == null)
				return;
			IWorld moonPhaseWorld = source.getEntityWorld();
			float efficiency = EffectHelper.getEffectEfficiency(DamageBufferer.getLastActiveDamageSource(), MOONSTRIKE_EFFECT);
			event.setAmount(event.getAmount() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	private static float getMoonFactor(IWorld world, float efficiency) {
		int moonPhase = world.getMoonPhase();
		return (1 + ((efficiency / 10) * (float) Math.abs(moonPhase - 4) / 40));
	}

	private static boolean shouldMoonstrikeAffect(@Nullable DamageSource source) {
		if (source == null)
			return false;
		if (source.getTrueSource() instanceof LivingEntity) {
			LivingEntity user = (LivingEntity) source.getTrueSource();

			return ItemHelper.getEffectLevel(user.getHeldItemMainhand(), MOONSTRIKE_EFFECT) > 0 ||
				ItemHelper.getEffectLevel(ItemHelper.getThrownItemStack(source.getImmediateSource()), MOONSTRIKE_EFFECT) > 0;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientInit() {
		final IStatGetter moonstrikeGetter = new StatGetterEffectEfficiency(MOONSTRIKE_EFFECT, 1);
		final GuiStatBar moonstrikeBar = new GuiStatBar(0, 0, 59, EffectHelper.getStatsPath(MOONSTRIKE_EFFECT),
			0D, 100D, false, moonstrikeGetter, LabelGetterBasic.percentageLabelDecimal,
			(player, itemStack) -> I18n.format(EffectHelper.getTooltipPath(MOONSTRIKE_EFFECT),
				moonstrikeGetter.getValue(player, itemStack), moonstrikeGetter.getValue(player, itemStack)));

		WorkbenchStatsGui.addBar(moonstrikeBar);
		HoloStatsGui.addBar(moonstrikeBar);
	}
}
