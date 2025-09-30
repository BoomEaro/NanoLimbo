package ua.nanit.limbo.util;

import lombok.experimental.UtilityClass;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;

import java.util.Locale;

@UtilityClass
public class PacketUtils {

    public static String toPacketId(int packetId) {
        return "0x" + Integer.toHexString(packetId).toUpperCase(Locale.ROOT);
    }

    public static String toDetailedInfo(Packet packet, int packetId, Version version, State state) {
        return packet.toString() + "(" + toPacketId(packetId) + ") [" + version + "|" + state + "]";
    }
}
