package org.eamonn.dearth

import com.badlogic.gdx.math.Vector3

case class Player (var position: Vector3 = new Vector3(0, 0, 0)) {
  var size: Vector3 = new Vector3(0.3f, 1, 0.3f)
}
