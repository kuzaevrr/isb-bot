package ru.isb.bot.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Commands {

    STOP_SERVER("/stopServer"),
    SCHEDULE("/schedule"),
    SCHEDULE_GROUP("/schedule@ISBNotPiBot"),

    LIST("/list"),
    LIST_GROUP("/list@ISBNotPiBot"),
    NO_COMMAND(null);

    private final String command;

    @Override
    public String toString() {
        return this.command;
    }

    public static Commands fromString(String value) {
        if (value != null) {
            for (Commands pt : Commands.values()) {
                if (value.equalsIgnoreCase(pt.command)) {
                    return pt;
                }
            }
        }
        return NO_COMMAND;
    }
}