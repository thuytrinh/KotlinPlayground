package coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class DeferredTest {
  @Test
  fun test() = runBlockingTest {
    val scope = this
    val stateFlow = scope.async { MutableStateFlow(0) }
    launch { stateFlow.await().also { println(it) } }
    launch { stateFlow.await().also { println(it) } }
  }
}
