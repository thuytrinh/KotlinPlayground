package flow

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MutableSharedFlowTest {
  @Test
  fun test() = runBlockingTest {
    // Given
    // val flow = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val flow = MutableSharedFlow<Int>() // This fails the test
    var value: Int? = null
    val job = launch {
      flow.collect { value = it }
    }

    // When
    val r0 = flow.tryEmit(0)

    // Then
    assertThat(r0).isTrue
    assertThat(value).isEqualTo(0)

    // When
    val r1 = flow.tryEmit(1)

    // Then
    assertThat(r1).isTrue
    assertThat(value).isEqualTo(1)

    job.cancel()
  }
}
