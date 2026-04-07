package io.github.apace100.cosmetic_armor;

import eu.pb4.trinkets.api.SlotType;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class CosmeticArmor implements ModInitializer {

	public static final String MODID = "cosmeticarmor";

	public static final TagKey<Item> BLACKLIST = TagKey.create(Registries.ITEM, id("blacklist"));
	public static final TagKey<Item> ALWAYS_VISIBLE = TagKey.create(Registries.ITEM, id("always_visible"));

	@Override
	public void onInitialize() {
		EquipmentSlot[] slots = new EquipmentSlot[]{
				EquipmentSlot.HEAD,
				EquipmentSlot.CHEST,
				EquipmentSlot.LEGS,
				EquipmentSlot.FEET
		};

		for(EquipmentSlot slot : slots) {
			TrinketsApi.registerTrinketPredicate(id(slot.getName()), (stack, slotReference, entity) -> {
				if(stack.is(BLACKLIST)) {
					return TriState.FALSE.get();
				}
				if(entity.getEquipmentSlotForItem(stack) == slot) {
					return TriState.TRUE.get();
				}
				return TriState.DEFAULT.get();
			});
		}
	}

	public static ItemStack getCosmeticArmor(LivingEntity entity, EquipmentSlot slot) {
		Optional<TrinketAttachment> component = Optional.ofNullable(TrinketsApi.getAttachment(entity));
		if(component.isPresent()) {
			List<Tuple<TrinketSlotAccess, ItemStack>> list = component.get().getEquipped(stack -> entity.getEquipmentSlotForItem(stack) == slot);

			for(Tuple<TrinketSlotAccess, ItemStack> equipped : list) {
				SlotType slotType = equipped.getA().inventory().slotType();
				if(!slotType.name().equals("cosmetic")) {
					continue;
				}
				if(!slotType.group().equalsIgnoreCase(slot.getName())) {
					continue;
				}
				return equipped.getB();
			}
		}
		return ItemStack.EMPTY;
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}
