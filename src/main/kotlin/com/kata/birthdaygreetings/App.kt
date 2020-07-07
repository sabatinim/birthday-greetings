package com.kata.birthdaygreetings

import com.dumbster.smtp.SimpleSmtpServer
import com.kata.birthdaygreetings.domain.sendGreetings
import com.kata.birthdaygreetings.domain.todayBirthdayEmployees
import com.kata.birthdaygreetings.infrastructure.*

fun main() {

    val mailServer = SimpleSmtpServer.start(9999)

    val sendGreetings =
        sendGreetings(
            loadEmployeeFrom("employeesStorage.txt"),
            todayBirthdayEmployees,
            sendMail(emailMessageFrom (sessionFor(MailConfiguration("localhost", 9999))))
        )

    val sendGreetingsResult = sendGreetings()

    println(sendGreetingsResult)

    mailServer.receivedEmail.forEach(::println)

    mailServer.stop()
}