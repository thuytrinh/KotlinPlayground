package logging

import org.junit.Test

class ExtractCompanionClassNameTest {
  companion object {
    init {
      println("canonicalName=${this::class.java.canonicalName}")
      println("name=${this::class.java.name}")
      println("simpleName=${this::class.java.simpleName}")
      println(this::class.java.canonicalName.split('.').takeLast(2).first())
    }
  }

  @Test
  fun test() {
    // Just to run companion object init.
  }
}
