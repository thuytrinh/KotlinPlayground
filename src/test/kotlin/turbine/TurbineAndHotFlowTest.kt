package turbine

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TurbineAndHotFlowTest {
  @Test
  fun `should receive 1, 2 and 3`() = runBlockingTest {
    val hotFlow = MutableStateFlow(1)
    hotFlow.test {
      assertThat(expectItem()).isEqualTo(1)

      hotFlow.value = 2
      assertThat(expectItem()).isEqualTo(2)

      cancelAndIgnoreRemainingEvents()
    }
  }
}
