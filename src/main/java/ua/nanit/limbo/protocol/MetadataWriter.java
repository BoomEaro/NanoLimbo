package ua.nanit.limbo.protocol;

import ua.nanit.limbo.protocol.registry.Version;

@FunctionalInterface
public interface MetadataWriter {

    void writeData(ByteMessage message, Version version);

}
