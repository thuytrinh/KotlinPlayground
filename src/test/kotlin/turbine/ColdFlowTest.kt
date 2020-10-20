package turbine

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ColdFlowTest {
  @Test
  fun `should receive 1, 2 and 3`() = runBlockingTest {
    // Given
    val coldFlow = flowOf(1, 2, 3)

    // When
    val elements = mutableListOf<Int>()
    coldFlow.toList(elements)

    // Then
    assertThat(elements).containsExactly(1, 2, 3)
  }

  @Test
  fun `should complete`() = runBlockingTest {
    // Given
    val coldFlow = flowOf(1, 2, 3)

    // When
    val job = launch { coldFlow.collect() }

    // Then
    assertThat(job.isCompleted).isTrue()
  }
}
