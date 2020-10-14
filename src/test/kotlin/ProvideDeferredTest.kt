import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.milliseconds

class ProvideDeferredTest {
  private object HelloWorldQualifier

  private class ServiceA(
    private val getHelloWorld: Deferred<String>
  ) {
    suspend fun say() {
      println("A: ${getHelloWorld.await()}")
    }
  }

  private class ServiceB(
    private val getHelloWorld: Deferred<String>
  ) {
    suspend fun say() {
      println("B: ${getHelloWorld.await()}")
    }
  }

  @Test
  fun `can inject suspend functions by using Deferred`() = runBlockingTest {
    suspend fun getHelloWorld(): String {
      delay(500.milliseconds)
      return "Hello, World!"
    }

    val appScope = this
    val app = startKoin {
      modules(
        module {
          single<Deferred<String>>(qualifier = named<HelloWorldQualifier>()) {
            appScope.async { getHelloWorld() }
          }
          single {
            ServiceA(getHelloWorld = get(named<HelloWorldQualifier>()))
          }
          single {
            ServiceB(getHelloWorld = get(named<HelloWorldQualifier>()))
          }
        }
      )
    }

    val serviceA = app.koin.get<ServiceA>()
    val serviceB = app.koin.get<ServiceB>()
    serviceA.say()
    serviceB.say()
  }
}
