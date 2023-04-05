package ru.isb.bot.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Commands {

    STOP_SERVER("/stopServer"),
    SCHEDULE("/schedule"),
    SCHEDULE_GROUP("/schedule@ISBNotPiBot"),

    LIST("/list"),
    LIST_GROUP("/list@ISBNotPiBot");

    private final String command;

    @Override
    public String toString() {
        return this.command;
    }
}