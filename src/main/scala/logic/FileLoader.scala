package logic
import java.nio.file.Paths

import cats.effect.{Blocker, ContextShift, IO}
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object FileLoader {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def loadResource(fileName: String) = {
    val file = Stream.resource(Blocker[IO]).flatMap { blocker =>
      val filePath = getClass.getClassLoader.getResource(fileName)
      io.file
        .readAll[IO](Paths.get(filePath.toURI), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
    }
    file.compile.string
  }

}
