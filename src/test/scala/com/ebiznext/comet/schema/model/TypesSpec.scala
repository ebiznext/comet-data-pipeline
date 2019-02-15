package com.ebiznext.comet.schema.model

import java.io.InputStream

import cats.data.Validated.{Invalid, Valid}
import com.ebiznext.comet.TestHelper


class TypesSpec extends TestHelper {

  "Default types" should "be valid" in {
    val stream: InputStream =
      getClass.getResourceAsStream("/quickstart/metadata/types/default.yml")
    val lines =
      scala.io.Source.fromInputStream(stream).getLines().mkString("\n")
    val types = mapper.readValue(lines, classOf[Types])
    Types.checkValidity(types) shouldBe Valid(types)
  }

  "Duplicate  type names" should "be refused" in {
    val stream: InputStream =
      getClass.getResourceAsStream("/quickstart/metadata/types/default.yml")
    val lines = scala.io.Source
      .fromInputStream(stream)
      .getLines()
      .mkString("\n") +
    """
        |  - name: "long"
        |    primitiveType: "long"
        |    pattern: "-?\\d+"
        |    sample: "-64564"
      """.stripMargin

    val types = mapper.readValue(lines, classOf[Types])
    Types.checkValidity(types) shouldBe Invalid(
      List("Not good ! I have 2 for this occurence", "Not good ! I have 2 for this occurence")
    )

  }

  "Date / Time Pattern" should "be valid" in {
    val stream: InputStream =
      getClass.getResourceAsStream("/quickstart/metadata/types/default.yml")
    val lines = scala.io.Source
      .fromInputStream(stream)
      .getLines()
      .mkString("\n") +
    """
        |  - name: "timeinmillis"
        |    primitiveType: "timestamp"
        |    pattern: "epoch_milli"
        |    sample: "1548923449662"
        |  - name: "timeinseconds"
        |    primitiveType: "timestamp"
        |    pattern: "epoch_second"
        |    sample: "1548923449"
      """.stripMargin
    val types = mapper.readValue(lines, classOf[Types])

    Types.checkValidity(types) shouldBe Valid(types)

    "2019-01-31 09:30:49.662" shouldBe types.types
      .find(_.name == "timeinmillis")
      .get
      .sparkValue("1548923449662")
      .toString
    "2019-01-31 09:30:49.0" shouldBe types.types
      .find(_.name == "timeinseconds")
      .get
      .sparkValue("1548923449")
      .toString

  }

}
