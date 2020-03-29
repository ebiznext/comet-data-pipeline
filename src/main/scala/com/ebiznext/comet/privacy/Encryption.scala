package com.ebiznext.comet.privacy

import scala.util.Random

/**
  * Several encryption methods used in privacy management
  */
object Encryption {
  def algo(alg: String, data: String): String = {
    val m = java.security.MessageDigest.getInstance(alg)
    val b = data.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest())
      .toString(16)
      .reverse
      .padTo(32, '0')
      .reverse
      .mkString
  }
}

trait Encryption {
  def encrypt(s: String): String
  def encrypt(s: String, params: List[Any]): String = encrypt(s)
}

object Md5 extends Encryption {
  def encrypt(s: String): String = Encryption.algo("MD5", s)
}

object Sha1 extends Encryption {
  def encrypt(s: String): String = Encryption.algo("SHA-1", s)
}

object Sha256 extends Encryption {
  def encrypt(s: String): String = Encryption.algo("SHA-256", s)
}

object Sha512 extends Encryption {
  def encrypt(s: String): String = Encryption.algo("SHA-512", s)
}

object Hide extends Encryption {
  def encrypt(s: String): String = ""
}

object No extends Encryption {
  def encrypt(s: String): String = s
}

object Initials extends Encryption {
  def encrypt(s: String): String = {
    s.split("\\s+").map(_.substring(0, 1)).mkString(".")
  }
}

object Email extends Encryption {
  def encrypt(s: String): String = encrypt(s, List("MD5"))
  override def encrypt(s: String, params: List[Any]): String = {
    assert(params.length == 1)
    val split = s.split('@')
    Encryption.algo(params.head.toString, split(0)) + "@" + split(1)
  }
}

trait IP extends Encryption {
  def separator: Char
  def encrypt(s: String): String = encrypt(s, 1)
  override def encrypt(s: String, params: List[Any]): String = {
    assert(params.length == 1)
    encrypt(s, params.head.asInstanceOf[Int])
  }

  def encrypt(s: String, maskBytes: Int): String = {
    val ip = s.split(separator)
    (ip.dropRight(maskBytes) ++ List.fill(maskBytes)(0)).mkString(separator.toString)
  }
}

object IPv4 extends IP {
  override val separator: Char = '.'
}

object IPv6 extends IP {
  override val separator: Char = ':'
}

trait Approx extends Encryption {
  val rnd = new Random()
  override def encrypt(s: String): String = encrypt(s.toDouble, 100).toString
  override def encrypt(s: String, params: List[Any]): String = {
    assert(params.length == 1)
    encrypt(s.toDouble, params.head.asInstanceOf[Int]).toString
  }


  def encrypt(value: Double, percent: Int): Double = {
    val rndBool = rnd.nextBoolean()
    val distance = (value * percent * rnd.nextDouble()) / 100
    if (rndBool)
      value - distance
    else
      value + distance
  }
}

object Mask extends Encryption {
  override def encrypt(s: String): String = encrypt(s, 'X', 8, 1, 1)
  override def encrypt(s: String, params: List[Any]): String = {
    assert(params.length == 4)
    val maskingChar = params(0).asInstanceOf[Char]
    val numberOfChars = params(1).asInstanceOf[Int]
    val leftSide = params(2).asInstanceOf[Int]
    val rightSide = params(3).asInstanceOf[Int]
    encrypt(s, maskingChar, numberOfChars, leftSide, rightSide)
  }

  def encrypt(
    s: String,
    maskingChar: Char,
    numberOfChars: Int,
    leftSide: Int,
    rightSide: Int
  ): String = {
    s match {
      case input if input.length <= leftSide =>
        "%s%s".format(input, maskingChar.toString * numberOfChars)
      case _ =>
        "%s%s%s".format(
          s.take(leftSide),
          maskingChar.toString * numberOfChars,
          s.takeRight(rightSide)
        )
    }
  }
}
