package ug.ac.ndejje.cbc_teachers_toolkit.data

import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity

object CbcSeedData {
    val topics: List<TopicEntity> = listOf(
        buildTopic(1, "Biology", "S1", "Classification of Living Things"),
        buildTopic(2, "Biology", "S2", "Nutrition in Plants and Animals"),
        buildTopic(3, "Biology", "S3", "Human Reproductive System"),
        buildTopic(4, "Biology", "S4", "Ecology and Environmental Conservation"),

        buildTopic(5, "Mathematics", "S1", "Sets and Venn Diagrams"),
        buildTopic(6, "Mathematics", "S2", "Linear Equations"),
        buildTopic(7, "Mathematics", "S3", "Quadratic Expressions"),
        buildTopic(8, "Mathematics", "S4", "Statistics and Probability"),

        buildTopic(9, "English", "S1", "Grammar and Sentence Structure"),
        buildTopic(10, "English", "S2", "Composition Writing"),
        buildTopic(11, "English", "S3", "Poetry Interpretation"),
        buildTopic(12, "English", "S4", "Oral Communication Skills"),

        buildTopic(13, "Chemistry", "S1", "States of Matter"),
        buildTopic(14, "Chemistry", "S2", "Chemical Bonding"),
        buildTopic(15, "Chemistry", "S3", "Acids, Bases and Salts"),
        buildTopic(16, "Chemistry", "S4", "Organic Chemistry Basics"),

        buildTopic(17, "Physics", "S1", "Force and Motion"),
        buildTopic(18, "Physics", "S2", "Work, Energy and Power"),
        buildTopic(19, "Physics", "S3", "Electricity and Circuits"),
        buildTopic(20, "Physics", "S4", "Waves, Light and Sound"),

        buildTopic(21, "History & Political Education", "S1", "Early Communities in East Africa"),
        buildTopic(22, "History & Political Education", "S2", "Colonial Rule in Uganda"),
        buildTopic(23, "History & Political Education", "S3", "Nationalism and Independence"),
        buildTopic(24, "History & Political Education", "S4", "Constitutional and Civic Governance"),

        buildTopic(25, "Geography", "S1", "Map Work Fundamentals"),
        buildTopic(26, "Geography", "S2", "Weather and Climate"),
        buildTopic(27, "Geography", "S3", "Population and Settlement"),
        buildTopic(28, "Geography", "S4", "Natural Resources and Development"),

        buildTopic(29, "Entrepreneurship", "S1", "Business Ideas and Opportunities"),
        buildTopic(30, "Entrepreneurship", "S2", "Starting and Managing a Small Business"),
        buildTopic(31, "Entrepreneurship", "S3", "Marketing and Customer Care"),
        buildTopic(32, "Entrepreneurship", "S4", "Financial Literacy and Record Keeping"),

        buildTopic(33, "Kiswahili", "S1", "Greetings and Basic Conversation"),
        buildTopic(34, "Kiswahili", "S2", "Reading and Listening Comprehension"),
        buildTopic(35, "Kiswahili", "S3", "Insha Writing"),
        buildTopic(36, "Kiswahili", "S4", "Public Speaking and Debate"),

        buildTopic(37, "Religious Education", "S1", "Values and Moral Formation"),
        buildTopic(38, "Religious Education", "S2", "Sacred Texts and Interpretation"),
        buildTopic(39, "Religious Education", "S3", "Faith, Leadership and Society"),
        buildTopic(40, "Religious Education", "S4", "Ethics, Peace and Social Justice"),

        buildTopic(41, "Agriculture", "S1", "Crop Production Basics"),
        buildTopic(42, "Agriculture", "S2", "Soil Fertility and Conservation"),
        buildTopic(43, "Agriculture", "S3", "Animal Husbandry"),
        buildTopic(44, "Agriculture", "S4", "Agribusiness and Farm Management"),

        buildTopic(45, "ICT", "S1", "Computer Fundamentals"),
        buildTopic(46, "ICT", "S2", "Word Processing and Spreadsheets"),
        buildTopic(47, "ICT", "S3", "Internet Safety and Research Skills"),
        buildTopic(48, "ICT", "S4", "Introduction to Programming")
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
