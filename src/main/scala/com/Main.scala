package com

import java.io._
import java.io.IOException
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File.separator
import com.typesafe.config._
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption._


object Main extends App {
  //load in, out dir and cutoff point from the conf file
  val conf = ConfigFactory.load()
  val srcDir = conf.getString("vars.in")
  val resultDir = conf.getString("vars.out")
  val border = conf.getInt("vars.point")

  //get path of every single jpg and png from in directory
  val photos = new File(srcDir).listFiles.filter(_.isFile).toList

  //todo: for in FP??
  //todo: 0 in return??
  //todo: try to remove all vars
  //todo: tests
  photos.map{el => {
    val image = ImageIO.read(el)
    var V: Double = 0
    for(i <- 0 until image.getWidth ) {
      for (j <- 0 until image.getHeight) {
        val singlePixel = image.getRGB(i, j)
        V = V + Calc.getMaxOfColors(singlePixel)
      }
    }
    val vAVG = V/(image.getWidth*image.getHeight)
    val score = 100 - (vAVG * 100).round
    val newName = el.getName.split("\\.")
    if(score > border){
      newName(0) = newName(0) + "_dark_" + score
    }else{
      newName(0) = newName(0) + "_bright_" + score
    }

    val destination = resultDir + separator + newName.mkString(".")
    val src: Path = Paths.get(el.getPath)
    val dest: Path = Paths.get(destination)
    Files.copy(src, dest, REPLACE_EXISTING)
  }}
}
