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

package ua.nanit.limbo.server;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public final class Log {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("Limbo");
    private static int debugLevel = Level.INFO.getIndex();

    private Log() {}

    public static void info(Object msg, Object... args) {
        LOGGER.info(String.format(msg.toString(), args));
    }

    public static void debug(Object msg, Object... args) {
        LOGGER.debug(String.format(msg.toString(), args));
    }

    public static void warning(Object msg, Object... args) {
        LOGGER.warn(String.format(msg.toString(), args));
    }

    public static void warning(Object msg, Throwable t, Object... args) {
        LOGGER.warn(String.format(msg.toString(), args), t);
    }

    public static void error(Object msg, Object... args) {
        LOGGER.error(msg.toString(), args);
    }

    public static void error(Object msg, Throwable t, Object... args) {
        LOGGER.error(String.format(msg.toString(), args), t);
    }

    public static boolean isDebug() {
        return debugLevel >= Level.DEBUG.getIndex();
    }

    static void setLevel(int level) {
        debugLevel = level;

        Logger logback = getRootLogger();

        if (logback != null) {
            logback.setLevel(convertLevel(level));
        }
    }

    private static Logger getRootLogger() {
        return (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    private static ch.qos.logback.classic.Level convertLevel(int level) {
        switch (level) {
            case 0:
                return ch.qos.logback.classic.Level.ERROR;
            case 1:
                return ch.qos.logback.classic.Level.WARN;
            case 2:
                return ch.qos.logback.classic.Level.INFO;
            case 3:
                return ch.qos.logback.classic.Level.DEBUG;
            default:
                throw new IllegalStateException("Undefined log level: " + level);
        }
    }

    public enum Level {

        ERROR("ERROR", 0),
        WARNING("WARNING", 1),
        INFO("INFO", 2),
        DEBUG("DEBUG", 3);

        private final String display;
        private final int index;

        Level(String display, int index) {
            this.display = display;
            this.index = index;
        }

        public String getDisplay() {
            return display;
        }

        public int getIndex() {
            return index;
        }
    }
}
