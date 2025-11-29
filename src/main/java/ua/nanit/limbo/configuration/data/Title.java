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
import ua.nanit.limbo.protocol.NbtMessage;
import ua.nanit.limbo.util.NbtMessageUtil;

@ConfigSerializable
@Data
public class Title {

    private boolean enable = true;
    @Comment("Set title text value empty, if you need only subtitle")
    private NbtMessage title = NbtMessageUtil.create("{\"text\": \"&9&lWelcome!\"}");
    @Comment("Set subtitle text value empty, if you need only title")
    private NbtMessage subtitle = NbtMessageUtil.create("{\"text\": \"&6NanoLimbo\"}");
    @Comment("Fade in time in ticks (1 sec = 20 ticks)")
    private int fadeIn = 10;
    @Comment("Stay time in ticks")
    private int stay = 100;
    @Comment("Fade out time in ticks")
    private int fadeOut = 10;
}
