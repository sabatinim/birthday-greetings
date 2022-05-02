package com.kata.birthdaygreetings

import com.dumbster.smtp.SimpleSmtpServer
import com.kata.birthdaygreetings.domain.sendGreetingsWith
import com.kata.birthdaygreetings.infrastructure.*

fun main() {

    val mailServer = SimpleSmtpServer.start(9999)

    val sendGreetings =
        sendGreetingsWith(
            loadEmployeeFrom("employeesStorage.txt"),
            sendMailWith("localhost", 9999)
        )

    val sendGreetingsResult = sendGreetings()

    println(sendGreetingsResult)

    mailServer.receivedEmail.forEach(::println)

    mailServer.stop()
}