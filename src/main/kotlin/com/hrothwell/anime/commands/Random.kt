package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.client.MALClient
import com.hrothwell.anime.domain.ListStatus
import com.hrothwell.anime.domain.MALUserListResponse
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil
import kotlinx.serialization.decodeFromString

class Random: CliktCommand(
  help = """
    Select a random anime from your lists
  """.trimIndent()
) {

  private val possibleListStatusValues = ListStatus.values().map { it.malValue }.toTypedArray()
  private val user by option("-u", "--user-name", help = """
    user name. if not provided this will default to "user_name" value in "${FileUtil.home}/anime-cli/mal-secret.json"
  """.trimIndent())

  private val list by option(
    "-l",
    "--list-status",
    help = "list to select from"
  ).choice(choices = possibleListStatusValues)
    .default("plan_to_watch")

  override fun run() {
    try{
      echo(MALClient.getRandomAnime(user, list))
    } catch(t: Throwable){
      echoError("""
        Unable to retrieve random anime. Message: ${t.message}
        Cause: ${t.cause}
      """.trimIndent())
    }
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg", err = true)
  }
}