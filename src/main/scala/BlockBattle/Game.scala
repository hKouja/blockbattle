package BlockBattle

import BlockBattle.Game.Color.{black, grass, mole, mole2, sky, soil, worm}

object Game {
  val windowSize = (30, 50)
  val windowTitle = "EPIC BLOCK BATTLE"
  val blockSize = 14
  val skyRange = 0 to 7
  val grassRange = 8 to 8

  object Color {
    import java.awt.Color as JColor

    val black  = new JColor (0, 0, 0)
    val mole   = new JColor (51, 51, 0)
    val mole2  = new JColor (60, 60, 60)
    val soil   = new JColor (153, 102, 51)
    val tunnel = new JColor (204, 153, 102)
    val grass  = new JColor (25, 130, 35)
    val sky    = new JColor (31, 190, 214)
    val worm   = new JColor (225, 100, 235)
  }
}

class Game (
             val leftPlayerName: String,
             val rightPlayerName: String
           ) {

  import Game._

  val window = new BlockWindow(windowSize, windowTitle, blockSize)

  val leftMole = new Mole (
    name = leftPlayerName,
    Pos (windowSize._1 * blockSize/ 2, windowSize._2 * blockSize /2),
    dir = (1,0),
    Color.mole,
    KeyControl("a", "d", "w", "s")
  )
  val rightMole = new Mole (
    name = rightPlayerName,
    Pos (1,2),
    dir = (1,1),
    Color.mole2,
    KeyControl("Left", "Right", "Up", "Down")
  )

  def place(n: Int): Vector [(Int, Int)] = {
    import scala.util.Random.nextInt
    (for (i <- 1 to n) yield (
      1 + nextInt(windowSize._1  * blockSize - 1),
      grassRange.last + 1 + nextInt(windowSize._2 - (grassRange.last + 1))*blockSize)).toVector
  }

  def worm(xs: Vector[(Int, Int)]): Unit = {
    for (i <- 0 until xs.length) {
      val localPos: Pos = Pos(xs(i)._1, xs(i)._2)
      window.setBlock(localPos, Color.worm)
    }
  }

  def eraseBlocks(mole: Mole)(x1: Int, y1: Int, x2: Int, y2: Int): Unit = {
    x1 == mole.pos.x
    y1 == mole.pos.y
  }

  def paint(): Unit = {
    window.pixelWindow.fill(0, 0, windowSize._1 * blockSize, skyRange.last * blockSize, sky)
    window.pixelWindow.fill(0, skyRange.last * blockSize, windowSize._1 * blockSize, grassRange.last + blockSize, grass)
    window.pixelWindow.fill(0, grassRange.last * blockSize, windowSize._1 * blockSize, windowSize._2 * blockSize, soil)
  }

  def text(): Unit = {
    window.write(s"$leftPlayerName's points: ${leftMole.points}", pos = Pos(0,0), black, blockSize)
    window.write(s"$rightPlayerName's points: ${rightMole.points}", pos = Pos(19,0), black, blockSize)

  }

  def drawWorld() = {
    paint()
    text()
  }

  def update(mole: Mole): Unit = {

    if (mole.nextPos.x == windowSize._1 * blockSize) {
      mole.reverseDir()
    }
    if (mole.nextPos.y == windowSize._2 * blockSize) {
      mole.reverseDir()
    }
    if (mole.nextPos.y == skyRange.last * blockSize) {
      mole.reverseDir()
    }
    if (mole.nextPos.x == - blockSize) {
      mole.reverseDir()
    }

    window.setBlock(mole.nextPos, Color.tunnel)
    mole.move()
    window.setBlock(mole.nextPos, mole.color)
  }

  def handleEvents(): Unit = {
    var anEvent = window.nextEvent()
    while (anEvent != BlockWindow.Event.Undefined) {
      anEvent match {
        case BlockWindow.Event.KeyPressed(key) => {
          rightMole.setDir(key)
          leftMole.setDir(key)
        }

      }
    }
  }

  var quit = false
  val delayMillis = 100

  def gameLoop(): Unit = {
    while (!quit) {
      val t0 = System.currentTimeMillis

      handleEvents()
      update(leftMole)
      update(rightMole)

      val time = (System.currentTimeMillis - t0).toInt
      Thread.sleep((delayMillis - time) max 0)
    }
  }

  def start(): Unit = {
    println("Start digging!")
    println(s"$leftPlayerName ${leftMole.keyControl}")
    println(s"$rightPlayerName ${rightMole.keyControl}")
    drawWorld()
    worm(place(6))
    gameLoop()
  }
}