package ua.nanit.limbo.protocol.packets.play;

import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketIn;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;

public class PacketPlayerLook implements PacketIn {

    private float yaw;
    private float pitch;
    private boolean onGround;

    public PacketPlayerLook() {}

    public PacketPlayerLook(float yaw, float pitch, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.yaw = msg.readFloat();
        this.pitch = msg.readFloat();
        this.onGround = msg.readBoolean();
    }

    @Override
    public void handle(ClientConnection conn, LimboServer server) {
        server.getPacketHandler().handle(conn, this);
    }
}
