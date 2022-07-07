package com.vincenthuto.lavamotron.network;

import java.util.function.Supplier;

import com.vincenthuto.lavamotron.menu.LavamotronMenu;
import com.vincenthuto.lavamotron.objects.LavamotronBlockEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

public class PacketToggleMachineMode {

	public PacketToggleMachineMode() {
	}

	public static void encode(PacketToggleMachineMode msg, FriendlyByteBuf buf) {
	}

	public static PacketToggleMachineMode decode(FriendlyByteBuf buf) {

		return new PacketToggleMachineMode();
	}

	public static class Handler {

		public static void handle(final PacketToggleMachineMode msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				AbstractContainerMenu container = ctx.get().getSender().containerMenu;
				if (container instanceof LavamotronMenu) {
					LavamotronBlockEntity station = ((LavamotronMenu) container).getTe();
					station.setLiquidMode(!station.getLiquidMode());
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}