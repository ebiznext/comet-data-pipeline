package com.ebiznext.comet.job.convert

import com.ebiznext.comet.TestHelper
import com.ebiznext.comet.privacy.{Encryption, Initials}

class EncryptionSpec extends TestHelper {

  new WithSettings() {
    "Parsing a single arg encryption algo" should "succeed" in {
      val (algo, params)  = Encryption.parse("com.ebiznext.comet.privacy.Approx(10)")
      algo should equal("com.ebiznext.comet.privacy.Approx")
      params.head shouldBe a [Int]
      params should equal (List(10))
    }
    "Parsing a multiple arg encryption algo" should "succeed" in {
      val (algo, params)  = Encryption.parse("package.SomeAlgo('X', \"Hello\", 12, false, 12.34)")
      algo should equal("package.SomeAlgo")
      params should have length 5
      params(0) shouldBe a [Char]
      params(1) shouldBe a [String]
      params(2) shouldBe a [Int]
      params(3) shouldBe a [Boolean]
      params(4) shouldBe a [Double]
      params should equal (List('X', "Hello", 12, false, 12.34))
    }

    "Initials Masking Firstname" should "succeed" in {
      val result = Initials.encrypt("John")
      result shouldBe "J."
    }

    "Initials Masking Composite name" should "succeed" in {
      val result = Initials.encrypt("John Doe")
      result shouldBe "J.D."
    }

  }
}
