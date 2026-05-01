package ug.ac.ndejje.cbc_teachers_toolkit.data

import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity

object CbcSeedData {
    val starterResources: List<TeachingResourceEntity> = listOf(
        // --- ATTACHED LOCAL RESOURCES (After you download and add to assets/Resources/) ---
        TeachingResourceEntity(
            key = "1|VIDEO|local",
            topicId = 1,
            title = "S.1 Biology: Classification (Offline Video)",
            type = "VIDEO",
            url = "asset:///Resources/biology_s1_classification.mp4",
            source = "Internal Storage",
            isDownloaded = true,
            localPath = "Resources/biology_s1_classification.mp4"
        ),
        TeachingResourceEntity(
            key = "17|NOTES|local",
            topicId = 17,
            title = "S.1 Physics: Force and Motion (Official PDF)",
            type = "NOTES",
            url = "asset:///Resources/physics_s1_force.pdf",
            source = "NCDC Prototype",
            isDownloaded = true,
            localPath = "Resources/physics_s1_force.pdf"
        ),
        TeachingResourceEntity(
            key = "5|NOTES|local",
            topicId = 5,
            title = "S.1 Math: Sets and Venn Diagrams (PDF)",
            type = "NOTES",
            url = "asset:///Resources/math_s1_sets.pdf",
            source = "NCDC Prototype",
            isDownloaded = true,
            localPath = "Resources/math_s1_sets.pdf"
        ),
        TeachingResourceEntity(
            key = "45|VIDEO|local",
            topicId = 45,
            title = "S.1 ICT: Intro to Computers (Offline Video)",
            type = "VIDEO",
            url = "asset:///Resources/ict_s1_intro.mp4",
            source = "Internal Storage",
            isDownloaded = true,
            localPath = "Resources/ict_s1_intro.mp4"
        ),

        // --- REMOTE FALLBACKS ---
        TeachingResourceEntity(
            key = "17|VIDEO|https://www.youtube.com/watch?v=ZM8ECpBuQYE",
            topicId = 17,
            title = "S.1 Physics: Force and Motion (Video Lesson)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=ZM8ECpBuQYE",
            source = "YouTube"
        ),
        TeachingResourceEntity(
            key = "18|VIDEO|https://www.youtube.com/watch?v=2WPT06R4Ac4",
            topicId = 18,
            title = "S.2 Physics: Work, Energy and Power (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=2WPT06R4Ac4",
            source = "YouTube"
        ),
        TeachingResourceEntity(
            key = "18|NOTES|https://ncdc.go.ug/sites/default/files/S2_PHYSICS_PROTOTYPE.pdf",
            topicId = 18,
            title = "S.2 Physics: Work and Energy (PDF Guide)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S2_PHYSICS_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "19|NOTES|https://ncdc.go.ug/sites/default/files/S3_PHYSICS_PROTOTYPE.pdf",
            topicId = 19,
            title = "S.3 Physics: Electricity and Circuits (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S3_PHYSICS_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "19|VIDEO|https://www.youtube.com/watch?v=mc979OhitAg",
            topicId = 19,
            title = "S.3 Physics: Electric Circuits (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=mc979OhitAg",
            source = "YouTube"
        ),
        TeachingResourceEntity(
            key = "20|NOTES|https://ncdc.go.ug/sites/default/files/S4_PHYSICS_PROTOTYPE.pdf",
            topicId = 20,
            title = "S.4 Physics: Atomic and Nuclear Physics (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S4_PHYSICS_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "16|NOTES|https://ncdc.go.ug/sites/default/files/S4_CHEMISTRY_PROTOTYPE.pdf",
            topicId = 16,
            title = "S.4 Chemistry: Organic Chemistry (PDF Guide)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S4_CHEMISTRY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "4|NOTES|https://ncdc.go.ug/sites/default/files/S4_BIOLOGY_PROTOTYPE.pdf",
            topicId = 4,
            title = "S.4 Biology: Ecology and environment (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S4_BIOLOGY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "8|NOTES|https://ncdc.go.ug/sites/default/files/S4_MATH_PROTOTYPE.pdf",
            topicId = 8,
            title = "S.4 Math: Calculus and Statistics (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S4_MATH_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),

        // --- CHEMISTRY ---
        TeachingResourceEntity(
            key = "13|NOTES|https://ncdc.go.ug/sites/default/files/S1_CHEMISTRY_PROTOTYPE.pdf",
            topicId = 13,
            title = "S.1 Chemistry: States of Matter (PDF Guide)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_CHEMISTRY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "13|VIDEO|https://www.youtube.com/watch?v=HAPc6JH85pM",
            topicId = 13,
            title = "S.1 Chemistry: States of Matter (Video Lesson)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=HAPc6JH85pM",
            source = "Khan Academy"
        ),
        TeachingResourceEntity(
            key = "14|VIDEO|https://www.youtube.com/watch?v=L2Q2q20EkeA",
            topicId = 14,
            title = "S.2 Chemistry: Chemical Bonding (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=L2Q2q20EkeA",
            source = "Khan Academy"
        ),
        TeachingResourceEntity(
            key = "14|NOTES|https://ncdc.go.ug/sites/default/files/S2_CHEMISTRY_PROTOTYPE.pdf",
            topicId = 14,
            title = "S.2 Chemistry: Chemical Bonding (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S2_CHEMISTRY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "15|NOTES|https://ncdc.go.ug/sites/default/files/S3_CHEMISTRY_PROTOTYPE.pdf",
            topicId = 15,
            title = "S.3 Chemistry: Acids, Bases and Salts (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S3_CHEMISTRY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "15|VIDEO|https://www.youtube.com/watch?v=ANi709MYn8E",
            topicId = 15,
            title = "S.3 Chemistry: Acids and Bases (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=ANi709MYn8E",
            source = "YouTube"
        ),

        // --- OTHER ---
        TeachingResourceEntity(
            key = "1|VIDEO|https://www.youtube.com/watch?v=vqxomJIBGcY",
            topicId = 1,
            title = "S.1 Biology: Classification (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=vqxomJIBGcY",
            source = "YouTube Education"
        ),
        TeachingResourceEntity(
            key = "5|NOTES|https://ncdc.go.ug/sites/default/files/S1_MATH_PROTOTYPE.pdf",
            topicId = 5,
            title = "S.1 Math: Sets and Venn Diagrams (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_MATH_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        // --- ADDED RESOURCES FOR OTHER SUBJECTS ---
        TeachingResourceEntity(
            key = "9|NOTES|https://ncdc.go.ug/sites/default/files/S1_ENGLISH_PROTOTYPE.pdf",
            topicId = 9,
            title = "S.1 English: Narrative Writing (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_ENGLISH_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "21|NOTES|https://ncdc.go.ug/sites/default/files/S1_HISTORY_PROTOTYPE.pdf",
            topicId = 21,
            title = "S.1 History: The Cradle of Mankind (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_HISTORY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "25|NOTES|https://ncdc.go.ug/sites/default/files/S1_GEOGRAPHY_PROTOTYPE.pdf",
            topicId = 25,
            title = "S.1 Geography: Physical Geography (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_GEOGRAPHY_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "33|NOTES|https://ncdc.go.ug/sites/default/files/S1_KISWAHILI_PROTOTYPE.pdf",
            topicId = 33,
            title = "S.1 Kiswahili: Lugha na Mawasiliano (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_KISWAHILI_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "37|NOTES|https://ncdc.go.ug/sites/default/files/S1_CRE_PROTOTYPE.pdf",
            topicId = 37,
            title = "S.1 R.E: God's Creation (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_CRE_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "41|NOTES|https://ncdc.go.ug/sites/default/files/S1_AGRIC_PROTOTYPE.pdf",
            topicId = 41,
            title = "S.1 Agriculture: Intro to Farming (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_AGRIC_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "45|VIDEO|https://www.youtube.com/watch?v=S_0mXbYJ8rE",
            topicId = 45,
            title = "S.1 ICT: Intro to Computers (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=S_0mXbYJ8rE",
            source = "YouTube"
        ),
        TeachingResourceEntity(
            key = "29|NOTES|https://ncdc.go.ug/sites/default/files/S1_ENTREPRENEURSHIP_PROTOTYPE.pdf",
            topicId = 29,
            title = "S.1 Entrepreneurship: Innovation (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_ENTREPRENEURSHIP_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "50|VIDEO|https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            topicId = 50,
            title = "S.1 Performing Arts: Music & Dance (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            source = "YouTube"
        ),
        TeachingResourceEntity(
            key = "51|NOTES|https://ncdc.go.ug/sites/default/files/S1_ART_DESIGN_PROTOTYPE.pdf",
            topicId = 51,
            title = "S.1 Art and Design: Drawing (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_ART_DESIGN_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "52|NOTES|https://ncdc.go.ug/sites/default/files/S1_FOOD_NUTRITION_PROTOTYPE.pdf",
            topicId = 52,
            title = "S.1 Nutrition: Safety & Hygiene (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_FOOD_NUTRITION_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "53|NOTES|https://ncdc.go.ug/sites/default/files/S1_TECH_DESIGN_PROTOTYPE.pdf",
            topicId = 53,
            title = "S.1 Technology: Technical Drawing (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_TECH_DESIGN_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "54|VIDEO|https://www.youtube.com/watch?v=7uK8_6E6L_M",
            topicId = 54,
            title = "S.1 Physical Education: Athletics (Video)",
            type = "VIDEO",
            url = "https://www.youtube.com/watch?v=7uK8_6E6L_M",
            source = "YouTube"
        ),
        TeachingResourceEntity(
            key = "55|NOTES|https://ncdc.go.ug/sites/default/files/S1_LOCAL_LANGUAGE_PROTOTYPE.pdf",
            topicId = 55,
            title = "S.1 Local Language: Grammar & Culture (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_LOCAL_LANGUAGE_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        ),
        TeachingResourceEntity(
            key = "56|NOTES|https://ncdc.go.ug/sites/default/files/S1_FOREIGN_LANGUAGE_PROTOTYPE.pdf",
            topicId = 56,
            title = "S.1 Foreign Languages: Basic Comm (PDF)",
            type = "NOTES",
            url = "https://ncdc.go.ug/sites/default/files/S1_FOREIGN_LANGUAGE_PROTOTYPE.pdf",
            source = "NCDC Uganda"
        )
    )

    val topics: List<TopicEntity> = listOf(
        // --- SCIENCES ---
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
