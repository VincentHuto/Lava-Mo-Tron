package com.vincenthuto.eelcologicaldisasters.common.data;

import com.vincenthuto.eelcologicaldisasters.EelcologicalDisasters;
import com.vincenthuto.eelcologicaldisasters.init.BlockInit;
import com.vincenthuto.eelcologicaldisasters.init.EntityInit;
import com.vincenthuto.eelcologicaldisasters.init.ItemInit;
import com.vincenthuto.hutoslib.client.HLTextUtils;

import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class EDLanguageProvider extends LanguageProvider {

	public EDLanguageProvider(PackOutput output, String locale) {
		super(output, EelcologicalDisasters.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		// Jei
//		add("eelcologicaldisasters.jei.recaller", "Visceral Recaller");
//		add("eelcologicaldisasters.jei.chisel_station", "Chisel Station");

		// Banner
//		addArmBannerTranslation("chitinite");
//
//		addBannerTranslation("eelcologicaldisasters_heart", "Vascularium");
//		addBannerTranslation("eelcologicaldisasters_veins", "Veins");
//		add("item.eelcologicaldisasters.heart_pattern.desc", "Vascularium Crest");
//		add("item.eelcologicaldisasters.veins_pattern.desc", "Vein Border");
//		addKeyBindTranslations();

		add("item_group.eelcologicaldisasters.eelcologicaldisasterstab", "Eelcological Disaster");

		for (RegistryObject<EntityType<?>> e : EntityInit.ENTITY_TYPES.getEntries()) {
			addEntityType(e,
					HLTextUtils.convertInitToLang(e.get().getDescriptionId().replace("entity.eelcologicaldisasters.", "")));
		}
//		for (RegistryObject<Block> b : BlockInit.CROSSBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
		for (RegistryObject<Block> b : BlockInit.BASEBLOCKS.getEntries()) {
			addBlock(b, HLTextUtils.convertInitToLang(
					b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
		}

//		for (RegistryObject<Block> b : BlockInit.SLABBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
//
//		for (RegistryObject<Block> b : BlockInit.STAIRBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
//
//		for (RegistryObject<Block> b : BlockInit.SPECIALBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
//		for (RegistryObject<Block> b : BlockInit.MODELEDBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
//		for (RegistryObject<Block> b : BlockInit.POTTEDBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
//		for (RegistryObject<Block> b : BlockInit.OBJBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}
//		for (RegistryObject<Block> b : BlockInit.COLUMNBLOCKS.getEntries()) {
//			addBlock(b,
//					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.eelcologicaldisasters.", "")));
//		}

		for (RegistryObject<Item> i : ItemInit.BASEITEMS.getEntries()) {
			addItem(i, HLTextUtils
					.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.eelcologicaldisasters.", "")));
		}
//		for (RegistryObject<Item> i : ItemInit.HANDHELDITEMS.getEntries()) {
//			addItem(i,
//					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.eelcologicaldisasters.", "")));
//		}
//		for (RegistryObject<Item> i : ItemInit.SPECIALITEMS.getEntries()) {
//			addItem(i,
//					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.eelcologicaldisasters.", "")));
//		}
		for (RegistryObject<Item> i : ItemInit.SPAWNEGGS.getEntries()) {
			addItem(i, HLTextUtils
					.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.eelcologicaldisasters.", "")));
		}

//		add("entity.minecraft.villager.eelcologicaldisasters.hemopothecary", "Hemopothecary");

//
//		for (RegistryObject<MobEffect> i : PotionInit.EFFECTS.getEntries()) {
//			add("item.minecraft.potion.effect.potion_of_" + i.getId().getPath(),
//					"Potion of " + HLTextUtils.convertInitToLang(i.getId().getPath()));
//			add("item.minecraft.splash_potion.effect.potion_of_" + i.getId().getPath(),
//					"Spash Potion of " + HLTextUtils.convertInitToLang(i.getId().getPath()));
//			add("item.minecraft.lingering_potion.effect.potion_of_" + i.getId().getPath(),
//					"Lingering Potion of " + HLTextUtils.convertInitToLang(i.getId().getPath()));
//			add("item.minecraft.tipped_arrow.effect.potion_of_" + i.getId().getPath(),
//					"Arrow of " + HLTextUtils.convertInitToLang(i.getId().getPath()));
//			addEffect(() -> i.get(), HLTextUtils.convertInitToLang(i.getId().getPath()));
//		}

	}

	public void addKeyBindTranslations() {
		add("key.eelcologicaldisasters.category", "Hemomancy");
		add("key.eelcologicaldisasters.bloodcrafting.desc", "Activate Blood Construct");
		add("key.eelcologicaldisasters.bloodformation.desc", "Blood Formation");
		add("key.eelcologicaldisasters.drawtest.desc", "Blood Draw");
		add("key.eelcologicaldisasters.morphjarpickup.desc", "Toggle Morphling Jar P``````````````ickup");
		add("key.eelcologicaldisasters.openjar.desc", "Open Morphling Jar");
		add("key.eelcologicaldisasters.quickusemanip.desc", "Use Quick Manipulation");
		add("key.eelcologicaldisasters.runebinderpickup.desc", "Toggle Rune Binder Pickup");
		add("key.eelcologicaldisasters.contusemanip.desc", "Use Continous Manipulation");
		add("key.eelcologicaldisasters.cyclemanip.desc", "Cycle Known Manipulations");

	}

	public void addBannerTranslation(String regName, String transName) {
		add("block.minecraft.banner." + regName + ".black", "Black " + transName);
		add("block.minecraft.banner." + regName + ".red", "Red " + transName);
		add("block.minecraft.banner." + regName + ".green", "Green " + transName);
		add("block.minecraft.banner." + regName + ".brown", "Brown " + transName);
		add("block.minecraft.banner." + regName + ".blue", "Blue " + transName);
		add("block.minecraft.banner." + regName + ".purple", "Purple " + transName);
		add("block.minecraft.banner." + regName + ".cyan", "Cyan " + transName);
		add("block.minecraft.banner." + regName + ".silver", "Light Gray " + transName);
		add("block.minecraft.banner." + regName + ".gray", "Gray " + transName);
		add("block.minecraft.banner." + regName + ".pink", "Pink " + transName);
		add("block.minecraft.banner." + regName + ".lime", "Lime " + transName);
		add("block.minecraft.banner." + regName + ".yellow", "Yellow " + transName);
		add("block.minecraft.banner." + regName + ".lightBlue", "Light " + transName);
		add("block.minecraft.banner." + regName + ".magenta", "Magenta " + transName);
		add("block.minecraft.banner." + regName + ".orange", "Orange " + transName);
		add("block.minecraft.banner." + regName + ".white", "White " + transName);
	}

	public void addArmBannerTranslation(String prefix) {
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.black",
				"Black " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.red",
				"Red " + HLTextUtils.convertInitToLang("_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.green",
				"Green " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.brown",
				"Brown " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.blue",
				"Blue " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.purple",
				"Purple " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.cyan",
				"Cyan " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.silver",
				"Light Gray " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.gray",
				"Gray " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.pink",
				"Pink " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.lime",
				"Lime " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.yellow",
				"Yellow " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.lightBlue",
				"Light " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.magenta",
				"Magenta " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.orange",
				"Orange " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.eelcologicaldisasters." + prefix + "_arm_banner.white",
				"White " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
	}
}
