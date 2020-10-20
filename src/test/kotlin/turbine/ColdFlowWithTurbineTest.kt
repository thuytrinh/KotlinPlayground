package turbine

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ColdFlowWithTurbineTest {
  @Test
  fun `should receive 1, 2, 3 & then complete`() = runBlockingTest {
    // Given
    val coldFlow = flowOf(1, 2, 3)

    // When
    coldFlow.test {
      // Then
      assertThat(expectItem()).isEqualTo(1)
      assertThat(expectItem()).isEqualTo(2)
      assertThat(expectItem()).isEqualTo(3)
      expectComplete()
    }
  }
}
