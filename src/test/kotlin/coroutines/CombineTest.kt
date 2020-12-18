package coroutines

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CombineTest {
  @Test
  fun case0() = runBlockingTest {
    val a = MutableSharedFlow<Int>(1)
    val b = MutableSharedFlow<Int>(1)
    val c = MutableSharedFlow<Int>(1)
    combine(a, b, c) { x, y, z -> "$x.$y.$z" }
      .onEach { println("item: $it") }
      .test {
        a.tryEmit(0)
        b.tryEmit(1)
        c.tryEmit(2)

        assertThat(expectItem()).isEqualTo("0.1.2")
        cancelAndIgnoreRemainingEvents()
      }
  }

  @Test
  fun case1() = runBlockingTest {
    combine(flowOf(0), flowOf(1), flowOf(2)) { x, y, z ->
      "$x.$y.$z"
    }.test {
      assertThat(expectItem()).isEqualTo("0.1.2")
      cancelAndIgnoreRemainingEvents()
    }
  }
}
