package ua.nanit.limbo.protocol.packets.play;

import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;

import java.util.BitSet;

public class PacketEmptyChunk implements PacketOut {

    private int x;
    private int z;

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeInt(this.x);
        msg.writeInt(this.z);

        if (version.less(Version.V1_17)) {
            msg.writeBoolean(true);
        }

        if (version.moreOrEqual(Version.V1_16) && version.less(Version.V1_16_2)) {
            msg.writeBoolean(true);
        }

        //BitMasks
        if (version.less(Version.V1_17)) {
            if (version.equals(Version.V1_8)) {
                msg.writeShort(1);
            } else {
                msg.writeVarInt(0);
            }
        } else if (version.less(Version.V1_18)) {
            BitSet bitSet = new BitSet();
            for (int i = 0; i < 16; i++) {
                bitSet.set(i, false);
            }
            long[] mask = bitSet.toLongArray();
            msg.writeVarInt(mask.length);
            for (long l : mask) {
                msg.writeLong(l);
            }
        }
        if (version.moreOrEqual(Version.V1_14)) {
            write1_14Heightmaps(msg, version);
            if (version.moreOrEqual(Version.V1_15) && version.less(Version.V1_18)) {
                if (version.moreOrEqual(Version.V1_16_2)) {
                    msg.writeVarInt(1024);
                    for (int i = 0; i < 1024; i++) {
                        msg.writeVarInt(1);
                    }
                } else {
                    for (int i = 0; i < 1024; i++) {
                        msg.writeInt(0);
                    }
                }
            }
        }
        if (version.less(Version.V1_13)) {
            msg.writeBytesArray(new byte[256]); //1.8 - 1.12.2
        } else if (version.less(Version.V1_15)) {
            msg.writeBytesArray(new byte[1024]); //1.13 - 1.14.4
        } else if (version.less(Version.V1_18)) {
            msg.writeVarInt(0); //1.15 - 1.17.1
        } else {
            byte[] sectionData = new byte[]{0, 0, 0, 0, 0, 0, 1, 0};
            msg.writeVarInt(sectionData.length * 16);
            for (int i = 0; i < 16; i++) {
                msg.writeBytes(sectionData);
            }
        }
        if (version.moreOrEqual(Version.V1_9_4)) {
            msg.writeVarInt(0);
        }

        if (version.moreOrEqual(Version.V1_18)) { //light data
            byte[] lightData = new byte[]{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, -1, -1, 0, 0};
            msg.ensureWritable(lightData.length);
            if (version.moreOrEqual(Version.V1_20)) {
                msg.writeBytes(lightData, 1, lightData.length - 1);
            } else {
                msg.writeBytes(lightData);
            }
        }
    }

    private static void write1_14Heightmaps(ByteMessage buf, Version version) {
        long[] arrayData = new long[version.less(Version.V1_18) ? 36 : 37];
        LongArrayBinaryTag longArrayTag = LongArrayBinaryTag.longArrayBinaryTag(arrayData);
        CompoundBinaryTag tag = CompoundBinaryTag.builder()
                .put("MOTION_BLOCKING", longArrayTag).build();
        CompoundBinaryTag rootTag = CompoundBinaryTag.builder()
                .put("root", tag).build();
        if (version.moreOrEqual(Version.V1_20_2)) {
            buf.writeNamelessCompoundTag(rootTag);
        } else {
            buf.writeCompoundTag(CompoundBinaryTag.builder().put("", rootTag).build());
        }
    }
}
