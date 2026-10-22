package com.example.model

data class Milestone(
  val id: String,
  val title: String,
  val targetMinutes: Int,
  val description: String,
  val badgeIcon: String,
  val rewardSummary: String
)

object MilestonePresets {
  val milestone100Mins = Milestone(
    id = "century_100m",
    title = "Century of Calm",
    targetMinutes = 100,
    description = "Achieve 100 total minutes of conscious breathwork",
    badgeIcon = "🏅",
    rewardSummary = "Milestone Achieved! You reached 100 total minutes of mindful breathing, establishing profound nervous system resilience."
  )

  val allMilestones = listOf(
    Milestone(
      id = "spark_5m",
      title = "Mindful Spark",
      targetMinutes = 5,
      description = "Begin your calming journey",
      badgeIcon = "🌱",
      rewardSummary = "First steps toward mindful stillness."
    ),
    Milestone(
      id = "flow_30m",
      title = "Tranquil Flow",
      targetMinutes = 30,
      description = "Half an hour of conscious breath",
      badgeIcon = "🌊",
      rewardSummary = "Steady relaxation flowing into your day."
    ),
    Milestone(
      id = "anchor_60m",
      title = "Zen Anchor",
      targetMinutes = 60,
      description = "1 full hour of mindful breathing",
      badgeIcon = "🌿",
      rewardSummary = "One hour of inner peace unlocked."
    ),
    milestone100Mins
  )
}
