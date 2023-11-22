package org.eamonn.dearth

import com.badlogic.gdx.backends.lwjgl3.{
  Lwjgl3Application,
  Lwjgl3ApplicationConfiguration
}

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
object DesktopLauncher extends App {
  val config = new Lwjgl3ApplicationConfiguration
  config.setForegroundFPS(60)
  val desktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
  config.setForegroundFPS(60)
  config.setFullscreenMode(desktopMode)
  new Lwjgl3Application(new Dearth, config)
}
