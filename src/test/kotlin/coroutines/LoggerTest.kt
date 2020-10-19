package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.milliseconds

class LoggerTest {
  @Test
  fun `stop will wait till all queue messages are written down`() {
    runBlocking {
      // Given
      val logFile = File("build", "test.log").also { it.delete() }
      val logger = Logger { logFile }

      // When
      logger.start()
      val logLines = (0..10).map { it.toString() }
      logLines.forEach { logger.log(it) }
      logger.stop()
      logger.waitForCompletion()

      // Then: all log lines are written into the log file.
      assertThat(logFile).hasContent(
        logLines.joinToString("\n")
      )
    }
  }
}

class Logger(
  private val scope: CoroutineScope = GlobalScope,
  private val actionDispatcher: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
  private val getLogFile: () -> File,
) {
  private val writer by lazy { FileWriter(getLogFile(), true) }
  private val actor by lazy {
    scope.actor<LogAction>(context = Dispatchers.IO) {
      var shouldLog = false

      for (action in channel) {
        println("action -> $action")

        when (action) {
          LogAction.Start -> shouldLog = true
          is LogAction.Log -> {
            if (shouldLog) {
              val line = "${action.message}\n"
              writer.write(line)
              delay(500.milliseconds) // Just for debugging.
              writer.flush()
            }
          }
          LogAction.Stop -> shouldLog = false
          is LogAction.WaitForCompletion -> action.response.complete(Unit)
        }
      }
    }
  }

  fun start() = debug("start()") {
    scope.launch(actionDispatcher) {
      actor.send(LogAction.Start)
    }
  }

  fun stop() = debug("stop()") {
    scope.launch(actionDispatcher) {
      actor.send(LogAction.Stop)
    }
  }

  fun log(message: String) = debug("log(message: $message)") {
    scope.launch(actionDispatcher) {
      actor.send(LogAction.Log(message))
    }
  }

  suspend fun waitForCompletion() = debug("waitForCompletion()") {
    scope.launch(actionDispatcher) {
      val response = CompletableDeferred<Unit>()
      actor.send(LogAction.WaitForCompletion(response))
      println("Awaiting...")
      response.await()
    }.join()
  }

  private sealed class LogAction {
    object Start : LogAction()
    data class Log(val message: String) : LogAction()
    object Stop : LogAction()
    data class WaitForCompletion(val response: CompletableDeferred<Unit>) : LogAction()
  }

  private inline fun debug(call: String, block: () -> Unit) {
    println(call)
    block()
  }
}
