sealed trait ADown

case object RootDown extends ADown
case class ArrayDown(index: Int, next: ADown) extends ADown
case class ObjectDown(key: String, next: ADown) extends ADown

object Down {
  def parseDown(value: String): ADown = {
    def go(elements: List[String]): ADown = {
      elements match {
        case Nil                          => RootDown
        case head :: tail if head.isEmpty => go(tail)
        case head :: tail if (head.startsWith("[\"") && head.endsWith("\"]")) =>
          ObjectDown(head.drop(2).dropRight(2), go(tail))
        case head :: tail if (head.startsWith("[") && head.endsWith("]")) =>
          ArrayDown(
            head.drop(1).dropRight(1).toInt,
            go(tail)
          )
        case head :: tail => ObjectDown(head, go(tail))
      }
    }

    go(value.split("\\.").toList)
  }
}
