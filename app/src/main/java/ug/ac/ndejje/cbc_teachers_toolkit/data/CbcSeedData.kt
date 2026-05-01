package ug.ac.ndejje.cbc_teachers_toolkit.data

import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity

object CbcSeedData {
    // This list has all the files we put in the assets folder like PDFs and Videos
    val starterResources: List<TeachingResourceEntity> = listOf(
        // These are local files we saved in the app so they work without internet
        TeachingResourceEntity(
            key = "17|NOTES|local_force",
            topicId = 17,
            title = "S.1 Physics: Forces and Motion (Summary Notes)",
            type = "NOTES",
            url = "asset:///Resources/Summary Notes - Topic 1 Forces and Motion - Edexcel Physics IGCSE.pdf",
            source = "Edexcel IGCSE",
            isDownloaded = true,
            localPath = "Resources/Summary Notes - Topic 1 Forces and Motion - Edexcel Physics IGCSE.pdf"
        ),
        TeachingResourceEntity(
            key = "17|VIDEO|local_force",
            topicId = 17,
            title = "What is Force? - Part 1 (Video)",
            type = "VIDEO",
            url = "asset:///Resources/What is Force_ - Part 1_ Forces and Motion _ Physics _ Infinity Learn NEET.mp4.mp4",
            source = "Infinity Learn",
            isDownloaded = true,
            localPath = "Resources/What is Force_ - Part 1_ Forces and Motion _ Physics _ Infinity Learn NEET.mp4.mp4"
        ),

        // S.2 Physics: Work, Energy and Power (Mapped to Topic 17)
        TeachingResourceEntity(
            key = "17|NOTES|local_work",
            topicId = 17,
            title = "S.2 Physics: Work, Energy and Power (Handout)",
            type = "NOTES",
            url = "asset:///Resources/O-Level-New-curriculum-Physics-SV-topic-5-WORK-ENERGY-AND-POWER.pdf",
            source = "NCDC Prototype",
            isDownloaded = true,
            localPath = "Resources/O-Level-New-curriculum-Physics-SV-topic-5-WORK-ENERGY-AND-POWER.pdf"
        ),
        TeachingResourceEntity(
            key = "17|VIDEO|local_work",
            topicId = 17,
            title = "Work, Energy, and Power Introduction (Video)",
            type = "VIDEO",
            url = "asset:///Resources/Work, Energy, and Power - Basic Introduction.mp4.mp4",
            source = "YouTube Education",
            isDownloaded = true,
            localPath = "Resources/Work, Energy, and Power - Basic Introduction.mp4.mp4"
        ),
        TeachingResourceEntity(
            key = "17|VIDEO|local_work_efficiency",
            topicId = 17,
            title = "Energy, Work, Power and Efficiency (Video)",
            type = "VIDEO",
            url = "asset:///Resources/Energy, Work, Power and efficiency for IGCSE, O level and GCSE Physics.mp4.mp4",
            source = "GCSE Physics",
            isDownloaded = true,
            localPath = "Resources/Energy, Work, Power and efficiency for IGCSE, O level and GCSE Physics.mp4.mp4"
        ),

        // S.1 Chemistry: Particulate Nature of Matter (Topic 13)
        TeachingResourceEntity(
            key = "13|VIDEO|local_matter",
            topicId = 13,
            title = "S.1 Chemistry: States of Matter (Offline Video)",
            type = "VIDEO",
            url = "asset:///Resources/states of matter.mp4",
            source = "Internal Storage",
            isDownloaded = true,
            localPath = "Resources/states of matter.mp4"
        ),
        TeachingResourceEntity(
            key = "13|NOTES|local_matter",
            topicId = 13,
            title = "S.1 Chemistry: States of Matter (PDF Guide)",
            type = "NOTES",
            url = "asset:///Resources/5.-State-of-matter.pdf",
            source = "Chemistry Guide",
            isDownloaded = true,
            localPath = "Resources/5.-State-of-matter.pdf"
        ),

        // S.2 Chemistry: Chemical Bonding / Periodic Table (Topic 14)
        TeachingResourceEntity(
            key = "14|VIDEO|local_periodic",
            topicId = 14,
            title = "The Periodic Table Explained (Video)",
            type = "VIDEO",
            url = "asset:///Resources/The Periodic Table Explained.mp4.mp4",
            source = "Internal Storage",
            isDownloaded = true,
            localPath = "Resources/The Periodic Table Explained.mp4.mp4"
        ),

        // S.4 Chemistry: Organic Chemistry (Topic 16)
        TeachingResourceEntity(
            key = "16|VIDEO|local_organic",
            topicId = 16,
            title = "Organic Chemistry in 8 Minutes (Video)",
            type = "VIDEO",
            url = "asset:///Resources/ORGANIC CHEMISTRY Explained in 8 Minutes.mp4.mp4",
            source = "Quick Chemistry",
            isDownloaded = true,
            localPath = "Resources/ORGANIC CHEMISTRY Explained in 8 Minutes.mp4.mp4"
        ),

        // S.1 Chemistry: More Matter Resources
        TeachingResourceEntity(
            key = "13|NOTES|local_matter_handbook",
            topicId = 13,
            title = "States of Matter Handbook (PDF)",
            type = "NOTES",
            url = "asset:///Resources/EL11_StatesofMatter_Handbook.pdf",
            source = "Education Hub",
            isDownloaded = true,
            localPath = "Resources/EL11_StatesofMatter_Handbook.pdf"
        ),
        TeachingResourceEntity(
            key = "13|VIDEO|local_matter_gcse",
            topicId = 13,
            title = "GCSE Chemistry: States of Matter (Video)",
            type = "VIDEO",
            url = "asset:///Resources/GCSE Chemistry - States of Matter & Changing State.mp4.mp4",
            source = "GCSE Revision",
            isDownloaded = true,
            localPath = "Resources/GCSE Chemistry - States of Matter & Changing State.mp4.mp4"
        ),

        // S.2 Physics: Waves (Topic 18)
        TeachingResourceEntity(
            key = "18|VIDEO|local_waves_long",
            topicId = 18,
            title = "Transverse & Longitudinal Waves (Video)",
            type = "VIDEO",
            url = "asset:///Resources/Transverse and Longitudinal Waves.mp4.mp4",
            source = "Science Explained",
            isDownloaded = true,
            localPath = "Resources/Transverse and Longitudinal Waves.mp4.mp4"
        ),
        TeachingResourceEntity(
            key = "18|VIDEO|local_waves_fuse",
            topicId = 18,
            title = "Wave Motion - FuseSchool (Video)",
            type = "VIDEO",
            url = "asset:///Resources/Wave Motion _ Waves _ Physics _ FuseSchool.mp4.mp4",
            source = "FuseSchool",
            isDownloaded = true,
            localPath = "Resources/Wave Motion _ Waves _ Physics _ FuseSchool.mp4.mp4"
        ),
        TeachingResourceEntity(
            key = "18|NOTES|local_waves_notes",
            topicId = 18,
            title = "S.2 Physics: Waves (Summary Notes)",
            type = "NOTES",
            url = "asset:///Resources/Summary Notes - Topic 3 CAIE Physics IGCSE.pdf",
            source = "CAIE IGCSE",
            isDownloaded = true,
            localPath = "Resources/Summary Notes - Topic 3 CAIE Physics IGCSE.pdf"
        ),

        // S.1 Physics: Mechanics and Energy (Topic 17)
        TeachingResourceEntity(
            key = "17|NOTES|local_work_extra",
            topicId = 17,
            title = "Physical Science: Work energy and power (PDF)",
            type = "NOTES",
            url = "asset:///Resources/PHYSICAL SCIENCE_ Work energy and power.pdf",
            source = "Physical Science",
            isDownloaded = true,
            localPath = "Resources/PHYSICAL SCIENCE_ Work energy and power.pdf"
        ),

        // S.1 Chemistry: Particulate Nature of Matter (Topic 13)
        TeachingResourceEntity(
            key = "13|VIDEO|local_gen_chem",
            topicId = 13,
            title = "General Chemistry Explained (Video)",
            type = "VIDEO",
            url = "asset:///Resources/GENERAL CHEMISTRY explained in 19 Minutes.mp4.mp4",
            source = "Chemistry Essentials",
            isDownloaded = true,
            localPath = "Resources/GENERAL CHEMISTRY explained in 19 Minutes.mp4.mp4"
        ),

        // S.2 Chemistry: Periodic Table (Topic 14)
        TeachingResourceEntity(
            key = "14|VIDEO|local_periodic_trends",
            topicId = 14,
            title = "Periodic Table Trends (Video)",
            type = "VIDEO",
            url = "asset:///Resources/The Periodic Table_ Atomic Radius, Ionization Energy, and Electronegativity.mp4.mp4",
            source = "Chemistry Trends",
            isDownloaded = true,
            localPath = "Resources/The Periodic Table_ Atomic Radius, Ionization Energy, and Electronegativity.mp4.mp4"
        )
    )

    // This list has all the topics for different subjects like Biology, Math, etc.
    val topics: List<TopicEntity> = listOf(
        buildTopic(1, "Biology", "S1", "Cells and Classification"),
        buildTopic(2, "Biology", "S2", "Human Body Systems"),
        buildTopic(3, "Biology", "S3", "Genetics and Evolution"),
        buildTopic(4, "Biology", "S4", "Ecology and Environment"),
        buildTopic(13, "Chemistry", "S1", "Particulate Nature of Matter"),
        buildTopic(14, "Chemistry", "S2", "Chemical Reactions and Bonding"),
        buildTopic(15, "Chemistry", "S3", "Acids, Bases and Salts"),
        buildTopic(16, "Chemistry", "S4", "Organic Chemistry"),
        buildTopic(17, "Physics", "S1", "Mechanics and Energy"),
        buildTopic(18, "Physics", "S2", "Waves and Light"),
        buildTopic(19, "Physics", "S3", "Electricity and Magnetism"),
        buildTopic(20, "Physics", "S4", "Atomic and Nuclear Physics"),
        
        // --- HUMANITIES & LANGUAGES ---
        buildTopic(5, "Mathematics", "S1", "Number Bases and Sets"),
        buildTopic(6, "Mathematics", "S2", "Algebra and Equations"),
        buildTopic(7, "Mathematics", "S3", "Geometry and Trigonometry"),
        buildTopic(8, "Mathematics", "S4", "Calculus and Statistics"),
        buildTopic(9, "English Language", "S1", "Narrative and Descriptive Writing"),
        buildTopic(10, "English Language", "S2", "Functional Writing and Literacy"),
        buildTopic(11, "English Language", "S3", "Poetry and Drama Analysis"),
        buildTopic(12, "English Language", "S4", "Public Speaking and Debating"),
        buildTopic(21, "History & Political Education", "S1", "The Cradle of Mankind"),
        buildTopic(22, "History & Political Education", "S2", "Colonialism in Africa"),
        buildTopic(23, "History & Political Education", "S3", "The Road to Independence"),
        buildTopic(24, "History & Political Education", "S4", "Governance and Citizenship"),
        buildTopic(25, "Geography", "S1", "Physical Geography of East Africa"),
        buildTopic(26, "Geography", "S2", "Economic Geography of Uganda"),
        buildTopic(27, "Geography", "S3", "World Geography and Environment"),
        buildTopic(28, "Geography", "S4", "Map Work and Photographic Interpretation"),
        buildTopic(33, "Kiswahili", "S1", "Lugha na Mawasiliano"),
        buildTopic(34, "Kiswahili", "S2", "Ufahamu na Matumizi ya Lugha"),
        buildTopic(37, "Religious Education", "S1", "God's Creation and Human Values"),
        buildTopic(38, "Religious Education", "S2", "Life of Jesus and Christian Values"),
        buildTopic(55, "Local Language", "S1", "Language Structure and Heritage"),
        buildTopic(56, "Foreign Languages", "S1", "Introduction to Communication"),

        // --- VOCATIONAL & CREATIVE ---
        buildTopic(41, "Agriculture", "S1", "Introduction to Farming"),
        buildTopic(45, "ICT", "S1", "Information and Communication Systems"),
        buildTopic(29, "Entrepreneurship Education", "S1", "Business Innovation"),
        buildTopic(50, "Performing Arts", "S1", "Introduction to Music, Dance and Drama"),
        buildTopic(60, "Performing Arts", "S1", "Elements of African Music"),
        buildTopic(51, "Art and Design", "S1", "Foundations of Drawing"),
        buildTopic(61, "Art and Design", "S1", "Graphic Design and Lettering"),
        buildTopic(52, "Nutrition & Food Technology", "S1", "Kitchen Safety and Hygiene"),
        buildTopic(62, "Nutrition & Food Technology", "S1", "Nutritional Needs of Adolescents"),
        buildTopic(53, "Technology and Design", "S1", "Introduction to Technical Drawing"),
        buildTopic(63, "Technology and Design", "S1", "Woodwork and Fabrication"),
        buildTopic(54, "Physical Education", "S1", "Athletics and Team Games")
    )

    // This helper function makes it easy to add a new topic to our list
    private fun buildTopic(
        id: Int,
        subject: String,
        classLevel: String,
        title: String
    ): TopicEntity {
        val learnerActivity = "Learners work in groups to solve a practical task related to $title."
        val lessonPlanText = """
            Competency focus: By the end of this lesson, learners should apply $title concepts in real contexts.
            Lesson objectives:
            1) Define and explain key concepts in $title.
            2) Demonstrate understanding through classroom activity and peer explanation.
            3) Apply the concept to local school/community examples.

            Suggested lesson flow:
            - Starter (5-10 min): prior knowledge questions.
            - Core (25-30 min): teacher guidance + group activity.
            - Practice (10-15 min): structured learner task.
            - Reflection (5 min): exit ticket and recap.
        """.trimIndent()
        val projectIdeasText = """
            Project options for $title:
            - Design a low-cost teaching aid using local materials.
            - Present a mini-demonstration in groups.
            - Create a short class poster summarizing concepts and examples.
            Output should include written explanation + oral presentation.
        """.trimIndent()
        val rubricText = """
            Assessment rubric (4-point):
            - Concept accuracy (0-4)
            - Application to real context (0-4)
            - Collaboration and participation (0-4)
            - Communication and presentation clarity (0-4)
            Total = 16 marks.
        """.trimIndent()
        val teachingTipsText = """
            Teaching guide:
            - Begin with a local example before introducing theory.
            - Use learner-centred questioning and pair discussion.
            - Differentiate tasks for mixed ability groups.
            - End with quick formative assessment and feedback notes.
        """.trimIndent()
        return TopicEntity(
            id = id,
            title = title,
            subject = subject,
            classLevel = classLevel,
            lessonPlan = "$lessonPlanText\n\nClass context: $classLevel $subject\n$learnerActivity",
            projectIdeas = projectIdeasText,
            assessmentRubric = rubricText,
            teachingTips = teachingTipsText
        )
    }
}
