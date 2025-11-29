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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import ua.nanit.limbo.protocol.NbtMessage;
import ua.nanit.limbo.util.NbtMessageUtil;

@ConfigSerializable
@Data
public class BossBar {

    private boolean enable = true;
    private NbtMessage text = NbtMessageUtil.create("{\"text\": \"Welcome to the Limbo!\"}");
    private float health = 1.0F;
    @Comment(value = "Available colors: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE", override = true)
    private Color color = Color.PINK;
    @Comment("Available divisions: SOLID, DASHES_6, DASHES_10, DASHES_12, DASHES_20")
    private Division division = Division.SOLID;

    @AllArgsConstructor
    @Getter
    public enum Color {
        PINK(0),
        BLUE(1),
        RED(2),
        GREEN(3),
        YELLOW(4),
        PURPLE(5),
        WHITE(6);

        private final int index;
    }

    @AllArgsConstructor
    @Getter
    public enum Division {
        SOLID(0),
        DASHES_6(1),
        DASHES_10(2),
        DASHES_12(3),
        DASHES_20(4);

        private final int index;
    }
}
