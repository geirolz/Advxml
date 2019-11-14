package com.github.geirolz.advxml.convert

import cats.data.Kleisli
import cats.data.Validated.Valid
import com.github.geirolz.advxml.convert.impls.XmlConverter.{ModelToXml, XmlToModel}
import com.github.geirolz.advxml.validate.ValidatedEx
import org.scalatest.FunSuite

import scala.xml.Elem

/**
  * Advxml
  * Created by geirolad on 28/06/2019.
  *
  * @author geirolad
  */
class XmlConverterTest extends FunSuite {

  import cats.implicits._
  import com.github.geirolz.advxml.implicits.traverse.validated._
  import com.github.geirolz.advxml.implicits.convert._

  test("XML to Model - Convert simple case class") {

    case class Person(name: String, surname: String, age: Option[Int])

    implicit val converter: XmlToModel[Elem, Person] = Kleisli(x => {
      (
        (x \@! "Name"),
        (x \@! "Surname"),
        (x \@? "Age").map(_.map(_.toInt))
      ).mapN(Person)
    })

    val xml = <Person Name="Matteo" Surname="Bianchi"/>
    val res: ValidatedEx[Person] = xml.as[Person]

    assert(res.isValid)
    assert(res.toOption.get.name == "Matteo")
    assert(res.toOption.get.surname == "Bianchi")
  }

  test("Model to XML - Convert simple case class") {

    case class Person(name: String, surname: String, age: Option[Int])

    implicit val converter: ModelToXml[Person, Elem] = Kleisli(
      x =>
        Valid(
          <Person Name={x.name} Surname={x.surname} Age={x.age.map(_.toString).getOrElse("")}/>
        )
    )

    val p = Person("Matteo", "Bianchi", Some(23))
    val res: ValidatedEx[Elem] = p.asXml[Elem]

    assert(res.isValid)
    assert(res.toOption.get == <Person Name="Matteo" Surname="Bianchi" Age="23"/>)
  }
}
