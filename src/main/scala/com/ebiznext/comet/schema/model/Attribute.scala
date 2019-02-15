package com.ebiznext.comet.schema.model

import java.util.regex.Pattern

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.spark.sql.types._


/**
  * A field in the schema. For struct fields, the field "attributes" contains all sub attributes
  *
  * @param name       : Attribute name as defined in the source dataset
  * @param `type`     : semantic type of the attribute
  * @param array      : Is it an array ?
  * @param required   : Should this attribute always be present in the source
  * @param privacy    : Shoudl this attribute be applied a privacy transformaiton at ingestion time
  * @param comment    : free text for attribute description
  * @param rename     : If present, the attribute is renamed with this name
  * @param stat       : If present, what kind of stat should be computed for this field
  * @param attributes : List of sub-attributes
  */
case class Attribute(
  name: String,
  `type`: String = "string",
  array: Option[Boolean] = None,
  required: Boolean = true,
  privacy: Option[PrivacyLevel] = None,
  comment: Option[String] = None,
  rename: Option[String] = None,
  stat: Option[Stat] = None,
  attributes: Option[List[Attribute]] = None
) {

  /**
    *
    * Spark Type if this attribute is a primitive type of array of primitive type
    *
    * @param types : List of gloablly defined types
    * @return Primitive type if attribute is a leaf node or array of primitive type, None otherwise
    */
  def primitiveSparkType(types: Types): DataType = {
    types.types
      .find(_.name == `type`)
      .map(_.primitiveType)
      .map { tpe =>
        if (isArray())
          ArrayType(tpe.sparkType, !required)
        else
          tpe.sparkType
      }
      .getOrElse(PrimitiveType.struct.sparkType)
  }

  /**
    * Go get recursively the Spark tree type of this object
    *
    * @param types : List of globalluy defined types
    * @return Spark type of this attribute
    */
  def sparkType(types: Types): DataType = {
    val tpe = primitiveSparkType(types)
    tpe match {
      case _: StructType =>
        attributes.map { attrs =>
          val fields = attrs.map { attr =>
            StructField(attr.name, attr.sparkType(types), !attr.required)
          }
          if (isArray())
            ArrayType(StructType(fields))
          else
            StructType(fields)
        } getOrElse (throw new Exception("Should never happen: empty list of attributes"))

      case simpleType => simpleType
    }
  }

  /**
    * @return renamed column if defined, source name otherwise
    */
  @JsonIgnore
  def getFinalName(): String = rename.getOrElse(name)

  def getPrivacy(): PrivacyLevel = this.privacy.getOrElse(PrivacyLevel.NONE)

  def isArray(): Boolean = this.array.getOrElse(false)

  def isRequired(): Boolean = Option(this.required).getOrElse(false)

  def getStat(): Stat = this.stat.getOrElse(Stat.NONE)

}

/**
  * Companion object used to check the attribute validation
  */
object Attribute {

  /**
    * Check attribute validity
    * An attribute is valid if :
    *     - Its name is a valid identifier
    *     - its type is defined
    *     - When a privacy function is defined its primitive type is a string
    *
    * @param types : List of defined types
    * @param att : An attrbute
    * @return The arribute if is valid
    */
  def checkValidity(att: Attribute,types: List[Type]): ValidationResult[Attribute] = {
    def validateNullType(`type`: String): ValidationResult[String] =
      if (`type` == null) Invalid(List(s"$att : unspecified type")) else Valid(`type`)

    def validateColNamePattern(name: String): ValidationResult[String] = {
      val colNamePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]{1,767}")
      if (!colNamePattern.matcher(name).matches())
        Invalid(List(s"attribute with name $name should respect the pattern ${colNamePattern.pattern()}"))
      else
        Valid(name)
    }

    def validatePrimType(`type`: String, privacyLevel: PrivacyLevel,types: List[Type],attributes: Option[List[Attribute]]): ValidationResult[String] = {
      val primitiveType = types.find(_.name == `type`).map(_.primitiveType).getOrElse(None)

      def checkPrimitivePrivacy() : ValidationResult[String]=
        if (primitiveType != PrimitiveType.string && privacyLevel != PrivacyLevel.NONE)
          Invalid(List(s"Attribute $att : string is the only supported primitive type for an attribute when privacy is requested"))
        else
          Valid(`type`)


      def checkPrimitiveStructure()  : ValidationResult[String] =
        if (primitiveType == PrimitiveType.struct && attributes.isEmpty)
          Invalid(List(s"Attribute $att : Struct types have at least one attribute."))
        else
          Valid(`type`)


      def checkPrimitiveSub()  : ValidationResult[String] = {
        if (primitiveType != PrimitiveType.struct && attributes.isDefined)
          Invalid(List(s"Attribute $att : Simple attributes cannot have sub-attributes"))
        else
          Valid(`type`)
      }

      def checkPrimitiveType()  : ValidationResult[String] = {
        if (primitiveType == None && attributes.isEmpty)
          Invalid(List(s"Invalid Type ${`type`}"))
        else
          Valid(`type`)
      }

      checkPrimitiveType *> checkPrimitivePrivacy *> checkPrimitiveStructure *> checkPrimitiveSub

    }

    def validateAttributes(attributes: Option[List[Attribute]]): ValidationResult[Option[List[Attribute]]]= {
      val attributeList = attributes.getOrElse(List())
      if(attributeList.isEmpty)
        Invalid(List(s"Attribute $att : when present, attributes list cannot be empty."))
      else
        Valid(attributes)
    }

    val nullTypeVal = validateNullType(att.`type`)
    val primTypeVal = validatePrimType(att.`type`, att.getPrivacy(),types, att.attributes)
    val globalTypeVal = nullTypeVal *> primTypeVal //productRight
    val attributeVal = validateAttributes(att.attributes)
    val colNameVal = validateColNamePattern(att.name)
    (globalTypeVal, colNameVal, attributeVal).mapN((gbType,colName,attrib) => Attribute(colName,gbType,att.array,att.required,att.privacy,
      att.comment, att.rename,att.stat,attrib))
  }
}
