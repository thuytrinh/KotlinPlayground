package turbine

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TuesdayTest {
  @Test
  fun test() = runBlockingTest {
    val flow = MutableSharedFlow<Int>()
    val initialValue = flow.first()
    assertThat(initialValue).isZero()
  }
}
