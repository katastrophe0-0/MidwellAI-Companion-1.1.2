package com.example.data.constants

import com.example.data.model.EmergencyPlanItem
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizResult
import com.example.data.model.ResourceItem

data class MoodOption(
    val level: Int,
    val emoji: String,
    val label: String,
    val colorHex: Long
)

data class QuizDefinition(
    val id: String,
    val label: String,
    val fullLabel: String,
    val description: String,
    val questions: List<QuizQuestion>,
    val accentHex: Long,
    val evaluate: (Int) -> QuizResult
)

data class GuidedTemplate(
    val id: String,
    val label: String,
    val prompt: String,
    val template: String
)

data class BreathingStep(
    val label: String,
    val durationMs: Long,
    val targetScale: Float
)

data class BreathingTechnique(
    val name: String,
    val description: String,
    val steps: List<BreathingStep>
)

data class SomaticExercise(
    val id: String,
    val title: String,
    val durationSeconds: Int,
    val description: String,
    val instructions: List<String>,
    val accentHex: Long
)

data class CyclePhaseInfo(
    val name: String,
    val startDay: Int,
    val endDay: Int,
    val accentHex: Long,
    val mood: String,
    val insight: String,
    val action: String
)

object MindWellConstants {

    val MOODS = listOf(
        MoodOption(1, "😞", "Awful", 0xFFF87171),
        MoodOption(2, "😕", "Bad", 0xFFFB923C),
        MoodOption(3, "😐", "Okay", 0xFFFACC15),
        MoodOption(4, "🙂", "Good", 0xFFA3E635),
        MoodOption(5, "😄", "Great", 0xFF4ADE80)
    )

    val MOOD_TAGS = listOf("Work", "Sleep", "Family", "Health", "Social", "Finance", "Weather", "Hobby")

    val AVATARS = listOf("😀", "😌", "🧘", "🌿", "⭐", "🌙", "🦊", "🐱", "🦁", "🐼", "🌸", "🌊")

    val ONBOARDING_GOALS = listOf(
        Triple("anxiety", "Reduce Anxiety", "😌"),
        Triple("sleep", "Improve Sleep", "😴"),
        Triple("focus", "Better Focus", "🎯"),
        Triple("mood", "Boost Mood", "☀️"),
        Triple("stress", "Manage Stress", "🌱")
    )

    val ONBOARDING_CHALLENGES = listOf(
        "Work Stress", "Relationship Issues", "Insomnia", "Loneliness", "Health Concerns", "Financial Stress"
    )

    val FOCUS_ACTIVITIES = listOf(
        "Take 5 deep, slow breaths, focusing only on your breathing.",
        "Step outside for a few minutes and notice 3 things in nature.",
        "Write down one thing you are grateful for today.",
        "Do a gentle 5-minute stretch to release tension.",
        "Listen to one of your favorite uplifting songs without distractions.",
        "Drink a full glass of water mindfully.",
        "Jot down a small, positive goal for tomorrow.",
        "Send a short, kind message to a friend or family member.",
        "Tidy up one small area of your space for 2 minutes.",
        "Close your eyes and visualize a place that makes you feel calm and happy."
    )

    val DEFAULT_EMERGENCY_ITEMS = listOf(
        EmergencyPlanItem("1", "contact", "Crisis Lifeline", "988"),
        EmergencyPlanItem("2", "mantra", "Coping Statement", "This feeling is temporary. I am safe."),
        EmergencyPlanItem("3", "activity", "Calming Action", "Drink a glass of cold water.")
    )

    val GUIDED_TEMPLATES = listOf(
        GuidedTemplate("free", "Free Write", "Free Write", ""),
        GuidedTemplate(
            "emotional_dive",
            "Deep Dive",
            "Emotional Deep Dive",
            "What emotion is most present right now?\n\nWhere do I feel it in my body?\n\nWhat is this emotion trying to tell me?\n\nWhat do I need most right now?"
        ),
        GuidedTemplate(
            "self_compassion",
            "Self Compassion",
            "Self Compassion",
            "I am judging myself for...\n\nIf a friend were in this situation, I would tell them...\n\nI can offer myself kindness by..."
        ),
        GuidedTemplate(
            "morning",
            "Morning Intent",
            "Morning Intention",
            "Today's Intention:\n\nThree things I want to accomplish:\n1. \n2. \n3. \n\nI choose to feel..."
        ),
        GuidedTemplate(
            "evening",
            "Evening Reflection",
            "Evening Reflection",
            "Highlights of the day:\n\nOne thing I learned:\n\nI am grateful for:\n\nTomorrow, I will..."
        ),
        GuidedTemplate(
            "anxiety",
            "Anxiety Check",
            "Anxiety Release",
            "What am I feeling right now?\n\nWhat is the worst that could happen?\n\nWhat is the best that could happen?\n\nWhat is most likely to happen?"
        ),
        GuidedTemplate(
            "gratitude",
            "Gratitude",
            "Gratitude Practice",
            "1. I am grateful for...\n2. A person who helped me...\n3. A small joy I experienced..."
        ),
        GuidedTemplate(
            "stress",
            "Stress Dump",
            "Stress Relief",
            "What is stressing me out?\n\nIs this within my control?\n\nOne small step I can take right now:"
        )
    )

    val MEDITATION_SCRIPTS = linkedMapOf(
        "5-Minute Mindfulness" to "Take a deep breath in... and out. For the next five minutes, we will focus on the present moment. Find a comfortable position, either sitting or lying down. Close your eyes gently. Bring your attention to your breath. Notice the sensation of the air entering your nostrils... filling your lungs... and the gentle release as you exhale. Your mind may wander, and that's okay. When it does, gently guide it back to your breath. Notice any sounds around you without judgment. Just observe them as part of this moment. Feel the contact of your body with the surface beneath you. Feel the gentle rise and fall of your chest. You are safe. You are present. Continue to breathe, moment by moment. As we come to a close, slowly bring your awareness back to the room. Wiggle your fingers and toes. And when you're ready, gently open your eyes. Carry this sense of peace with you.",
        "Body Scan" to "Find a comfortable position lying on your back. Close your eyes and take a few deep breaths. We will now begin a body scan meditation. Bring your awareness to your toes on your left foot. Notice any sensations... warmth, coolness, tingling... without judgment. Now, move your attention to the sole of your foot... your heel... the top of your foot. Slowly, move your awareness up your left leg... to your calf... your shin... your knee... your thigh. Just observe. Now, bring your attention to your right foot, and repeat the process. Your toes... your entire foot... your lower leg... your knee... your thigh. Now, focus on your hips... your abdomen... your lower back. Notice the gentle movement with each breath. Bring your awareness to your chest and heart area. Then to your hands and fingers... up through your arms to your shoulders. Finally, bring your attention to your neck... your jaw... your face... your forehead. Let go of any tension you find. Now, feel your whole body, buzzing with a gentle energy, completely relaxed. Rest in this awareness for a few moments. When you are ready, gently begin to move and open your eyes."
    )

    val BREATHING_TECHNIQUES = listOf(
        BreathingTechnique(
            name = "Box Breathing",
            description = "Reduces stress and improves focus. Inhale 4s, Hold 4s, Exhale 4s, Hold 4s.",
            steps = listOf(
                BreathingStep("Inhale", 4000L, 1.45f),
                BreathingStep("Hold", 4000L, 1.45f),
                BreathingStep("Exhale", 4000L, 1.0f),
                BreathingStep("Hold", 4000L, 1.0f)
            )
        ),
        BreathingTechnique(
            name = "4-7-8 Relax",
            description = "Natural tranquilizer for the nervous system. Good for sleep.",
            steps = listOf(
                BreathingStep("Inhale", 4000L, 1.45f),
                BreathingStep("Hold", 7000L, 1.45f),
                BreathingStep("Exhale", 8000L, 1.0f)
            )
        ),
        BreathingTechnique(
            name = "Coherent",
            description = "Balances the heart and brain. 5.5s Inhale, 5.5s Exhale.",
            steps = listOf(
                BreathingStep("Inhale", 5500L, 1.45f),
                BreathingStep("Exhale", 5500L, 1.0f)
            )
        )
    )

    val SOMATIC_EXERCISES = listOf(
        SomaticExercise(
            id = "shake",
            title = "The Shake",
            durationSeconds = 60,
            description = "Discharge excess energy and tension by shaking your body vigorously.",
            instructions = listOf(
                "Stand up with your feet hip-width apart.",
                "Start shaking your hands and wrists.",
                "Let the movement travel up your arms to your shoulders.",
                "Bounce on your heels and let your whole body shake.",
                "Exhale loudly through your mouth as you shake."
            ),
            accentHex = 0xFFF97316
        ),
        SomaticExercise(
            id = "butterfly",
            title = "Butterfly Hug",
            durationSeconds = 120,
            description = "A bilateral stimulation technique to process stress and calm the amygdala.",
            instructions = listOf(
                "Cross your arms over your chest.",
                "Hook your thumbs together to form a butterfly shape.",
                "Place your fingertips just below your collarbones.",
                "Alternately tap your hands against your chest: left, right, left, right.",
                "Breathe slowly and deeply while tapping."
            ),
            accentHex = 0xFFEC4899
        ),
        SomaticExercise(
            id = "orienting",
            title = "Orienting",
            durationSeconds = 90,
            description = "Connect to safety in your environment to engage the parasympathetic system.",
            instructions = listOf(
                "Sit comfortably and let your eyes wander slowly around the room.",
                "Let your gaze rest on an object that is pleasing or neutral.",
                "Slowly turn your head to look behind you, then back.",
                "Name 3 things you can see, 3 things you can hear, 3 things you can touch.",
                "Feel your feet on the floor."
            ),
            accentHex = 0xFF14B8A6
        ),
        SomaticExercise(
            id = "tapping",
            title = "Basic Tapping",
            durationSeconds = 180,
            description = "Gentle tapping on acupressure points to reduce cortisol levels.",
            instructions = listOf(
                "Tap the side of your hand (karate chop point) continuously.",
                "Tap the beginning of your eyebrow.",
                "Tap the side of your eye.",
                "Tap under your eye.",
                "Tap under your nose.",
                "Tap your chin.",
                "Tap your collarbone.",
                "Tap under your arm."
            ),
            accentHex = 0xFF6366F1
        )
    )

    val CYCLE_PHASES = listOf(
        CyclePhaseInfo(
            name = "Menstrual",
            startDay = 1,
            endDay = 5,
            accentHex = 0xFFEF4444,
            mood = "Inward & Reflective",
            insight = "Estrogen and progesterone are low. You may feel tired or withdrawn. It's a natural time for rest and journaling.",
            action = "Prioritize sleep and gentle movement like walking or stretching."
        ),
        CyclePhaseInfo(
            name = "Follicular",
            startDay = 6,
            endDay = 13,
            accentHex = 0xFFF472B6,
            mood = "Energetic & Creative",
            insight = "Estrogen is rising. Mental clarity and energy usually increase. A great time for brainstorming, learning, and social activities.",
            action = "Tackle complex tasks or start new habits."
        ),
        CyclePhaseInfo(
            name = "Ovulation",
            startDay = 14,
            endDay = 17,
            accentHex = 0xFFC084FC,
            mood = "Confident & Social",
            insight = "Estrogen peaks and testosterone rises slightly. Verbal skills and confidence are often at their highest.",
            action = "Schedule important conversations or social gatherings."
        ),
        CyclePhaseInfo(
            name = "Luteal",
            startDay = 18,
            endDay = 28,
            accentHex = 0xFF818CF8,
            mood = "Detailed & Sensitive",
            insight = "Progesterone rises, which can have a sedating effect but may also trigger anxiety or PMS symptoms as the phase ends.",
            action = "Be gentle with yourself. Focus on organizing, wrapping up tasks, and self-care."
        )
    )

    val STORY_THEMES = listOf(
        "A walk through an ancient forest",
        "Floating on a cloud at sunset",
        "A cozy cabin in the snowy mountains",
        "A gentle stream in a secret garden",
        "Stargazing from a quiet hilltop"
    )

    val ZODIAC_SIGNS = listOf(
        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    )

    val COGNITIVE_DISTORTIONS = listOf(
        "All-or-Nothing Thinking", "Catastrophizing", "Emotional Reasoning", "Mind Reading",
        "Fortune Telling", "Personalization", "Filtering", "Should Statements"
    )

    val HELPLINES = listOf(
        ResourceItem("988 Suicide & Crisis Lifeline", "Free, confidential support for people in distress, 24/7.", phone = "988", url = "https://988lifeline.org/"),
        ResourceItem("Crisis Text Line", "Text with a trained crisis counselor for free, 24/7.", phoneText = "Text HOME to 741741", url = "https://www.crisistextline.org/"),
        ResourceItem("The Trevor Project", "Support for LGBTQ young people in crisis, 24/7.", phone = "1-866-488-7386", url = "https://www.thetrevorproject.org/")
    )

    val ORGANIZATIONS = listOf(
        ResourceItem("National Alliance on Mental Illness (NAMI)", "Advocacy, education, support, and public awareness.", url = "https://www.nami.org/"),
        ResourceItem("National Institute of Mental Health (NIMH)", "The lead federal agency for research on mental disorders.", url = "https://www.nimh.nih.gov/"),
        ResourceItem("Mental Health America (MHA)", "Promoting mental health as a critical part of overall wellness.", url = "https://mhanational.org/"),
        ResourceItem("Depression and Bipolar Support Alliance (DBSA)", "Support groups and wellness resources for depression and bipolar disorder.", url = "https://www.dbsalliance.org/"),
        ResourceItem("International OCD Foundation (IOCDF)", "Resources and support for those affected by OCD and related disorders.", url = "https://iocdf.org/"),
        ResourceItem("National Education Alliance for BPD (NEABPD)", "Education, research, and support for borderline personality disorder.", url = "https://www.borderlinepersonalitydisorder.org/")
    )

    val THERAPIST_FINDERS = listOf(
        ResourceItem("Psychology Today Therapist Finder", "Comprehensive directory of therapists, psychiatrists, and treatment centers.", url = "https://www.psychologytoday.com/us/therapists"),
        ResourceItem("SAMHSA's National Helpline", "Treatment referral routing service for mental and substance use support.", phone = "1-800-662-4357", url = "https://www.samhsa.gov/find-help/national-helpline")
    )

    private val frequencyOptions = listOf("Not at all", "Several days", "More than half the days", "Nearly every day")
    private val frequencyValues = listOf(0, 1, 2, 3)

    private val adhdOptions = listOf("Never", "Rarely", "Sometimes", "Often", "Very Often")
    private val adhdValues = listOf(0, 1, 2, 3, 4)

    private val yesNoOptions = listOf("No", "Yes")
    private val yesNoValues = listOf(0, 1)

    private val ociOptions = listOf("Not at all", "A little", "Moderately", "A lot", "Extremely")
    private val ociValues = listOf(0, 1, 2, 3, 4)

    val QUIZZES: List<QuizDefinition> = listOf(
        QuizDefinition(
            id = "phq9",
            label = "Depression",
            fullLabel = "Depression (PHQ-9)",
            description = "A standard tool to monitor the severity of depression symptoms over the last 2 weeks.",
            accentHex = 0xFF3B82F6,
            questions = listOf(
                "Little interest or pleasure in doing things",
                "Feeling down, depressed, or hopeless",
                "Trouble falling or staying asleep, or sleeping too much",
                "Feeling tired or having little energy",
                "Poor appetite or overeating",
                "Feeling bad about yourself — or that you are a failure or have let yourself or your family down",
                "Trouble concentrating on things, such as reading the newspaper or watching television",
                "Moving or speaking so slowly that other people could have noticed? Or the opposite — being so fidgety or restless that you have been moving around a lot more than usual",
                "Thoughts that you would be better off dead, or of hurting yourself in some way"
            ).map { QuizQuestion(it, frequencyOptions, frequencyValues) },
            evaluate = { score ->
                when {
                    score <= 4 -> QuizResult(score, "Minimal depression", "Your responses suggest minimal or no symptoms commonly associated with depression. Continue monitoring your mood.")
                    score <= 9 -> QuizResult(score, "Mild depression", "Your responses share characteristics commonly associated with mild symptoms of depression. Talking with a trusted friend or provider may help.")
                    score <= 14 -> QuizResult(score, "Moderate depression", "Your responses share characteristics associated with moderate depression symptoms. Consulting a healthcare professional is recommended.")
                    score <= 19 -> QuizResult(score, "Moderately severe depression", "Your responses suggest moderately severe symptoms. Please consider reaching out to a licensed healthcare provider for support.")
                    else -> QuizResult(score, "Severe depression", "Your responses suggest severe symptoms. Please seek professional clinical support promptly or call 988 if in distress.")
                }
            }
        ),
        QuizDefinition(
            id = "gad7",
            label = "Anxiety",
            fullLabel = "Anxiety (GAD-7)",
            description = "A screening tool for Generalized Anxiety Disorder and symptom severity.",
            accentHex = 0xFF14B8A6,
            questions = listOf(
                "Feeling nervous, anxious, or on edge",
                "Not being able to stop or control worrying",
                "Worrying too much about different things",
                "Trouble relaxing",
                "Being so restless that it is hard to sit still",
                "Becoming easily annoyed or irritable",
                "Feeling afraid, as if something awful might happen"
            ).map { QuizQuestion(it, frequencyOptions, frequencyValues) },
            evaluate = { score ->
                when {
                    score <= 4 -> QuizResult(score, "Minimal anxiety", "Your score suggests minimal anxiety symptoms.")
                    score <= 9 -> QuizResult(score, "Mild anxiety", "Your responses share characteristics of mild anxiety. Mindfulness and relaxation exercises may be supportive.")
                    score <= 14 -> QuizResult(score, "Moderate anxiety", "Your score falls in the moderate anxiety range. Speaking with a healthcare provider could be beneficial.")
                    else -> QuizResult(score, "Severe anxiety", "Your score falls in the severe anxiety range. Seeking professional consultation is strongly recommended.")
                }
            }
        ),
        QuizDefinition(
            id = "mdq",
            label = "Bipolar",
            fullLabel = "Bipolar (MDQ)",
            description = "Screens for Bipolar Spectrum Disorder symptoms and history.",
            accentHex = 0xFFF97316,
            questions = listOf(
                "Has there ever been a period of time when you felt so good or hyper that other people thought you were not your normal self, or you were so hyper that you got into trouble?",
                "During that time, were you so irritable that you shouted at people or started fights or arguments?",
                "During that time, did you feel much more self-confident than usual?",
                "During that time, did you get much less sleep than usual and found you didn’t really miss it?",
                "During that time, were you much more talkative or spoke much faster than usual?",
                "During that time, did thoughts race through your head or could you not slow your mind down?",
                "During that time, were you so easily distracted by things around you that you had trouble concentrating or staying on track?",
                "During that time, did you have much more energy than usual?",
                "During that time, were you much more active or did many more things than usual?",
                "During that time, were you much more social or outgoing than usual, for example, you telephoned friends in the middle of the night?",
                "During that time, did you do things that were unusual for you or that other people might have thought were excessive, foolish, or risky?",
                "During that time, did spending money get you or your family into trouble?"
            ).map { QuizQuestion(it, yesNoOptions, yesNoValues) },
            evaluate = { score ->
                if (score >= 7) {
                    QuizResult(score, "Positive Screen", "You answered Yes to 7 or more items. If these experiences occurred during the same period and affected daily functioning, consider discussing them with a psychiatrist.")
                } else {
                    QuizResult(score, "Negative Screen", "Your responses do not currently exceed the positive screening threshold, though any mood fluctuation concerns are worth discussing with a provider.")
                }
            }
        ),
        QuizDefinition(
            id = "msi_bpd",
            label = "BPD",
            fullLabel = "Borderline Personality (MSI-BPD)",
            description = "Screens for traits associated with Borderline Personality Disorder.",
            accentHex = 0xFFEC4899,
            questions = listOf(
                "Have any of your closest relationships been troubled by a lot of arguments or repeated breakups?",
                "Have you deliberately hurt yourself physically (e.g., punched, cut, burned) or made a suicide attempt?",
                "Have you had at least two other problems with impulsivity (e.g., eating binges, spending sprees, drinking too much, verbal outbursts)?",
                "Have you been extremely moody?",
                "Have you felt very angry often?",
                "Have you often been distrustful of other people?",
                "Have you frequently felt unreal or as if things around you were unreal?",
                "Have you chronically felt empty?",
                "Have you often felt that you had no idea of who you are or that you have no identity?",
                "Have you made desperate efforts to avoid feeling abandoned or being abandoned (e.g., called someone many times, pleaded with them)?"
            ).map { QuizQuestion(it, yesNoOptions, yesNoValues) },
            evaluate = { score ->
                if (score >= 7) {
                    QuizResult(score, "Positive Screen", "Your responses share characteristics commonly explored in BPD evaluations. Speaking with a mental health clinician is recommended.")
                } else {
                    QuizResult(score, "Negative Screen", "Your responses fall below the screening threshold. If emotional regulation feels challenging, therapy can still offer helpful tools.")
                }
            }
        ),
        QuizDefinition(
            id = "oci_r",
            label = "OCD",
            fullLabel = "OCD (OCI-R)",
            description = "Assesses symptoms of Obsessive-Compulsive Disorder.",
            accentHex = 0xFFA855F7,
            questions = listOf(
                "I have saved up so many things that they get in the way.",
                "I check things more often than necessary.",
                "I get upset if objects are not arranged properly.",
                "I feel compelled to count while I am doing things.",
                "I find it difficult to touch an object when I know it has been touched by strangers or certain people.",
                "I find it difficult to control my own thoughts.",
                "I collect things I don’t need.",
                "I repeatedly check doors, windows, drawers, etc.",
                "I get upset if others change the way I have arranged things.",
                "I feel I have to repeat certain numbers.",
                "I sometimes have to wash or clean myself simply because I feel contaminated.",
                "I am upset by unpleasant thoughts that come into my mind against my will."
            ).map { QuizQuestion(it, ociOptions, ociValues) },
            evaluate = { score ->
                if (score >= 21) {
                    QuizResult(score, "Likely OCD", "Your score suggests notable obsessive-compulsive traits. A clinical evaluation can help explore ERP or other evidence-based options.")
                } else {
                    QuizResult(score, "Unlikely OCD", "Your score is below the clinical threshold for OCD. If intrusive thoughts or rituals cause distress, support is still available.")
                }
            }
        ),
        QuizDefinition(
            id = "asrs",
            label = "ADHD",
            fullLabel = "ADHD (ASRS-v1.1)",
            description = "Screens for adult ADHD symptoms.",
            accentHex = 0xFFEAB308,
            questions = listOf(
                "How often do you have trouble wrapping up the final details of a project, once the challenging parts have been done?",
                "How often do you have difficulty getting things in order when you have to do a task that requires organization?",
                "How often do you have problems remembering appointments or obligations?",
                "When you have a task that requires a lot of thought, how often do you avoid or delay getting started?",
                "How often do you fidget or squirm with your hands or feet when you have to sit down for a long time?",
                "How often do you feel overly active and compelled to do things, like you were driven by a motor?"
            ).map { QuizQuestion(it, adhdOptions, adhdValues) },
            evaluate = { score ->
                when {
                    score <= 9 -> QuizResult(score, "Unlikely ADHD", "Your responses are below the typical adult ADHD screening range.")
                    score <= 13 -> QuizResult(score, "Possible ADHD", "Your responses suggest some executive function traits worth discussing with a provider.")
                    else -> QuizResult(score, "Likely ADHD", "Your responses are strongly consistent with adult ADHD traits. A formal evaluation is recommended.")
                }
            }
        ),
        QuizDefinition(
            id = "isi",
            label = "Sleep",
            fullLabel = "Insomnia (ISI)",
            description = "Measures the nature, severity, and impact of insomnia.",
            accentHex = 0xFF6366F1,
            questions = listOf(
                QuizQuestion("Difficulty falling asleep", listOf("None", "Mild", "Moderate", "Severe", "Very Severe"), adhdValues),
                QuizQuestion("Difficulty staying asleep", listOf("None", "Mild", "Moderate", "Severe", "Very Severe"), adhdValues),
                QuizQuestion("Problems waking up too early", listOf("None", "Mild", "Moderate", "Severe", "Very Severe"), adhdValues),
                QuizQuestion("How satisfied/dissatisfied are you with your CURRENT sleep pattern?", listOf("Very Satisfied", "Satisfied", "Neutral", "Dissatisfied", "Very Dissatisfied"), adhdValues),
                QuizQuestion("How noticeable to others do you think your sleep problem is in terms of impairing the quality of your life?", listOf("Not at all", "A little", "Somewhat", "Much", "Very Much"), adhdValues),
                QuizQuestion("How worried/distressed are you about your current sleep problem?", listOf("Not at all", "A little", "Somewhat", "Much", "Very Much"), adhdValues),
                QuizQuestion("To what extent do you consider your sleep problem to interfere with your daily functioning?", listOf("Not at all", "A little", "Somewhat", "Much", "Very Much"), adhdValues)
            ),
            evaluate = { score ->
                when {
                    score <= 7 -> QuizResult(score, "No significant insomnia", "Your sleep patterns appear generally healthy.")
                    score <= 14 -> QuizResult(score, "Subthreshold insomnia", "You may be experiencing mild sleep difficulties. Sleep hygiene tools can help.")
                    score <= 21 -> QuizResult(score, "Clinical insomnia (Moderate)", "Your responses suggest moderate insomnia. Consider CBT-I strategies or speaking with a healthcare provider.")
                    else -> QuizResult(score, "Clinical insomnia (Severe)", "Your responses suggest severe insomnia. Consulting a sleep specialist or physician is recommended.")
                }
            }
        ),
        QuizDefinition(
            id = "ptsd",
            label = "PTSD",
            fullLabel = "PTSD (PC-PTSD-5)",
            description = "Screens for Post-Traumatic Stress Disorder symptoms.",
            accentHex = 0xFFEF4444,
            questions = listOf(
                "Had nightmares about the event(s) or thought about the event(s) when you did not want to?",
                "Tried hard not to think about the event(s) or went out of your way to avoid situations that reminded you of the event(s)?",
                "Been constantly on guard, watchful, or easily startled?",
                "Felt numb or detached from people, activities, or your surroundings?",
                "Felt guilty or unable to stop blaming yourself or others for the event(s) or any problems the event(s) may have caused?"
            ).map { QuizQuestion(it, yesNoOptions, yesNoValues) },
            evaluate = { score ->
                if (score < 3) {
                    QuizResult(score, "Negative Screen", "Your responses do not currently exceed the PTSD screening threshold, though support is always available if past experiences cause distress.")
                } else {
                    QuizResult(score, "Positive Screen", "Your responses share characteristics associated with post-traumatic stress. Consider speaking with a trauma-informed clinician.")
                }
            }
        )
    )
}
