package io.github.apace100.cosmetic_armor;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeInfo;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.component.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public class CosmeticArmor implements ModInitializer {

	public static final String MODID = "cosmetic-armor";

	public static final String HELMET = "cosmetic_helmet";
	public static final String CHESTPLATE = "cosmetic_chestplate";
	public static final String LEGGINGS = "cosmetic_leggings";
	public static final String BOOTS = "cosmetic_boots";

	@Override
	public void onInitialize() {
		CuriosApi.enqueueSlotType(SlotTypeInfo.BuildScheme.REGISTER, new SlotTypeInfo.Builder(HELMET).icon(new Identifier("cosmetic-armor", "gui/cosmetic_helmet_icon")).priority(2).build());
		CuriosApi.enqueueSlotType(SlotTypeInfo.BuildScheme.REGISTER, new SlotTypeInfo.Builder(CHESTPLATE).icon(new Identifier("cosmetic-armor", "gui/cosmetic_chestplate_icon")).priority(3).build());
		CuriosApi.enqueueSlotType(SlotTypeInfo.BuildScheme.REGISTER, new SlotTypeInfo.Builder(LEGGINGS).icon(new Identifier("cosmetic-armor", "gui/cosmetic_leggings_icon")).priority(4).build());
		CuriosApi.enqueueSlotType(SlotTypeInfo.BuildScheme.REGISTER, new SlotTypeInfo.Builder(BOOTS).icon(new Identifier("cosmetic-armor", "gui/cosmetic_boots_icon")).priority(5).build());
	}

	public static ItemStack getStackInCosmeticSlot(LivingEntity entity, EquipmentSlot slot) {
		String slotIdentifier = getCuriosIdentifierBySlot(slot);
		if(!slotIdentifier.isEmpty()) {
			Optional<ICuriosItemHandler> itemHandler = CuriosApi.getCuriosHelper().getCuriosHandler(entity);
			if(itemHandler.isPresent()) {
				Optional<ICurioStacksHandler> optionalStacksHandler = itemHandler.get().getStacksHandler(slotIdentifier);
				if(optionalStacksHandler.isPresent()) {
					ICurioStacksHandler stacksHandler = optionalStacksHandler.get();
					int slotCount = stacksHandler.getSlots();
					ItemStack stack = ItemStack.EMPTY;
					IDynamicStackHandler dynamicStackHandler = stacksHandler.getStacks();
					DefaultedList<Boolean> renders = stacksHandler.getRenders();
					for(int i = 0; i < slotCount && stack.isEmpty(); i++) {
						if(renders.get(i)) {
							stack = dynamicStackHandler.getStack(i);
						}
					}
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private static String getCuriosIdentifierBySlot(EquipmentSlot slot) {
		if(slot == EquipmentSlot.CHEST) {
			return CHESTPLATE;
		} else if(slot == EquipmentSlot.HEAD) {
			return HELMET;
		} else if(slot == EquipmentSlot.LEGS) {
			return LEGGINGS;
		} else if(slot == EquipmentSlot.FEET) {
			return BOOTS;
		}
		return "";
	}
}
