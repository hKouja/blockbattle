package BlockBattle

import BlockBattle.Game.Color.backgroundColorAtDepth
import BlockBattle.Game.{windowSize, windowTitle}

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
    val mole2  = new JColor (60, 60,0)
    val soil   = new JColor (153, 102, 51)
    val tunnel = new JColor (204, 153, 102)
    val grass  = new JColor (25, 130, 35)
    val sky    = new JColor (31, 190, 214)
    val worm   = new JColor (225, 100, 235)

    def backgroundColorAtDepth (y: Int): java.awt.Color = {
      if (Game.skyRange contains(y)) {Color.sky}
      else if (Game.grassRange contains(y)) {Color.grass}
      else Color.soil

    }
  }
}

class Game (
             val leftPlayerName: String = "LEFT",
             val rightPlayerName: String = "RIGHT"
           ) {

  import Game._

  val window = new BlockWindow(windowSize, windowTitle, blockSize)
  val leftMole = new Mole (
    name = leftPlayerName,
    Pos (1,2),
    (1,0),
    Color.mole,
    KeyControl("a", "d", "w", "s")
  )
  val rightMole = new Mole (
    name = rightPlayerName,
    Pos (1,2),
    (0,1),
    Color.mole2,
    KeyControl("ö", "'", "å", "ä")
  )

  def drawWorld() = {
    for (y <- 0 to windowSize._2) {
      for (x <- 0 to windowSize._1) {
        var currentBlock: Pos = Pos (x,y)
        window.setBlock(currentBlock, backgroundColorAtDepth(y))
      }
    }
  }

  def eraseBlocks(x1: Int, y1: Int, x2: Int, y2: Int): Unit = {

  }

  def isHere(mole: Mole): Boolean = {

    var xs = mole.pos.x
    var ys = mole.pos.y

    xs == windowSize._1
    ys == windowSize._2
    ys == skyRange.last
    xs == 0

  }

  def update(mole: Mole): Unit = {
    if (isHere(mole)) mole.reverseDir()
  }

  def handleEvents(): Unit = {
    var anEvent = window.nextEvent()

    anEvent match {
      case BlockWindow.Event.KeyPressed(key) => {
        rightMole.setDir(key)
        leftMole.setDir(key)
      }
    }
  }

  var quit = false
  val delayMillis = 80

  def gameLoop(): Unit = {
    while (!quit) {
      val t0 = System.currentTimeMillis

      update(leftMole)
      update(rightMole)

    }
  }
  def start(): Unit = {
    println("Start digging!")
    println(s"$leftPlayerName ${leftMole.keyControl}")
    println(s"$rightPlayerName ${rightMole.keyControl}")
    drawWorld()
    gameLoop()
  }
}