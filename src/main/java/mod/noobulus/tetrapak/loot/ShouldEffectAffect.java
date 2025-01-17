package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mcp.MethodsReturnNonnullByDefault;
import mod.noobulus.tetrapak.util.ItemHelper;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShouldEffectAffect implements ILootCondition, ITetraEffect {
	private final ItemEffect effect;

	public ShouldEffectAffect(ItemEffect effect) {
		this.effect = effect;
	}

	@Override
	public LootConditionType getType() {
		return LootConditions.SHOULD_EFFECT_AFFECT.type;
	}

	@Override
	public boolean test(LootContext context) {
		if (context.hasParam(LootParameters.BLOCK_ENTITY))
			return false;

		ItemStack tool = context.getParamOrNull(LootParameters.TOOL);
		Entity killerEntity = context.getParamOrNull(LootParameters.KILLER_ENTITY);
		Entity directKillerEntity = context.getParamOrNull(LootParameters.DIRECT_KILLER_ENTITY);

		if (!(context.getParamOrNull(LootParameters.THIS_ENTITY) instanceof PlayerEntity || context.getParamOrNull(LootParameters.THIS_ENTITY) instanceof IInventory)) {
			if (tool == null && directKillerEntity != null)
				tool = ItemHelper.getThrownItemStack(directKillerEntity);
			if (tool == null && killerEntity != null) {
				Iterator<ItemStack> equip = killerEntity.getHandSlots().iterator();
				if (equip.hasNext())
					tool = equip.next();
			}
		}

		return hasEffect(tool);
	}

	@Override
	public ItemEffect getEffect() {
		return effect;
	}

	public static class Serializer implements ILootSerializer<ShouldEffectAffect> {
		public void serialize(JsonObject json, ShouldEffectAffect condition, JsonSerializationContext context) {
			json.addProperty("effect", condition.effect.getKey());
		}

		public ShouldEffectAffect deserialize(JsonObject json, JsonDeserializationContext context) {
			return new ShouldEffectAffect(ItemEffect.get(JSONUtils.getAsString(json, "effect")));
		}
	}
}
