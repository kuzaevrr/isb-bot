package ru.isb.bot.enums

import lombok.AllArgsConstructor

@AllArgsConstructor
enum class Commands(
    private val command: String
) {

    SCHEDULE("/schedule"),
    SCHEDULE_GROUP("/schedule@ISBNotPiBot"),

    LIST("/list"),
    LIST_GROUP("/list@ISBNotPiBot"),
    ALL_LINK("@all"),
    ALL("/all"),
    ALL_GROUP("/all@ISBNotPiBot"),

    HELP("/help"),
    NO_COMMAND("");

    override fun toString(): String = command

    companion object {
        fun fromString(value: String?): Commands {
            if (value != null) {
                for (pt in entries) {
                    if (value.equals(pt.command, ignoreCase = true) || value.contains(pt.command)) {
                        return pt
                    }
                }
            }
            return NO_COMMAND
        }
    }
}