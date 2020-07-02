package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either
import arrow.core.Left
import com.dumbster.smtp.SimpleSmtpServer
import com.dumbster.smtp.SmtpMessage
import com.kata.birthdaygreetings.domain.BirthdayEmployees
import com.kata.birthdaygreetings.domain.EmailAddress
import com.kata.birthdaygreetings.domain.Employee
import com.kata.birthdaygreetings.domain.MyError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class SendMailKtTest {

    lateinit var mailServer: SimpleSmtpServer

    @BeforeEach
    internal fun setUp() {
        mailServer = SimpleSmtpServer.start(9999)
    }

    @AfterEach
    internal fun tearDown() {
        mailServer.stop()
    }

    @Test
    internal fun sendMail() {

        val sendMailTo: (BirthdayEmployees) -> Either<MyError, Unit> =
            sendMail("localhost", 9999)

        sendMailTo(
            BirthdayEmployees(
                listOf(
                    Employee(
                        firstName = "NAME",
                        emailAddress = EmailAddress("employee@email.com")
                    )
                )
            )
        )

        val message = mailServer.receivedEmail.next() as SmtpMessage

        assertThat(message.body).isEqualTo("Happy birthday, dear NAME!")
    }

    @Test
    internal fun sendMailGoesInError() {

        val sendMailTo: (BirthdayEmployees) -> Either<MyError, Unit> =
            sendMail("localhost", 99)

        val result = sendMailTo(
            BirthdayEmployees(
                listOf(
                    Employee()
                )
            )
        )

        assertThat(result).isEqualTo(Left(MyError.SendMailError("Couldn't connect to host, port: localhost, 99; timeout -1")))
    }
}