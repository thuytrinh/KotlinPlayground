package logging

import org.junit.Test

class BuildMessageTest {
  @Test
  fun test() {
    val userName = "Johnny Appleseed"
    val phoneNumber = "069 219308000"
    val email = "johnny@earth.com"
    i {
      "userName" `⭢` userName
      // Either this
      "phoneNumber" with phoneNumber
      // Or this.
      "email" to email
    }
  }
}

private class Message {
  private val builder = StringBuilder()

  fun string(): String = builder.toString()

  infix fun String.`⭢`(that: String) {
    addNewPair(this, that)
  }

  infix fun String.to(that: String) {
    addNewPair(this, that)
  }

  infix fun String.with(that: String) {
    addNewPair(this, that)
  }

  private fun addNewPair(dis: String, that: String) {
    if (builder.isNotEmpty()) {
      builder.append(", ")
    }
    builder.append(dis).append('=').append(that)
  }
}

private fun i(buildMessage: Message.() -> Unit) {
  Message().apply {
    buildMessage()
    println(string())
  }
}
