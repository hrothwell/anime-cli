package com.hrothwell.anime

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.MALUserListResponse
import java.io.File

class Anime : CliktCommand(
  help = "pick a random anime from your MAL lists",
  epilog = """
    You will need a MAL api key to use this. This api key should be placed in your home directory as anime-cli/mal-secret.txt
    Ex: C:\Users\hone_the_rat\anime-cli\mal-secret.txt
  """.trimIndent()
) {
  private val RED = "\u001b[31m"
  private val user by option("-u", "--user", help = "your user name").default("hone_the_rat")
  private val list by option("-l", "--list", help = "list to select from").default("plan_to_watch")

  private val objectMapper = jacksonObjectMapper()
  override fun run() {
    getRandomAnime(user, list)
  }

  fun getRandomAnime(user: String, list: String) {
    // TODO screw it force users to put a client id in a file somewhere because it just wouldn't work nicely with getResourceAsStream or anything
    //  also don't want to just have my client secret in git. Don't know how this will work if running natively tho
    val home = System.getProperty("user.home")
    val secretLocation = "$home/anime-cli/mal-secret.txt"
    val clientId = try {
      File(secretLocation).readText()
    } catch (t: Throwable) {
      echoError("You need to place your MAL client id in this file: $secretLocation")
      return
    }

    val headers = "X-MAL-CLIENT-ID" to clientId
    val listStatus = "listStatus" to list
    val limit = "limit" to 1000

    val request =
      Fuel.get(path = "https://api.myanimelist.net/v2/users/$user/animelist", parameters = listOf(listStatus, limit))
        .appendHeader(headers)

    // TODO check for errors before getting result here
    //   (also why does Fuel do this this way it feels weird to call .third)
    val response = request.response().third.get()

    try {
      val result = objectMapper.readValue(response, MALUserListResponse::class.java)
      echo("watch this: ${result.data.random().node.title}")
    } catch (t: Throwable) {
      echoError("couldn't read the response from MAL: $response")

    }
  }

  private fun echoError(msg: String) {
    echo("$RED $msg", err = true)
  }
}