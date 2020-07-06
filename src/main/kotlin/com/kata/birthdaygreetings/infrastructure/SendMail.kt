package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.either.monad.map
import arrow.core.extensions.list.traverse.sequence
import com.kata.birthdaygreetings.domain.BirthdayEmployees
import com.kata.birthdaygreetings.domain.Employee
import com.kata.birthdaygreetings.domain.GreetingsEmail
import com.kata.birthdaygreetings.domain.MyError
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


private fun sendmail(mimeMessage: MimeMessage) =
        Transport.send(mimeMessage)

data class MailConfiguration(val smtpHost: String, val smtpPort: Long)

private fun greetingsEmailFrom(e: Employee): GreetingsEmail =
    GreetingsEmail(
        "Subject: Happy birthday!",
        "Happy birthday, dear ${e.firstName}!",
        e.emailAddress.value
    )

fun toMailMessage(f:()-> Session): (GreetingsEmail) -> MimeMessage =
    {
        val msg = MimeMessage(f())
        msg.setFrom(InternetAddress("sender@email.com"))
        msg.setRecipient(Message.RecipientType.TO, InternetAddress(it.emailAddress))
        msg.subject = it.subject
        msg.setText(it.body)
        msg
    }

fun sessionFor(mailConfiguration: MailConfiguration): Session {
    val props = Properties()
    props["mail.smtp.host"] = mailConfiguration.smtpHost
    props["mail.smtp.port"] = "" + mailConfiguration.smtpPort
    return Session.getInstance(props, null)
}

fun sendMail(toMailMessage: (GreetingsEmail) -> MimeMessage): (BirthdayEmployees) -> Either<MyError, Unit> = {
    try {
        it.employeeGroup
            .map(::greetingsEmailFrom)
            .map(toMailMessage)
            .map(::sendmail)
            .let { Right(Unit) }
    } catch (e: Exception) {
        Left(MyError.SendMailError(e.localizedMessage))
    }


}



