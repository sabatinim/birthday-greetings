package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.kata.birthdaygreetings.domain.BirthdayEmployees
import com.kata.birthdaygreetings.domain.Employee
import com.kata.birthdaygreetings.domain.GreetingsMessage
import com.kata.birthdaygreetings.domain.MyError
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


data class MailConfiguration(val smtpHost: String, val smtpPort: Long)

private fun sendmail(mimeMessage: MimeMessage) =
    Transport.send(mimeMessage)

private fun greetingsMessageFrom(e: Employee): GreetingsMessage =
    GreetingsMessage(
        "Subject: Happy birthday!",
        "Happy birthday, dear ${e.firstName}!",
        e.emailAddress.value
    )

private fun emailMessageFrom(f:()-> Session): (GreetingsMessage) -> MimeMessage =
    {
        val msg = MimeMessage(f())
        msg.setFrom(InternetAddress("sender@email.com"))
        msg.setRecipient(Message.RecipientType.TO, InternetAddress(it.recipient))
        msg.subject = it.subject
        msg.setText(it.body)
        msg
    }

private fun sessionFor(mailConfiguration: MailConfiguration): ()->Session = {
    val props = Properties()
    props["mail.smtp.host"] = mailConfiguration.smtpHost
    props["mail.smtp.port"] = "" + mailConfiguration.smtpPort
    Session.getInstance(props, null)
}

private fun sendMail(toMailMessage: (GreetingsMessage) -> MimeMessage): (BirthdayEmployees) -> Either<MyError, Unit> = {
    try {
        it.e.employeeGroup
            .map(::greetingsMessageFrom)
            .map(toMailMessage)
            .map(::sendmail)
            .let { Right(Unit) }
    } catch (e: Exception) {
        Left(MyError.SendMailError(e.localizedMessage))
    }
}

//PUBLIC API
fun sendMailWith(host:String, port:Long):(BirthdayEmployees) -> Either<MyError, Unit>  = sendMail(emailMessageFrom(sessionFor(MailConfiguration(host, port))))