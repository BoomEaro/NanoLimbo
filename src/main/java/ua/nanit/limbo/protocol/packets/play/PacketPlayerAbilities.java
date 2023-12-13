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

package ua.nanit.limbo.protocol.packets.play;

import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

public class PacketPlayerAbilities implements PacketOut {

    private static final int FLAG_INVINCIBLE = 0x01;
    private static final int FLAG_FLYING = 0x02;
    private static final int FLAG_CAN_FLY = 0x04;
    private static final int FLAG_CREATIVE = 0x08;

    private boolean invincible;
    private boolean canFly;
    private boolean flying;
    private boolean creative;

    private float flySpeed;
    private float walkSpeed;

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }

    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        int flags = 0;
        if (this.invincible) {
            flags |= FLAG_INVINCIBLE;
        }

        if (this.canFly) {
            flags |= FLAG_CAN_FLY;
        }

        if (this.flying) {
            flags |= FLAG_FLYING;
        }

        if (this.creative) {
            flags |= FLAG_CREATIVE;
        }

        msg.writeByte(flags);

        msg.writeFloat(this.flySpeed);
        msg.writeFloat(this.walkSpeed);
    }

}
