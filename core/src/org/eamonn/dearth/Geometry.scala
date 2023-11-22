package org.eamonn.dearth

import com.badlogic.gdx.Gdx

object Geometry {

  def ScreenWidth: Float = (Gdx.graphics.getWidth.toFloat min Gdx.graphics.getHeight.toFloat)
  def ScreenHeight: Float = ScreenWidth

}
