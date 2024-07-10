package ua.nanit.limbo.protocol.packets.configuration;

import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.MetadataWriter;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.world.DimensionRegistry;

public class PacketRegistryData implements PacketOut {

    private DimensionRegistry dimensionRegistry;
    private MetadataWriter metadataWriter;

    public void setDimensionRegistry(DimensionRegistry dimensionRegistry) {
        this.dimensionRegistry = dimensionRegistry;
    }

    public void setMetadataWriter(MetadataWriter metadataWriter) {
        this.metadataWriter = metadataWriter;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        if (metadataWriter != null) {
            if (version.moreOrEqual(Version.V1_20_5)) {
                metadataWriter.writeData(msg, version);
                return;
            }
        }
        msg.writeNamelessCompoundTag(dimensionRegistry.getCodec_1_20());
    }
}
