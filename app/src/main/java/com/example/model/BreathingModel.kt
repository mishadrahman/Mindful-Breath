package com.example.model

enum class BreathingPhaseType {
  INHALE,
  HOLD_IN,
  EXHALE,
  HOLD_OUT
}

data class PhaseStep(
  val type: BreathingPhaseType,
  val label: String,
  val durationSeconds: Int,
  val guidance: String,
  val startScale: Float,
  val endScale: Float
)

data class BreathingTechnique(
  val id: String,
  val name: String,
  val category: String,
  val formula: String,
  val description: String,
  val benefits: String,
  val medicalOrigin: String = "Clinical Autonomic Protocol",
  val clinicalTarget: String = "Stress & Autonomic Modulation",
  val steps: List<PhaseStep>
) {
  val cycleDurationSeconds: Int = steps.sumOf { it.durationSeconds }
}

enum class SessionDuration(val label: String, val totalSeconds: Int) {
  ONE_MIN("1 Min", 60),
  THREE_MIN("3 Mins", 180),
  FIVE_MIN("5 Mins", 300),
  INFINITE("Infinite", -1)
}

object BreathingPresets {
  // 1. Box Breathing (Navy SEALs & Clinical Autonomic Regulation)
  val boxBreathing = BreathingTechnique(
    id = "box",
    name = "Box Breathing",
    category = "Focus & Calm",
    formula = "4-4-4-4",
    description = "Medically recognized tactical breathing protocol used by Navy SEALs, emergency physicians, and athletes to downregulate acute adrenaline and regain composure under pressure.",
    benefits = "Lowers cortisol, enhances executive focus, and stabilizes sympathetic arousal",
    medicalOrigin = "Tactical Medicine & Autonomic Nervous System Protocol",
    clinicalTarget = "Acute high-pressure stress, cognitive fog, panic prevention",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 4,
        guidance = "Breathe in deeply through your nose",
        startScale = 1.0f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.HOLD_IN,
        label = "Hold",
        durationSeconds = 4,
        guidance = "Retain air peacefully with open chest",
        startScale = 1.6f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 4,
        guidance = "Slowly release all air through your mouth",
        startScale = 1.6f,
        endScale = 1.0f
      ),
      PhaseStep(
        type = BreathingPhaseType.HOLD_OUT,
        label = "Hold",
        durationSeconds = 4,
        guidance = "Rest empty before the next breath",
        startScale = 1.0f,
        endScale = 1.0f
      )
    )
  )

  // 2. 4-7-8 Relaxation (Dr. Andrew Weil, Harvard MD)
  val relaxation478 = BreathingTechnique(
    id = "478",
    name = "4-7-8 Relaxation",
    category = "Deep Sleep & Anxiety",
    formula = "4-7-8",
    description = "Developed by Dr. Andrew Weil (Harvard Medical School graduate). By extending the exhalation to twice the duration of inhalation, it acts as a natural tranquilizer for the nervous system.",
    benefits = "Stimulates vagus nerve, slows rapid pulse, and facilitates rapid sleep onset",
    medicalOrigin = "Dr. Andrew Weil, Harvard Medical School",
    clinicalTarget = "Insomnia, nocturnal rumination, severe tension",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 4,
        guidance = "Inhale softly and quietly through your nose",
        startScale = 1.0f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.HOLD_IN,
        label = "Hold",
        durationSeconds = 7,
        guidance = "Hold your breath gently; let shoulders soften",
        startScale = 1.6f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 8,
        guidance = "Whoosh exhaling fully through parted lips",
        startScale = 1.6f,
        endScale = 1.0f
      )
    )
  )

  // 3. Equal Breathing (Sama Vritti)
  val equalBreathing = BreathingTechnique(
    id = "equal",
    name = "Equal Breathing",
    category = "Balancing & Clarity",
    formula = "5-5",
    description = "Medically recognized for balancing the sympathetic (fight-or-flight) and parasympathetic (rest-and-digest) branches of the autonomic nervous system through symmetric tidal volume.",
    benefits = "Harmonizes mind, prevents hyperventilation, and promotes steady mindfulness",
    medicalOrigin = "Pulmonary Rhythm & Autonomic Neuroscience",
    clinicalTarget = "General tension, cognitive resets, daily grounding",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 5,
        guidance = "Smooth, even inhalation through nose",
        startScale = 1.0f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 5,
        guidance = "Smooth, gentle exhalation of equal length",
        startScale = 1.6f,
        endScale = 1.0f
      )
    )
  )

  // 4. Coherent / Resonance Breathing (Cardiovascular Gold Standard: 5 breaths/min)
  val coherentBreathing = BreathingTechnique(
    id = "coherent",
    name = "Coherent Breathing",
    category = "Heart Rate & HRV",
    formula = "6-6",
    description = "Extensively studied in cardiovascular medicine (Dr. Stephen Elliott & HeartMath). Breathing at 5 breaths per minute entrains heart rate variability (HRV), baroreflex sensitivity, and blood pressure into peak coherence.",
    benefits = "Maximizes Heart Rate Variability (HRV), lowers systemic blood pressure, and calms emotional reactivity",
    medicalOrigin = "Cardiovascular & Neurocardiology Research",
    clinicalTarget = "Hypertension support, cardiovascular stress reduction, emotional resilience",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 6,
        guidance = "Inhale smoothly and expand lower ribcage",
        startScale = 1.0f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 6,
        guidance = "Slow, effortless exhalation without pause",
        startScale = 1.6f,
        endScale = 1.0f
      )
    )
  )

  // 5. Physiological Sigh (Stanford Medicine Neurobiology Research 2023)
  val physiologicalSigh = BreathingTechnique(
    id = "sigh",
    name = "Physiological Sigh",
    category = "Acute Panic & Reset",
    formula = "3+1-6",
    description = "Clinically validated by Stanford University School of Medicine (Cell Reports Medicine, 2023). A double inhalation re-inflates collapsed pulmonary alveoli, followed by a long exhale that expels carbon dioxide quickly.",
    benefits = "Fastest medically proven method to decelerate heart rate and eliminate acute panic in real time",
    medicalOrigin = "Stanford University School of Medicine (Huberman Lab, 2023)",
    clinicalTarget = "Acute panic spikes, severe claustrophobia, immediate post-stress recovery",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 3,
        guidance = "Deep first breath in through your nose",
        startScale = 1.0f,
        endScale = 1.45f
      ),
      PhaseStep(
        type = BreathingPhaseType.HOLD_IN,
        label = "Sip Air",
        durationSeconds = 1,
        guidance = "Sharp second sip of air to top off lungs",
        startScale = 1.45f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 6,
        guidance = "Long, slow sigh releasing all air through mouth",
        startScale = 1.6f,
        endScale = 1.0f
      )
    )
  )

  // 6. 7-11 De-stress Breathing (Clinical Psychotherapy Standard)
  val deepCalm711 = BreathingTechnique(
    id = "calm711",
    name = "7-11 De-stress",
    category = "Vagal Nerve Override",
    formula = "4-7",
    description = "Widely used in clinical psychotherapy and trauma rehabilitation. Making the exhalation substantially longer than the inhalation prompts immediate acetylcholine release via the vagus nerve.",
    benefits = "Rapid vagal stimulation, overrides sympathetic fight-or-flight, eases chest tightness",
    medicalOrigin = "Clinical Psychotherapy & Somatic Trauma Protocols",
    clinicalTarget = "Hyperventilation, chronic anxiety, chest tension",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 4,
        guidance = "Gentle, calm inhalation through nose",
        startScale = 1.0f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 7,
        guidance = "Long, steady exhalation letting all muscles release",
        startScale = 1.6f,
        endScale = 1.0f
      )
    )
  )

  // 7. Energizing Awakening (Neuro-Respiratory Activation)
  val energizingBreath = BreathingTechnique(
    id = "vitality",
    name = "Awakening Rhythm",
    category = "Energy & Alertness",
    formula = "4-2-4-2",
    description = "Medically recognized balanced rhythm designed to optimize oxygen-carbon dioxide gas exchange, elevating arterial oxygen saturation to dispel mental lethargy and brain fog.",
    benefits = "Enhances alertness, dispels afternoon fatigue, and sharpens cognitive processing",
    medicalOrigin = "Neuro-Respiratory Activation Physiology",
    clinicalTarget = "Morning sluggishness, mid-day energy slumps, pre-workout readiness",
    steps = listOf(
      PhaseStep(
        type = BreathingPhaseType.INHALE,
        label = "Inhale",
        durationSeconds = 4,
        guidance = "Crisp, energizing breath filling upper chest",
        startScale = 1.0f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.HOLD_IN,
        label = "Hold",
        durationSeconds = 2,
        guidance = "Feel the oxygen energizing your mind",
        startScale = 1.6f,
        endScale = 1.6f
      ),
      PhaseStep(
        type = BreathingPhaseType.EXHALE,
        label = "Exhale",
        durationSeconds = 4,
        guidance = "Release smoothly through the mouth",
        startScale = 1.6f,
        endScale = 1.0f
      ),
      PhaseStep(
        type = BreathingPhaseType.HOLD_OUT,
        label = "Hold",
        durationSeconds = 2,
        guidance = "Brief stillness before next energizing breath",
        startScale = 1.0f,
        endScale = 1.0f
      )
    )
  )

  val allTechniques = listOf(
    boxBreathing,
    relaxation478,
    equalBreathing,
    coherentBreathing,
    physiologicalSigh,
    deepCalm711,
    energizingBreath
  )
}
