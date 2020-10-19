import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TurbineAndHotFlowTest {
  @Test
  fun `should verify emission of StateFlow`() = runBlockingTest {
    val flow = MutableStateFlow(0)
    println("Asserting")
    flow.test {
      assertThat(expectItem()).isEqualTo(0)
      assertThat(expectItem()).isEqualTo(1)
      assertThat(expectItem()).isEqualTo(2)
      cancelAndIgnoreRemainingEvents()
    }
    println("Emitting values")
    flow.value = 1
    flow.value = 2
    println("Done")
  }
}
