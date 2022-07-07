package com.vincenthuto.lavamotron.network;

import com.vincenthuto.lavamotron.core.Lavamotron;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static int networkID = 0;
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel MAINCHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Lavamotron.MOD_ID, "mainchannel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void registerChannels() {

		MAINCHANNEL.registerMessage(networkID++, PacketToggleMachineMode.class, PacketToggleMachineMode::encode,
				PacketToggleMachineMode::decode, PacketToggleMachineMode.Handler::handle);

	}

}
