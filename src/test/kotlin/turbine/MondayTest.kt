package turbine

import app.cash.turbine.test
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MondayTest {
  @Test
  fun case0() = runBlockingTest {
    // Given
    val letItEmitEvenNumbers = MutableSharedFlow<Unit>()
    suspend fun evenNumberFlow() = withContext(coroutineContext) {
      callbackFlow {
        withContext(NonCancellable) {
          letItEmitEvenNumbers.first()
          send(0)
        }
        awaitClose { /* Do nothing for debugging purpose. */ }
      }
    }

    val oddNumberFlow = MutableSharedFlow<Int>(replay = 1)
    val x = flow {
      combine(evenNumberFlow(), oddNumberFlow) { i, j -> "$i.$j" }
        .collect { emit(it) }
    }

    // When
    oddNumberFlow.tryEmit(1)

    x.test {
      // When
      letItEmitEvenNumbers.tryEmit(Unit)

      // Then
      assertThat(expectItem()).isEqualTo("0.1")
    }
  }
}
