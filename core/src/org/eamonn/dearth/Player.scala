package org.eamonn.dearth

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}

case class Player (var position: Vector3 = new Vector3(0, 0, 0)) {
  var size: Vector3 = new Vector3(0.3f, 1, 0.3f)
  var bodyDef: BodyDef = new BodyDef()
  bodyDef.`type` = BodyDef.BodyType.DynamicBody
  bodyDef.position.set(position.x, position.z)
  var shape = new PolygonShape()
  shape.setAsBox(size.x/2, size.z/2)
  var fixtureDef = new FixtureDef()
  fixtureDef.shape = shape
  fixtureDef.density = 1f


}
