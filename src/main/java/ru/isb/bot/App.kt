package ru.isb.bot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class IsbBotApplication

fun main(args: Array<String>) {
    SpringApplication.run(IsbBotApplication::class.java, *args)
}