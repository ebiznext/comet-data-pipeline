package com.ebiznext.comet.schema.model

import java.io.InputStream

import cats.data.Validated.Invalid
import com.ebiznext.comet.TestHelper
import org.scalatest.{FlatSpec, Matchers}

class SchemaSpec extends FlatSpec with Matchers with TestHelper {

  "Attribute type" should "be valid" in {
    val stream: InputStream =
      getClass.getResourceAsStream("/quickstart/metadata/types/default.yml")
    val lines =
      scala.io.Source.fromInputStream(stream).getLines().mkString("\n")
    val types = mapper.readValue(lines, classOf[Types])
    val attr = Attribute(
      "attr",
      "invalid-type", // should raise error non existent type
      Some(true),
      true,
      Some(PrivacyLevel.MD5) // Should raise an error. Privacy cannot be applied on types other than string
    )

    Attribute.checkValidity(attr,types.types) shouldBe  Invalid(List("Invalid Type invalid-type",s"Attribute $attr : string is the only supported primitive type for an attribute when privacy is requested" ,s"Attribute $attr : when present, attributes list cannot be empty."))
  }

  "Attribute privacy" should "be applied on string type only" in {
    val stream: InputStream =
      getClass.getResourceAsStream("/quickstart/metadata/types/default.yml")
    val lines =
      scala.io.Source.fromInputStream(stream).getLines().mkString("\n")
    val types = mapper.readValue(lines, classOf[Types])
    val attr = Attribute(
      "attr",
      "long",
      Some(true),
      true,
      Some(PrivacyLevel.MD5) // Should raise an error. Privacy cannot be applied on types other than string
    )
    Attribute.checkValidity(attr,types.types) shouldBe
    Invalid(
      List(
        s"Attribute $attr : string is the only supported primitive type for an attribute when privacy is requested",
        s"Attribute $attr : when present, attributes list cannot be empty."
      )
    )
  }

  "Sub Attribute" should "be present for struct types only" in {
    val stream: InputStream =
      getClass.getResourceAsStream("/quickstart/metadata/types/default.yml")
    val lines =
      scala.io.Source.fromInputStream(stream).getLines().mkString("\n")
    val types = mapper.readValue(lines, classOf[Types])
    val attr = Attribute(
      "attr",
      "long",
      Some(true),
      true,
      Some(PrivacyLevel.MD5), // Should raise an error. Privacy cannot be applied on types other than string
      attributes = Some(List[Attribute]())
    )
    val expectedErrors = List(
      s"Attribute $attr : string is the only supported primitive type for an attribute when privacy is requested",
      s"Attribute $attr : Simple attributes cannot have sub-attributes",
      s"Attribute $attr : when present, attributes list cannot be empty."
    )

    Attribute.checkValidity(attr,types.types) shouldBe Invalid(expectedErrors)
  }
}
