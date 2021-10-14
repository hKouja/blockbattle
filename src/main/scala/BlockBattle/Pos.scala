package BlockBattle

import BlockBattle.Game.blockSize

case class Pos(x: Int, y: Int) {
  def moved(delta: (Int, Int)): Pos = Pos(x + (delta._1 * blockSize), y + (delta._2 * blockSize))
}