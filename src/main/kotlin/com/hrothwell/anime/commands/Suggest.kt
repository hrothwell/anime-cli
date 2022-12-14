package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.hrothwell.anime.client.MALClient
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.AnimeUtil.Companion.openAnime

class Suggest: CliktCommand(
  help = "Get anime suggestions!"
) {

  val limit by option("-l", "--limit", help = """
    limit on the amount of anime to suggest. Default 1, max 100
  """.trimIndent())
    .default("1")

  override fun run() {
    try{
      val animeList = MALClient.getSuggestedAnime(limit.toInt())
      echo("Try: ${animeList.map{it.title}}")
      if(animeList.size == 1){
        openAnime(animeList.first())
      }
    } catch(t: Throwable){
      echoError("""
        unable to suggest anime. Message: ${t.message}
        cause: ${t.cause}
      """.trimIndent())
    }
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg ${AnimeUtil.RESET}", err = true)
  }
}