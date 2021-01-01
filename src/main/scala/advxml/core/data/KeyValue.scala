package advxml.core.data

import cats.Eq

import scala.xml.Elem

case class Key(value: String) extends AnyVal with Serializable {
  def ==(that: String): Boolean = value == that
  def !=(that: String): Boolean = ! ==(that)
}
object Key {
  implicit val advxmlKeyCatsInstances: Eq[Key] = (x: Key, y: Key) => x == y.value
}

case class KeyValuePredicate(key: Key, private val valuePredicate: Value => Boolean) {

  def apply(t: Value): Boolean = valuePredicate(t)

  lazy val negate: KeyValuePredicate = copy(valuePredicate = t => !valuePredicate(t))

  override def toString: String = s"$key has value $valuePredicate"
}

case class AttributeData(key: Key, value: Value) {
  override def toString: String = s"""$key = $value"""
}

object AttributeData {

  def fromMap(m: Map[String, String]): List[AttributeData] =
    m.map { case (k, v) => AttributeData(Key(k), Value(v)) }.toList

  def fromElem(e: Elem): List[AttributeData] =
    fromMap(e.attributes.asAttrMap)
}
