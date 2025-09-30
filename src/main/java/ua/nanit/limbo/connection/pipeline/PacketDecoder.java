/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.SneakyThrows;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.util.PacketUtils;

import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private State state;
    private State.PacketRegistry mappings;
    private Version version;

    public PacketDecoder() {
        updateVersion(Version.getMin());
        this.state = State.HANDSHAKING;
        updateState(this.state);
    }

    @SneakyThrows
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        if (!ctx.channel().isActive() || mappings == null) return;

        ByteMessage msg = new ByteMessage(buf);
        int packetId = msg.readVarInt();
        Packet packet = mappings.getPacket(packetId);
        if (packet == null) {
            Log.debug("Undefined incoming packet: " + PacketUtils.toPacketId(packetId) + " [" + version + "|" + state + "]");
            return;
        }

        Log.debug("Received packet %s(%s) [%s|%s] (%d bytes)", packet.toString(), PacketUtils.toPacketId(packetId), version, state, msg.readableBytes());

        try {
            packet.decode(msg, version);
        } catch (Exception e) {
            throw new DecoderException("Cannot decode packet " + PacketUtils.toDetailedInfo(packet, packetId, version, state), e);
        }

        if (buf.isReadable()) {
            throw new DecoderException("Packet " + PacketUtils.toDetailedInfo(packet, packetId, version, state) + " larger than expected, extra bytes: " + msg.readableBytes());
        }

        ctx.fireChannelRead(packet);
    }

    public void updateVersion(Version version) {
        this.version = version;
    }

    public void updateState(State state) {
        this.state = state;
        this.mappings = state.serverBound.getRegistry(version);
    }
}
