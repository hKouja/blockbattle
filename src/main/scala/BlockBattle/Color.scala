package BlockBattle


import java.awt.Color as JColor

object Color {
  val black = new JColor(0, 0, 0)
  val mole = new JColor(51,51, 0)
  val soil =  new JColor(153, 102, 51)
  val tunnel = new JColor(204, 153, 102)
  val grass = new JColor(25, 130, 35)
  val sky = new JColor(31, 190, 214)
  val worm = new JColor(225, 100, 235)

  def backgroundColorAtDepth (y: Int): java.awt.Color = {
    if (Game.skyRange contains(y)) {Color.sky}
    else if (Game.grassRange contains(y)) {Color.grass}
    else Color.soil

  }
}
