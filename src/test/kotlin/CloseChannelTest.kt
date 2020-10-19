import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.time.milliseconds

class CloseChannelTest {
  @Test
  fun `close & join should wait till all sent values are processed`() {
    runBlocking {
      // Given
      val channel = BroadcastChannel<Int>(capacity = Channel.BUFFERED)
      val values = mutableListOf<Int>()
      val waitForCollectToStart = BroadcastChannel<Unit>(capacity = 1)
      val job = launch {
        println("Launching...")
        channel.asFlow()
          .onStart { waitForCollectToStart.send(Unit) }
          .collect {
            println("Queuing $it...")

            delay(500.milliseconds)
            values.add(it)

            println("Added $it")
          }
      }

      // When
      println("Waiting for collect()...")
      waitForCollectToStart.asFlow().first()

      (1..10).forEach {
        channel.send(it)
        println("Sent $it")
      }

      println("Closing...")
      channel.close()
      job.join()

      // Then
      println("Asserting...")
      assertThat(values).containsExactlyElementsOf(1..10)
      assertThat(job.isCompleted).isTrue
      println("Assertion is done")
    }
  }
}
