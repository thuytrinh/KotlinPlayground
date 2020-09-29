import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.time.milliseconds

class CloseChannelTest {
  @Test
  fun `close won't wait till all sent values are processed`() {
    runBlocking {
      // Given
      val channel = BroadcastChannel<Int>(capacity = Channel.BUFFERED)
      val values = mutableListOf<Int>()
      val job = launch {
        channel.asFlow().collect {
          println("Queuing $it...")

          delay(500.milliseconds)
          values.add(it)

          println("Added $it")
        }
      }

      // When
      (1..10).forEach {
        channel.send(it)
        println("Sent $it")
      }
      channel.close()
      job.join()

      // Then
      assertThat(values).isEmpty()
      assertThat(job.isCompleted).isTrue()
    }
  }
}
