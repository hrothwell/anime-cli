package com.hrothwell.anime.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MALUserListResponse(
  val data: List<Data>
)