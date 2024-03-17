sealed trait ADown

case object RootDown extends ADown
case class IndexArray(index: Int, next: ADown) extends ADown
case class KeyArray(key: String, next: ADown) extends ADown
case class KeyObject(key: String, next: ADown, optional: Boolean = false)
    extends ADown

object Down {
  def parseDown(value: String): ADown = {
    def go(elements: List[String]): ADown = {
      elements match {
        case Nil => RootDown
        case head :: tail if (head.startsWith("[\"") && head.endsWith("\"]")) =>
          KeyObject(head.drop(2).dropRight(2), go(tail))
        case head :: tail
            if (head.startsWith("[\"") && head.endsWith("\"]?")) =>
          KeyObject(head.drop(2).dropRight(3), go(tail), optional = true)
        case head :: tail if head.endsWith("[]") =>
          KeyArray(head.dropRight(2), go(tail))
        case head :: tail if (head.endsWith("?")) =>
          KeyObject(head.dropRight(1), go(tail), optional = true)
        case head :: tail if (head.startsWith("[") && head.endsWith("]")) =>
          IndexArray(
            head.drop(1).dropRight(1).toInt,
            go(tail)
          )
        case head :: tail => KeyObject(head, go(tail))
      }
    }

    // value.split(" | ").toList.map(p => go(p.split("\\.").toList))
    go(
      value
        .split("\\|")
        .map(_.trim)
        .toList
        .flatMap(_.split("\\.").toList)
        .filter(_.nonEmpty)
    )
  }
}
