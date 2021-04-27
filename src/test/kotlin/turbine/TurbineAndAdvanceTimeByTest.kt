package turbine

import app.cash.turbine.test
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.time.hours
import kotlin.time.seconds

class TurbineAndAdvanceTimeByTest {
  @Test
  fun test() {
    val testDispatcher = TestCoroutineDispatcher()
    testDispatcher.runBlockingTest {
      val flow = flow {
        println("Dispatcher of flow: ${currentCoroutineContext()}")
        delay(2.seconds)
        println("ok, done")
        emit(1)
      }.flowOn(testDispatcher)
      println("Dispatcher of test: ${currentCoroutineContext()}")

      val scope = this
      flow.test(timeout = 1.hours) {
        println("Changing the timer")
        scope.advanceTimeBy(3.seconds.toLongMilliseconds())
        println("Done changing the timer")

        assertThat(expectItem()).isEqualTo(1)
        expectComplete()
      }
    }
  }
}
