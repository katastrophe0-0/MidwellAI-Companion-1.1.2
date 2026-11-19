package com.example.data.constants

import com.example.data.model.ResourceItem

data class PmrStep(
    val muscleGroup: String,
    val tenseInstruction: String,
    val releaseInstruction: String,
    val tenseSeconds: Int = 5,
    val releaseSeconds: Int = 10
)

data class SomaticZoneRoutine(
    val zone: String,
    val title: String,
    val durationSeconds: Int,
    val description: String,
    val instructions: List<String>,
    val accentHex: Long
)

object WellnessData {
    val SLEEP_WINDDOWN_CHECKLIST = listOf(
        "Dim overhead lights & enable blue-light filter",
        "Silence non-urgent notifications",
        "5 minutes of slow exhale breathing or gentle stretch",
        "Write tomorrow's top 1 priority to clear your mind",
        "Cool bedroom & comfortable hydration sip"
    )

    val BODY_ZONES = listOf("All Zones", "Jaw", "Neck & Shoulders", "Chest", "Stomach")

    val SOMATIC_ZONE_ROUTINES = listOf(
        SomaticZoneRoutine(
            zone = "Jaw",
            title = "Jaw & Facial Unclenching",
            durationSeconds = 60,
            description = "Releases masseter tension and softens fight-or-flight bracing.",
            instructions = listOf(
                "Let your tongue rest gently on the floor of your mouth.",
                "Part your back teeth slightly while keeping lips soft.",
                "Place two fingertips along your cheekbones and make slow circles.",
                "Exhale with an audible 'sigh' to drop the lower jaw."
            ),
            accentHex = 0xFFEC4899
        ),
        SomaticZoneRoutine(
            zone = "Neck & Shoulders",
            title = "Shoulder Drop & Neck Sweep",
            durationSeconds = 90,
            description = "Unwinds trapped upper-trapezius stress from screen focus or worry.",
            instructions = listOf(
                "Inhale and shrug shoulders high toward your ears for 3 seconds.",
                "Exhale completely and let shoulders fall heavy.",
                "Slowly tilt right ear toward right shoulder; breathe for 20s.",
                "Repeat gently on the left side without forcing."
            ),
            accentHex = 0xFFF97316
        ),
        SomaticZoneRoutine(
            zone = "Chest",
            title = "Butterfly Hug & Rib Expansion",
            durationSeconds = 120,
            description = "Bilateral chest tapping combined with lateral ribcage breathing.",
            instructions = listOf(
                "Cross arms over your chest, fingertips below collarbones.",
                "Alternately tap left and right hands at a calm walking pace.",
                "Imagine your ribs expanding sideways like an accordion.",
                "Lengthen each exhale by 2 seconds."
            ),
            accentHex = 0xFF6366F1
        ),
        SomaticZoneRoutine(
            zone = "Stomach",
            title = "Belly Softening & Vagal Sigh",
            durationSeconds = 90,
            description = "Relaxes gut bracing and activates the parasympathetic vagus nerve.",
            instructions = listOf(
                "Place one warm palm gently over your lower abdomen.",
                "Unbutton or loosen tight waistbands if sitting.",
                "Let your belly rise naturally into your hand as you inhale.",
                "Double-inhale through the nose, then sigh out slowly through the mouth."
            ),
            accentHex = 0xFF14B8A6
        )
    )

    val PMR_STEPS = listOf(
        PmrStep("Hands & Forearms", "Clench both fists tightly and feel the tension.", "Release your fingers wide and notice warmth flowing into your palms."),
        PmrStep("Shoulders & Neck", "Lift your shoulders high toward your ears.", "Drop your shoulders down completely, letting gravity hold your arms."),
        PmrStep("Jaw & Forehead", "Scrunch your forehead and gently press lips together.", "Smooth your brow, part your teeth, and soften your cheeks."),
        PmrStep("Chest & Core", "Take a deep breath and tighten your core muscles.", "Exhale softly and let your ribcage and stomach sink back."),
        PmrStep("Legs & Feet", "Press your heels down and curl your toes gently.", "Unfurl your toes and feel heaviness settle through both legs.")
    )

    val DIRECTORY_CATEGORIES = listOf("All", "24/7 Crisis", "Text Lines", "Community & Youth Support", "Local Clinics")

    val CATEGORIZED_DIRECTORY = listOf(
        ResourceItem(
            name = "988 Suicide & Crisis Lifeline",
            description = "Free, confidential 24/7 support via phone or text for anyone in distress.",
            category = "24/7 Crisis",
            phone = "988",
            url = "https://988lifeline.org/"
        ),
        ResourceItem(
            name = "Emergency Medical Services (911)",
            description = "Immediate local emergency dispatch for acute life-safety situations.",
            category = "24/7 Crisis",
            phone = "911"
        ),
        ResourceItem(
            name = "Crisis Text Line",
            description = "Text with a trained volunteer crisis counselor 24/7 from anywhere in the US & Canada.",
            category = "Text Lines",
            phoneText = "Text HOME to 741741",
            url = "https://www.crisistextline.org/"
        ),
        ResourceItem(
            name = "TrevorText (LGBTQ+ Youth)",
            description = "Confidential crisis intervention via text & chat for LGBTQ young people.",
            category = "Text Lines",
            phone = "1-866-488-7386",
            phoneText = "Text START to 678-678",
            url = "https://www.thetrevorproject.org/"
        ),
        ResourceItem(
            name = "NAMI Helpline & Peer Groups",
            description = "Free peer-led support groups, family education, and navigation guidance.",
            category = "Community & Youth Support",
            phone = "1-800-950-6264",
            email = "info@nami.org",
            url = "https://www.nami.org/"
        ),
        ResourceItem(
            name = "SAMHSA Treatment Locator",
            description = "24/7 national routing for local mental health clinics, sliding-scale care, and recovery.",
            category = "Local Clinics",
            phone = "1-800-662-4357",
            url = "https://findtreatment.gov/"
        ),
        ResourceItem(
            name = "Psychology Today Clinic & Therapist Finder",
            description = "Filter local licensed therapists, clinics, and telehealth providers by insurance.",
            category = "Local Clinics",
            url = "https://www.psychologytoday.com/us/therapists"
        )
    )
}
