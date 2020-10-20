package turbine

import app.cash.turbine.test
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HotFlowWithTurbineTest {
  @Test
  fun `should receive 1, 2 and 3`() = runBlockingTest {
    // Given
    val hotFlow = channelFlow {
      send(1)
      send(2)
      send(3)
      awaitClose()
    }

    // When
    hotFlow.test {
      // Then
      assertThat(expectItem()).isEqualTo(1)
      assertThat(expectItem()).isEqualTo(2)
      assertThat(expectItem()).isEqualTo(3)
    }
  }

  @Test
  fun `should receive 1, 2 and 3 (alternative version)`() = runBlockingTest {
    // Given
    val hotFlow = MutableStateFlow(1)

    // When
    hotFlow.test {
      // Then
      assertThat(expectItem()).isEqualTo(1)

      // When
      hotFlow.value = 2

      // Then
      assertThat(expectItem()).isEqualTo(2)
    }
  }

  @Test
  fun `should not complete`() = runBlockingTest {
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
    hotFlow.test {
      // Then
      expectError()
    }
  }

  @Test
  fun `should not throw exception`() = runBlockingTest {
    // Given
    val hotFlow = channelFlow {
      send(0)
      awaitClose()
    }

    // When
    hotFlow.test {
      // Then
      assertThat(expectItem()).isEqualTo(0)
    }
  }
}
