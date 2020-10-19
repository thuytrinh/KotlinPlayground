package turbine

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HotFlowTest {
  @Test
  fun `should receive 1, 2 and 3`() = runBlockingTest {
    // Given
    val coldFlow = channelFlow {
      send(1)
      send(2)
      send(3)
      awaitClose()
    }

    // When
    val elements = mutableListOf<Int>()
    val job = launch {
      coldFlow.toList(elements)
    }

    // Then
    assertThat(elements).containsExactly(1, 2, 3)
    job.cancel()
  }

  @Test
  fun `is not terminated`() = runBlockingTest {
    // Given
    val hotFlow = channelFlow {
      send(0)
      awaitClose()
    }

    // When
    val job = launch { hotFlow.collect() }

    // Then
    assertThat(job.isCompleted).isFalse
    job.cancel()
  }

  @Test
  fun `should throw exception`() = runBlockingTest {
    // Given
    val hotFlow = channelFlow<Int> {
      error("Oops!")
      awaitClose()
    }

    // When
    var error: Throwable? = null
    try {
      hotFlow.collect()
    } catch (e: Throwable) {
      error = e
    }

    // Then
    assertThat(error).isInstanceOf(Throwable::class.java)
  }

  @Test
  fun `should not throw exception`() = runBlockingTest {
    // Given
    val hotFlow = channelFlow {
      send(0)
      awaitClose()
    }

    // When
    var error: Throwable? = null
    val job = launch {
      try {
        hotFlow.collect()
      } catch (e: Throwable) {
        error = e
      }
    }

    // Then
    assertThat(error).isNull()
    job.cancel()
  }
}
