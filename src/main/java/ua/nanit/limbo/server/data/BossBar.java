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

package ua.nanit.limbo.server.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ua.nanit.limbo.protocol.NbtMessage;
import ua.nanit.limbo.util.Colors;
import ua.nanit.limbo.util.NbtMessageUtil;

import java.lang.reflect.Type;
import java.util.Locale;

@Getter
@Setter
public class BossBar {

    private NbtMessage text;
    private float health;
    private Color color;
    private Division division;

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

    public static class Serializer implements TypeSerializer<BossBar> {
        @SneakyThrows
        @Override
        public BossBar deserialize(Type type, ConfigurationNode node) {
            BossBar bossBar = new BossBar();

            bossBar.setText(NbtMessageUtil.create(Colors.of(node.node("text").getString(""))));
            bossBar.setHealth(node.node("health").getFloat());

            if (bossBar.getHealth() < 0 || bossBar.getHealth() > 1)
                throw new SerializationException("BossBar health value must be between 0.0 and 1.0");

            try {
                bossBar.setColor(Color.valueOf(node.node("color").getString("").toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                throw new SerializationException("Invalid bossbar color");
            }

            try {
                bossBar.setDivision(Division.valueOf(node.node("division").getString("").toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                throw new SerializationException("Invalid bossbar division");
            }

            return bossBar;
        }

        @Override
        public void serialize(Type type, @Nullable BossBar obj, ConfigurationNode node) {}
    }
}
