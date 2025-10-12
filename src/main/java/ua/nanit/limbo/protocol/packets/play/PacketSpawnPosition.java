package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@AllArgsConstructor
@NoArgsConstructor
public class PacketSpawnPosition implements PacketOut {

    private String dimensionName;
    private long x;
    private long y;
    private long z;

    @Override
    public void encode(ByteMessage msg, Version version) {
        if (version.moreOrEqual(Version.V1_21_9)) {
            msg.writeString(dimensionName);
        }
        msg.writeLong(encodePosition(x, y ,z));
        msg.writeFloat(0);
        if (version.moreOrEqual(Version.V1_21_9)) {
            msg.writeFloat(0);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private static long encodePosition(long x, long y, long z) {
        return ((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
