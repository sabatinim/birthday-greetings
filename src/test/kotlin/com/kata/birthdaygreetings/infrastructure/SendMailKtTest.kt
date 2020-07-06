package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.dumbster.smtp.SimpleSmtpServer
import com.dumbster.smtp.SmtpMessage
import com.kata.birthdaygreetings.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime


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

        val now = LocalDateTime.now()
        val todayDateOfBirth = DateOfBirth(now.dayOfMonth, now.monthValue, now.year)

        val tomorrow = LocalDateTime.now().plusDays(1)
        val tomorrowDateOfBirth = DateOfBirth(tomorrow.dayOfMonth, tomorrow.monthValue, tomorrow.year)

        val employees = Employees(
            listOf(
                employeeBirthday(todayDateOfBirth,"TODAY_EMPLOYEE"),
                employeeBirthday(tomorrowDateOfBirth,"TOMORROW_EMPLOYEE")
            )
        )

        val loadEmployeeFromFile = { Right(employees)}

        val sendMailTo: (BirthdayEmployees) -> Either<MyError, Unit> =
            sendMail("localhost", 9999)

        val sendGreetings = sendGreetings(loadEmployeeFromFile, todayBirthdayEmployees, sendMailTo)

        sendGreetings()

        val message = mailServer.receivedEmail.next() as SmtpMessage
        assertThat(message.body).isEqualTo("Happy birthday, dear TODAY_EMPLOYEE!")

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

    private fun employeeBirthday(
        todayDateOfBirth: DateOfBirth,
        fistName: String
    ) = Employee(firstName = fistName,dateOfBirth = todayDateOfBirth)

}