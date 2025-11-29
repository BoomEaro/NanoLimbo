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

package ua.nanit.limbo.configuration.data;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@Data
public class Traffic {

    @Comment("If true, then additional handler will be added to the channel pipeline")
    private boolean enable = true;
    @Comment("Max packet size in bytes\n" +
            "Unlimited if -1")
    private int maxPacketSize = 8192;
    @Comment("""
            The interval to measure packets over
            Lowering this value will limit peak packets from players which would target people with bad connections
            Raising this value will allow higher peak packet rates, which will help with people who have poor connections
            Ignored if -1.0""")
    private double interval = 7.0;
    @Comment("""
            The maximum packets per second for players
            It is measured over the configured interval
            Ignored if -1.0""")
    private double maxPacketRate = 500.0;
    @Comment("""
            The maximum packet bytes per second for players
            It is measured over the configured interval as an average bytes/sec
            Ignored if -1.0""")
    private double maxPacketBytesRate = 2048.0;

}
