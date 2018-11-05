package com.acevedo.domain

import java.util.{Calendar, Date}

import com.acevedo.domain.Balance.Amount
import com.acevedo.domain.fp.Types.{Safe, SafeAccount}
import scalaz.{-\/, \/, \/-}

sealed trait Account {
  def no: String
  def name: String
  def dateOfOpen: Date
  def dateOfClose: Option[Date]
  def balance: Balance
}


final case class CheckingAccount private(no: String, name: String, dateOfOpen: Date, dateOfClose: Option[Date], balance: Balance) extends Account
final case class SavingsAccount private(no: String, name: String, rateOfInterest: Amount, dateOfOpen: Date, dateOfClose: Option[Date], balance: Balance) extends Account

object Account {
  def today: Date = Calendar.getInstance.getTime

  def checkingAccount(no: String, name: String, dateOfOpen: Date, dateOfClose: Option[Date], balance: Balance): Safe[CheckingAccount] = {
    for {
      _ <- nameCheck(no, name)
      _ <- openDateCheck(dateOfOpen)
      _ <- closDateCheck(dateOfOpen, dateOfClose)
    } yield CheckingAccount(no, name, dateOfOpen, dateOfClose, balance)
  }

  def savingsAccount(no: String, name: String, rateOfInterest: Amount, dateOfOpen: Date, dateOfClose: Option[Date], balance: Balance): SafeAccount = {
    for {
      _ <- nameCheck(no, name)
      _ <- openDateCheck(dateOfOpen)
      _ <- closDateCheck(dateOfOpen, dateOfClose)
    } yield SavingsAccount(no, name, rateOfInterest, dateOfOpen, dateOfClose, balance)
  }

  private def nameCheck(no: String, name: String): Safe[(String, String)] =
    if (no.isEmpty || name.isEmpty) {
      -\/(new Exception("Missing basic info to open an account"))
    } else {
      \/-((no, name))
    }

  private def openDateCheck(dateOfOpen: Date): Safe[Date] = {
    if (dateOfOpen before  today) {
      -\/(new Exception("Opening date can't be in the past"))
    } else {
      \/-(dateOfOpen)
    }
  }

  private def closDateCheck(dateOfOpen: Date, dateOfClose: Option[Date]): Safe[(Date, Option[Date])] = {
    if (dateOfClose.getOrElse(today) before  dateOfOpen) {
      -\/(new Exception("Closing date can't be before than opening"))
    } else {
      \/-((dateOfOpen, dateOfClose))
    }
  }
}