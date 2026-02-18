package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@Mixin(value = ArmorFeatureRenderer.class, priority = 650)
public abstract class MixinCosmeticArmorVisibility<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

	@Shadow protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

	@Shadow protected abstract boolean usesInnerModel(EquipmentSlot slot);

	@Shadow protected abstract void renderGlint(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model);

	@Shadow protected abstract void renderTrim(RegistryEntry<ArmorMaterial> armorMaterial, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings);

	@Shadow protected abstract void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model, int i, Identifier identifier);

	@Unique
	private List<Supplier<Boolean>> cosmeticarmor$renderList = new LinkedList<>();

	public MixinCosmeticArmorVisibility(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private void renderCustomArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot slot, int light, A model, CallbackInfo ci) {
		ItemStack equippedStack = entity.getEquippedStack(slot);
		ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
		if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.isIn(CosmeticArmor.ALWAYS_VISIBLE))) {
			ArmorRenderer renderer = ArmorRendererRegistryImpl.get(cosmeticStack.getItem());

			if (renderer != null) {
				cosmeticarmor$renderList.add(() -> {
					renderer.render(matrices, vertexConsumers, cosmeticStack, entity, slot, light,
						(BipedEntityModel<LivingEntity>) getContextModel());
					return true;
				});
				ci.cancel();
			} else {
				if(ArmorRendererRegistryImpl.get(equippedStack.getItem()) != null) {
					cosmeticarmor$renderList.add(() -> {
						cosmeticarmor$renderArmor(matrices, vertexConsumers, entity, cosmeticStack, slot, light, model);
						return true;
					});
					ci.cancel();
				}
			}
		}
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("TAIL"))
	private void renderDelayed(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		cosmeticarmor$renderList.forEach(Supplier::get);
		cosmeticarmor$renderList.clear();
	}

	@Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
	private ItemStack modifyVisibleArmor(LivingEntity entity, EquipmentSlot slot) {
		ItemStack equippedStack = entity.getEquippedStack(slot);
		ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
		if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.isIn(CosmeticArmor.ALWAYS_VISIBLE))) {
			return cosmeticStack;
		}
		return equippedStack;
	}

	@Unique
	private void cosmeticarmor$renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, ItemStack itemStack, EquipmentSlot armorSlot, int light, A model) {
		Item var9 = itemStack.getItem();
		if (var9 instanceof ArmorItem armorItem) {
			if (armorItem.getSlotType() == armorSlot) {
				((M) this.getContextModel()).copyBipedStateTo(model);
				this.setVisible(model, armorSlot);
				boolean bl = this.usesInnerModel(armorSlot);

				int color = itemStack.isIn(ItemTags.DYEABLE) ? ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(itemStack, -6265536)) : -1;

				for (ArmorMaterial.Layer layer : armorItem.getMaterial().value().layers()) {
					int layerColor = layer.isDyeable() ? color : -1;
					this.renderArmorParts(matrices, vertexConsumers, light, model, layerColor, layer.getTexture(bl));
				}

				ArmorTrim trim = itemStack.get(DataComponentTypes.TRIM);
				if (trim != null) {
					this.renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, trim, model, bl);
				}
				if (itemStack.hasGlint()) {
					this.renderGlint(matrices, vertexConsumers, light, model);
				}
			}
		}

	}
}
