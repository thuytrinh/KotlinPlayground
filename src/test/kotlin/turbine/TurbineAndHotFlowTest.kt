package turbine

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TurbineAndHotFlowTest {
  @Test
  fun `should verify emission of StateFlow`() = runBlockingTest {
    val hotFlow = MutableStateFlow(0)
    hotFlow.test {
      assertThat(expectItem()).isEqualTo(0)

      hotFlow.value = 1
      assertThat(expectItem()).isEqualTo(1)

      hotFlow.value = 2
      assertThat(expectItem()).isEqualTo(2)

      cancelAndIgnoreRemainingEvents()
    }
  }
}
