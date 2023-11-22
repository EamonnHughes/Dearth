package org.eamonn.dearth

case class Level(points: String) {
  def toWallLocs: List[Vec2] = {
    val rows = points.split("\n")
    val width = rows.head.length
    val height = rows.size
    rows.zipWithIndex.flatMap({
      case (row, y) =>
        row.zipWithIndex.collect({
          case (char, x) if char == 'w' =>
            Vec2(x, y)
        })
    }).toList
  }
}
