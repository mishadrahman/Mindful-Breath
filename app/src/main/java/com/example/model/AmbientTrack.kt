package com.example.model

import androidx.annotation.RawRes
import com.example.R

data class AmbientTrack(
  val id: String,
  val title: String,
  val subtitle: String,
  @param:RawRes val resId: Int
)

object AmbientPresets {
  val rain = AmbientTrack(
    id = "rain",
    title = "Forest Rain",
    subtitle = "Velvety cozy rain & soft distant drops",
    resId = R.raw.ambient_rain
  )

  val ocean = AmbientTrack(
    id = "ocean",
    title = "Ocean Waves",
    subtitle = "Deep rhythmic breathing surf & wash",
    resId = R.raw.ambient_ocean
  )

  val zenBowl = AmbientTrack(
    id = "zen_bowl",
    title = "Zen Singing Bowl",
    subtitle = "432 Hz Sacred Om & Theta wave drone",
    resId = R.raw.ambient_zen_bowl
  )

  val forestWind = AmbientTrack(
    id = "forest_wind",
    title = "Forest Wind",
    subtitle = "Gentle mountain canopy breeze",
    resId = R.raw.ambient_forest_wind
  )

  val allTracks = listOf(rain, ocean, zenBowl, forestWind)
}
