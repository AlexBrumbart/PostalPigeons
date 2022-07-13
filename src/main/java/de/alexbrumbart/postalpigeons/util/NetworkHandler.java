package de.alexbrumbart.postalpigeons.util;

import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.util.packets.CBMailReceptorPacket;
import de.alexbrumbart.postalpigeons.util.packets.SBMailReceptorPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {
    private static final String protocolVersion = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(PostalPigeons.ID, "main"), () -> protocolVersion, protocolVersion::equals, protocolVersion::equals);

    private NetworkHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(0, CBMailReceptorPacket.class, CBMailReceptorPacket::encode, CBMailReceptorPacket::new, CBMailReceptorPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(1, SBMailReceptorPacket.class, SBMailReceptorPacket::encode, SBMailReceptorPacket::new, SBMailReceptorPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
