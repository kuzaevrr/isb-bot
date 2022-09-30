package ru.isb.bot.enums;

public enum Commands {

    SCHEDULE("/schedule"),
    SCHEDULE_GROUP("/schedule@ISBNotPiBot");

    private String command;

    Commands(String command) {
        this.command = command;
    }



    public String getCommand() {
        return command;
    }
}