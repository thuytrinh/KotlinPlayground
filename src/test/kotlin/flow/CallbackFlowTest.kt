package flow

import app.cash.turbine.test
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.time.seconds

class CallbackFlowTest {
  @Test
  fun `sendBlocking causes blocking when sending more than 64 values`() = runBlocking {
    val callbacks = mutableListOf<(Int) -> Unit>()
    val flow = callbackFlow {
      val newCallback: (Int) -> Unit = {
        println("Sending $it")
        sendBlocking(it)
        println("Done sending $it")
      }
      callbacks += newCallback
      awaitClose { callbacks -= newCallback }
    }
    flow.map {
      delay(2.seconds)
      it.toString().also { println("Make: $it") }
    }.test {
      val count = 100
      (0..count).forEach { x -> callbacks.forEach { it(x) } }

      delay((count * 2).seconds)
      (0..count).forEach {
        assertThat(expectItem()).isEqualTo(it.toString())
      }
    }
  }
}
