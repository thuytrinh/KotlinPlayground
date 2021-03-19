package turbine

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.time.seconds

class TurbineAndAdvanceTimeByTest {
  @Test
  fun test() = runBlockingTest {
    val flow = flow {
      delay(2.seconds)
      emit(1)
    }
    flow.test(5.seconds) {
      advanceTimeBy(3000)
      assertThat(expectItem()).isEqualTo(1)
      expectComplete()
    }
  }
}
