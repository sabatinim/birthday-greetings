package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.kata.birthdaygreetings.domain.BirthdayEmployees
import com.kata.birthdaygreetings.domain.Employee
import com.kata.birthdaygreetings.domain.MyError
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


fun sendMail(smtpHost: String, smtpPort: Long): (BirthdayEmployees) -> Either<MyError, Unit> = {
    try {
        it.employeeGroup
            .map { sendmail(it, sessionFor(smtpHost, smtpPort)) }
            .let { Right(Unit) }
    } catch (e: Exception) {
        Left(MyError.SendMailError(e.localizedMessage))
    }
}

private fun sessionFor(smtpHost: String, smtpPort: Long): Session {
    val props = Properties()
    props["mail.smtp.host"] = smtpHost
    props["mail.smtp.port"] = "" + smtpPort
    val session = Session.getInstance(props, null)
    return session
}

private fun sendmail(e: Employee, session: Session) {
    val msg = MimeMessage(session)
    msg.setFrom(InternetAddress("sender@email.com"))
    msg.setRecipient(Message.RecipientType.TO, InternetAddress("employee@email.com"))
    msg.subject = "Subject: Happy birthday!"
    msg.setText("Happy birthday, dear ${e.firstName}!")
    Transport.send(msg)
}