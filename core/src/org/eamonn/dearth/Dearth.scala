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
import org.eamonn.dearth.Dearth.{MiddleFinger, Hand, Square, Weapon}
import org.eamonn.dearth.scenes.Home
import org.eamonn.dearth.util.{GarbageCan, TextureWrapper}

class Dearth extends ApplicationAdapter with InputProcessor {
  import Dearth.garbage

  private val idMatrix = new Matrix4()
  private var batch: PolygonSpriteBatch = _
  private var scene: Scene = _
  var keysPressed: List[Int] = List.empty
  var player = Player()
  var lX = 0f
  var lY = 0f
  var lZ = 0f
  var mx = 0
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
  var floor: Model = _
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
    floor = modelBuilder.createBox(50f, 1f, 50f,
      new Material(TextureAttribute.createDiffuse(TextureWrapper.load("Floor.png"))),
      VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates    )
    modelInstance = new ModelInstance(box, 0, 0, 0)
    environment = new Environment()
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
    Gdx.input.setInputProcessor(this)
    Dearth.Weapon = TextureWrapper.load("Dagger.png")
    Dearth.Square = TextureWrapper.load("Square.png")
    Dearth.Hand = TextureWrapper.load("fingerless.png")
    Dearth.MiddleFinger = TextureWrapper.load("hand.png")
    //    Dearth.sound = Dearth.loadSound("triangle.mp3")
    Text.loadFonts()
  }

  override def render(): Unit = {
    Gdx.input.setCursorCatched(true)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT)
    update()
    camera.update()
    modelBatch.begin(camera)
    wallLocs.foreach(wall => {

    modelBatch.render(new ModelInstance(box, wall.x, 0, wall.y) , environment)
    })
        modelBatch.render(new ModelInstance(floor, player.position.x, -1.5f, player.position.z), environment)
        modelBatch.render(new ModelInstance(floor, player.position.x, 1.5f, player.position.z), environment)

    modelBatch.end()
    batch.begin()
    batch.draw(Square, 0f, 0f, Gdx.graphics.getWidth/8, Gdx.graphics.getWidth/2)
    batch.draw(Weapon, Gdx.graphics.getWidth*5/8, 0f, Gdx.graphics.getWidth/4, Gdx.graphics.getWidth/2)
    batch.draw(Square, Gdx.graphics.getWidth*7/8, 0f, Gdx.graphics.getWidth/8, Gdx.graphics.getWidth/2)
    Text.mediumFont.setColor(Color.BLACK)
    Text.mediumFont.draw(batch, "HELLO", 0f,  Gdx.graphics.getHeight)



    for(x <- player.position.x.round - 5 to player.position.x.round + 5){
      for(y <- player.position.z.round - 5 to player.position.z.round + 5) {
        if(wallLocs.exists(wall => wall.x == x && wall.y ==y)){
          batch.setColor(Color.RED)
          batch.draw(Square,  (5+x-player.position.x)*screenUnit, (5+y-player.position.z)*screenUnit, screenUnit, screenUnit)
          batch.setColor(Color.WHITE)
        }
      }
      }
    batch.setColor(Color.GREEN)
    batch.draw(Square, (4.85f) * screenUnit, (4.85f) * screenUnit, .3f*screenUnit, .3f*screenUnit)
    batch.setColor(Color.WHITE)
    if(isMiddleFinger){
      batch.draw(MiddleFinger, Gdx.graphics.getWidth/8, 0f, Gdx.graphics.getWidth/4, Gdx.graphics.getWidth/4)
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

    var posPos = player.position.cpy().mulAdd(camera.direction, lZ).mulAdd(sideways, lX)
    wallLocs.foreach(wall => {
      if(player.position.z + (player.size.z/2) >= wall.y && player.position.z - (player.size.z/2) <= wall.y + 1) {
        if (posPos.x + (player.size.x/2) >= wall.x && posPos.x - (player.size.x/2) <= wall.x + 1) {
          if (player.position.x + (player.size.x/2) <= wall.x) {
            posPos.x = wall.x - (player.size.x/2)
          } else if (player.position.x - (player.size.x/2) >= wall.x + 1) {
            posPos.x = wall.x + 1 + (player.size.x/2)
          }
        }


      }
      if(player.position.x + (player.size.x/2) >= wall.x && player.position.x - (player.size.x/2) <= wall.x + 1) {
        if(posPos.z + (player.size.z/2) >= wall.y && posPos.z - (player.size.z/2) <= wall.y + 1) {
        if (player.position.z + (player.size.z/2) <= wall.y) {
          posPos.z = wall.y - (player.size.z/2)
        } else if (player.position.z - (player.size.z/2) >= wall.y + 1) {
          posPos.z = wall.y + 1 + (player.size.z/2)
        }
      }}
    })
    player.position.set(posPos)

val xx = player.position.cpy()
    xx.sub(.5f,0, .5f)
    camera.position.set(xx)
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
  var MiddleFinger: TextureWrapper = _
  var Circle: TextureWrapper = _

  def mobile: Boolean = isMobile(Gdx.app.getType)

  private def isMobile(tpe: ApplicationType) =
    tpe == ApplicationType.Android || tpe == ApplicationType.iOS

  private def loadSound(path: String)(implicit garbage: GarbageCan): Sound =
    garbage.add(Gdx.audio.newSound(Gdx.files.internal(path)))

}
