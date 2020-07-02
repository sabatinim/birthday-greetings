package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.kata.birthdaygreetings.domain.*
import java.io.File
import java.io.FileNotFoundException

fun loadEmployeeFrom(fileName: String): () -> Either<MyError, Employees> = {
    try {
        Right(Employees(File(fileName).bufferedReader().readLines().map { toEmployee(it) }))
    } catch (e: FileNotFoundException) {
        Left(MyError.LoadEmployeesError("File $fileName doesn't exist"))
    }
}

private fun toEmployee(row: String): Employee {
    val rowSplitted = row.split(",")
    return Employee(
        rowSplitted[0],
        rowSplitted[1],
        DateOfBirth(
            rowSplitted[2].split("/")[0].toInt(),
            rowSplitted[2].split("/")[1].toInt(),
            rowSplitted[2].split("/")[2].toInt()
        )
        , EmailAddress(rowSplitted[3])
    )
}
