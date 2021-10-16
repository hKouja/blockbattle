package BlockBattle

import BlockBattle.Game.Color.{black, grass, mole, mole2, sky, soil, worm}

import java.lang
import scala.io.StdIn.readLine

object Game {
  val windowSize = (31, 50)
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
    Pos (10 , windowSize._2  /2),
    dir = (0,1),
    Color.mole,
    KeyControl("a", "d", "w", "s")
  )
  val rightMole = new Mole (
    name = rightPlayerName,
    Pos (5 + windowSize._1 / 2, windowSize._2  /2),
    dir = (0,1),
    Color.mole2,
    KeyControl("Left", "Right", "Up", "Down")
  )

  def place(n: Int): Vector [(Int, Int)] = {
    import scala.util.Random.nextInt
    (for (i <- 1 to n) yield (
      1 + nextInt(windowSize._1 - 1),
      grassRange.last  + 1 + nextInt(windowSize._2 - (grassRange.last + 1)))).toVector
  }

  def worm(xs: Vector[(Int, Int)]): Unit = {
    for (i <- 0 until xs.length) {
      val localPos: Pos = Pos(xs(i)._1, xs(i)._2)
      window.setBlock(localPos, Color.worm)
    }
  }


  def paint(): Unit = {
    window.pixelWindow.fill(0, 0, windowSize._1 * blockSize, skyRange.last * blockSize, sky)
    window.pixelWindow.fill(0, skyRange.last * blockSize, windowSize._1 * blockSize, grassRange.last + blockSize, grass)
    window.pixelWindow.fill(0, grassRange.last * blockSize, windowSize._1 * blockSize, windowSize._2 * blockSize, soil)
  }

  def text(): Unit = {

    import window._
    window.pixelWindow.fill(0,0, window.nbrOfBlocks._1 * blockSize, skyRange.last * blockSize, Color.sky)

    window.write(s"$leftPlayerName's points: ${leftMole.points}", pos = Pos(0,0), black, blockSize)
    window.write(s"$rightPlayerName's points: ${rightMole.points}", pos = Pos(19,0), black, blockSize)

  }


  def gameOver():Unit = {

    window.write(
      text = "GAME OVER!",
      pos = Pos(25 - (windowSize._1 / 2), windowSize._2 / 2),
      color = Color.black,
      textSize = blockSize * 2,
    )
    winner()
  }
  def winner(): Unit = {
    if (leftMole.points < rightMole.points) {
      window.write(
        text = s"${rightMole.name} is the winner!",
        pos = Pos(25 - windowSize._1 / 2,windowSize._2 / 4),
        color = Color.mole2,
        textSize = 20,
      )
    }
    else {
      window.write(
        text = s"${leftMole.name} is the winner!",
        pos = Pos(25 - windowSize._1 / 2, windowSize._2 / 4),
        color = Color.mole,
        textSize = 20,
      )
    }
  }

  def drawWorld() = {
    paint()
    text()
  }

  def update(mole: Mole): Unit = {

    if (mole.nextPos.x == windowSize._1 ) then {
      mole.reverseDir()
    }
    if (mole.nextPos.y == windowSize._2 ) then {
      mole.reverseDir()
    }
    if (mole.nextPos.y == skyRange.last ) then {
      mole.reverseDir()
    }
    if (mole.nextPos.x < 0) then {
      mole.reverseDir()
    }
    if (window.getBlock(mole.nextPos) == Color.worm){
      mole.points += 10
      println(mole.points)
    }

    if (window.getBlock(mole.nextPos) == Color.soil){
      mole.points += 1
    }
    if (mole.points >= 100) then {
      gameOver()
      quit = true
    }
    window.setBlock(mole.nextPos, mole.color)
    window.setBlock(mole.pos, Color.tunnel)
    mole.move()
  }
  var quit = false
  def handleEvents(): Unit = {
    var anEvent = window.nextEvent()
    while (anEvent != BlockWindow.Event.Undefined) {
      anEvent match {
        case BlockWindow.Event.KeyPressed(key) => {
          rightMole.setDir(key)
          leftMole.setDir(key)
        }
        case BlockWindow.Event.WindowClosed => {quit = true; println("Window closed")} }
        anEvent = window.nextEvent()

    }
  }

  val delayMillis = 100

  def gameLoop(): Unit = {
    while (!quit) {
      val t0 = System.currentTimeMillis

      handleEvents()
      update(leftMole)
      update(rightMole)
      text()


      val time = (System.currentTimeMillis - t0).toInt
      Thread.sleep((delayMillis - time) max 0)
    }
  }
  var timeLeft = System.currentTimeMillis()
  def elapsed = System.currentTimeMillis() - timeLeft



  def starting(): Unit = {
    window.write(
      text = s"WELCOME $leftPlayerName AND $rightPlayerName TO MY BLOCK BATTLE GAME!",
      pos = Pos(0, 10),
      color = Color.black,
      textSize = 15
    )
    Thread.sleep(4000)
    drawWorld()
    playerControlText()
    window.write (
      text = "CHECK YOUR CONTROLS",
      pos = Pos(2, skyRange.last + 1),
      color = Color.black,
      textSize = 20
    )
    Thread.sleep(5000)
    drawWorld()
    window.write (
      text = "GET TO 100 POINTS TO WIN!!",
      pos = Pos(2, skyRange.last + 1),
      color = Color.black,
      textSize = 20
    )
  }
  def playerControlText(): Unit = {
    window.write (
      text = s"$leftPlayerName use 'W' 'A' 'S' 'D' ",
      pos = Pos(2, 15),
      color = Color.black,
      textSize = 20
    )
    window.write (
      text =s"$rightPlayerName use Arrows",
      pos = Pos(2, 17),
      color = Color.black,
      textSize = 20
    )
  }

  def start(): Unit = {

    println("Start digging!")
    println(s"$leftPlayerName ${leftMole.keyControl}")
    println(s"$rightPlayerName ${rightMole.keyControl}")
    drawWorld()
    starting()
    Thread.sleep(5000)
    drawWorld()
    worm(place(10))
    gameLoop()
  }
}