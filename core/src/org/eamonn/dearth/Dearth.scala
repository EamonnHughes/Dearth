package org.eamonn.dearth

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Cursor.SystemCursor
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.{Color, GL20, PerspectiveCamera, VertexAttribute, VertexAttributes}
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g3d.attributes.{ColorAttribute, TextureAttribute}
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.{Environment, Material, Model, ModelBatch, ModelInstance}
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.{Matrix4, Vector2, Vector3}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input, InputProcessor}
import org.eamonn.dearth.Dearth.{Fingerer, Hand, Square, Weapon}
import org.eamonn.dearth.scenes.Home
import org.eamonn.dearth.util.{GarbageCan, TextureWrapper}

class Dearth extends ApplicationAdapter with InputProcessor {
  import Dearth.garbage

  private val idMatrix = new Matrix4()
  private var batch: PolygonSpriteBatch = _
  private var scene: Scene = _
  var keysPressed: List[Int] = List.empty
  var lX = 0f
  var lY = 0f
  var lZ = 0f
  var mx = 0
  var my = 0
  var wallLocs = List[Vec2](Vec2(0, 0), Vec2(1, 0)
    , Vec2(3, 0)
    , Vec2(4, 0)
    , Vec2(4, 1)
    , Vec2(4, 2)
    , Vec2(4, 3)
    , Vec2(4, 4)
    , Vec2(4, 5)
    , Vec2(1, 5)
    , Vec2(0, 5)
    , Vec2(-1, 5)
    , Vec2(-1, 4)
    , Vec2(-1, 3)
    , Vec2(-1, 2)
    , Vec2(-1, 1)
    , Vec2(-1, 0)
    , Vec2(2, 5)
    , Vec2(3, 5)
  )
  val texCoords: Array[Float] =
    Array(
      0.0f, 0.0f,
      1.0f, 0.0f,
      0.0f, 1.0f,
      1.0f, 1.0f
    )

  var camera: PerspectiveCamera = _
  var modelBatch: ModelBatch = _
  var modelBuilder: ModelBuilder = _
  var isMiddleFinger = false
  var box: Model = _
  var modelInstance: ModelInstance = _
  var switchedFinger = false
  var environment: Environment = _


  override def create(): Unit = {
    Gdx.input.setCatchKey(Input.Keys.BACK, true)
    Gdx.graphics.setSystemCursor(SystemCursor.Crosshair)
    batch = garbage.add(new PolygonSpriteBatch())
    camera = new PerspectiveCamera(75, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
    camera.position.set(0f, 0f, 3f)
    camera.near=0.1f
    camera.far =  300f
    modelBatch = new ModelBatch()
    modelBuilder =new ModelBuilder()
    box = modelBuilder.createBox(1f, 2f, 1f,
      new Material(TextureAttribute.createDiffuse(TextureWrapper.load("WallTexture.png"))),
      VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates    )
    modelInstance = new ModelInstance(box, 0, 0, 0)
    environment = new Environment()
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
    Gdx.input.setInputProcessor(this)
    Dearth.Weapon = TextureWrapper.load("Dagger.png")
    Dearth.Square = TextureWrapper.load("Square.png")
    Dearth.Hand = TextureWrapper.load("fingerless.png")
    Dearth.Fingerer = TextureWrapper.load("hand.png")
    //    Dearth.sound = Dearth.loadSound("triangle.mp3")
    //Text.loadFonts()
  }

  override def render(): Unit = {
    Gdx.input.setCursorCatched(true)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT)
    update()
    camera.update()
    modelInstance = new ModelInstance(box, lX, lY, lZ)
    modelBatch.begin(camera)
    wallLocs.foreach(wall => {

    modelBatch.render(new ModelInstance(box, wall.x, lY, wall.y) , environment)
  })
    modelBatch.end()
    batch.begin()
    batch.draw(Square, 0f, 0f, Gdx.graphics.getWidth/8, Gdx.graphics.getWidth/2)
    batch.draw(Weapon, Gdx.graphics.getWidth*5/8, 0f, Gdx.graphics.getWidth/4, Gdx.graphics.getWidth/2)
    batch.draw(Square, Gdx.graphics.getWidth*7/8, 0f, Gdx.graphics.getWidth/8, Gdx.graphics.getWidth/2)
    if(isMiddleFinger){
      batch.draw(Fingerer, Gdx.graphics.getWidth/8, 0f, Gdx.graphics.getWidth/4, Gdx.graphics.getWidth/4)
    } else {
      batch.draw(Hand, Gdx.graphics.getWidth/ 8, 0f, Gdx.graphics.getWidth / 4, Gdx.graphics.getWidth / 4)
    }
    batch.end()
  }

  def update(): Unit = {
    if (keysPressed.contains(Keys.W)) {
      lZ = .1f
    } else if (keysPressed.contains(Keys.S)) {
      lZ = -.1f
    } else {
      lZ = 0f
    }

    if(keysPressed.contains(Keys.Q) && !switchedFinger){
      isMiddleFinger = !isMiddleFinger
      switchedFinger = true
    }
    if(!keysPressed.contains(Keys.Q)){
      switchedFinger = false
    }
    var sideways = camera.direction.cpy()
    sideways.rotate(Vector3.Y, 90f)
    if (keysPressed.contains(Keys.A)) {
      lX = .1f
    } else if (keysPressed.contains(Keys.D)) {
      lX = -.1f
    } else {
      lX = 0f
    }

    camera.position.mulAdd(camera.direction, lZ)
    camera.position.set(camera.position.x, 0, camera.position.z)
    camera.position.mulAdd(sideways, lX)
  }

  override def dispose(): Unit = {
    garbage.dispose()
  }

  private def setScene(newScene: Scene): Unit = {
    scene = newScene
    Gdx.input.setInputProcessor(scene.init())
  }

  override def keyDown(keycode: Int): Boolean = {
    if(!keysPressed.contains(keycode)){
      keysPressed = keycode :: keysPressed
    }
  true
  }


  override def keyUp(keycode: Int): Boolean = {
      keysPressed = keysPressed.filterNot(k => k == keycode)

    true
  }

  override def keyTyped(character: Char): Boolean = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    camera.rotate( new Vector3(0f, 1f, 0f), ( mx - screenX)*.8f)
    //camera.rotate(new Vector3(1f, 0f, 0f), ( my - screenY)*.8f)
    mx = screenX
    //my = screenY
    false
  }

  override def scrolled(amountX: Float, amountY: Float): Boolean = false
}

object Dearth {
  implicit val garbage: GarbageCan = new GarbageCan

  def screenUnit = (Geometry.ScreenWidth min Geometry.ScreenHeight) / 40
  var sound: Sound = _
  var Square: TextureWrapper = _
  var Weapon: TextureWrapper = _
  var Hand: TextureWrapper = _
  var Fingerer: TextureWrapper = _
  var Circle: TextureWrapper = _

  def mobile: Boolean = isMobile(Gdx.app.getType)

  private def isMobile(tpe: ApplicationType) =
    tpe == ApplicationType.Android || tpe == ApplicationType.iOS

  private def loadSound(path: String)(implicit garbage: GarbageCan): Sound =
    garbage.add(Gdx.audio.newSound(Gdx.files.internal(path)))

}
