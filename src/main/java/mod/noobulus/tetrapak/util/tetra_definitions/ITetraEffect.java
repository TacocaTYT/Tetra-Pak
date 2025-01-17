package mod.noobulus.tetrapak.util.tetra_definitions;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import javax.annotation.Nullable;

import static mod.noobulus.tetrapak.util.ItemHelper.getModularItemOfDamgeSource;

public interface ITetraEffect {
	static ItemEffect get(String key) {
		return ItemEffect.get(BuildConfig.MODID + ":" + key);
	}

	ItemEffect getEffect();

	default boolean hasEffect(@Nullable ItemStack stack) {
		return getEffectLevel(stack) > 0;
	}

	default void doBeltTick(PlayerEntity player, int effectLevel) {
	}

	default void doBeltTick(PlayerEntity player, @Nullable ItemStack belt) {
		doBeltTick(player, getEffectLevel(belt));
	}

	default int getBeltEffectLevel(PlayerEntity playerEntity) {
		return getEffectLevel(ToolbeltHelper.findToolbelt((playerEntity)));
	}

	default boolean hasBeltEffect(@Nullable Entity entity) {
		if (entity instanceof PlayerEntity)
			return getBeltEffectLevel(((PlayerEntity) entity)) > 0;
		return false;
	}

	default double getEffectEfficiency(@Nullable DamageSource source) {
		return getEffectEfficiency(getModularItemOfDamgeSource(source));
	}

	default boolean hasEffect(DamageSource source) {
		return hasEffect(getModularItemOfDamgeSource(source));
	}

	default double getEffectEfficiency(@Nullable ItemStack test) {
		if (test == null || test.isEmpty() || !(test.getItem() instanceof ModularItem))
			return 0;
		ModularItem item = (ModularItem) test.getItem();
		return item.getEffectEfficiency(test, getEffect());
	}

	default String getStatsPath() {
		return BuildConfig.MODID + ".stats." + new ResourceLocation(getEffect().getKey()).getPath();
	}

	default int getEffectLevel(@Nullable ItemStack test) {
		if (test == null || test.isEmpty() || !(test.getItem() instanceof ModularItem))
			return 0;
		ModularItem item = (ModularItem) test.getItem();
		return item.getEffectLevel(test, getEffect());
	}

	default String getTooltipPath() {
		return getStatsPath() + ".tooltip";
	}
}
