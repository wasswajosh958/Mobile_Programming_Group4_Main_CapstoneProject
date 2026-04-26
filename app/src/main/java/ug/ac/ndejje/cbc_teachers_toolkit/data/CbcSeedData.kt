package ug.ac.ndejje.cbc_teachers_toolkit.data

import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity

object CbcSeedData {
    val topics: List<TopicEntity> = listOf(
        TopicEntity(
            id = 1,
            title = "Classification of Living Things",
            subject = "Biology",
            classLevel = "S1",
            lessonPlan = "Guide learners to classify organisms using observable characteristics.",
            projectIdeas = "Create a chart showing local organisms grouped by kingdom.",
            assessmentRubric = "Accuracy of classification, clarity of explanations, teamwork.",
            teachingTips = "Use real examples from the school compound for engagement."
        ),
        TopicEntity(
            id = 2,
            title = "Photosynthesis",
            subject = "Biology",
            classLevel = "S2",
            lessonPlan = "Learners explain how plants make food and factors affecting the process.",
            projectIdeas = "Investigate effects of light by comparing covered and exposed leaves.",
            assessmentRubric = "Scientific method, observation quality, and conclusion quality.",
            teachingTips = "Encourage hypothesis writing before practical activities."
        ),
        TopicEntity(
            id = 3,
            title = "Set Theory and Venn Diagrams",
            subject = "Mathematics",
            classLevel = "S1",
            lessonPlan = "Teach set notation, subsets, and operations through guided examples.",
            projectIdeas = "Collect class data and represent using Venn diagrams.",
            assessmentRubric = "Correct notation, diagram accuracy, interpretation.",
            teachingTips = "Start with relatable categories such as sports and clubs."
        ),
        TopicEntity(
            id = 4,
            title = "Linear Equations",
            subject = "Mathematics",
            classLevel = "S2",
            lessonPlan = "Solve one-variable equations and connect to word problems.",
            projectIdeas = "Design market-based budgeting problems solved with equations.",
            assessmentRubric = "Procedure correctness, final answer, application to context.",
            teachingTips = "Use think-pair-share for multi-step solutions."
        ),
        TopicEntity(
            id = 5,
            title = "Grammar and Sentence Structure",
            subject = "English",
            classLevel = "S1",
            lessonPlan = "Develop sentence construction skills using guided writing tasks.",
            projectIdeas = "Create peer-reviewed paragraph writing portfolios.",
            assessmentRubric = "Grammar accuracy, coherence, punctuation.",
            teachingTips = "Use short peer feedback cycles for quick improvement."
        ),
        TopicEntity(
            id = 6,
            title = "Composition Writing",
            subject = "English",
            classLevel = "S2",
            lessonPlan = "Build composition structure: introduction, body, and conclusion.",
            projectIdeas = "Write local community stories and present orally.",
            assessmentRubric = "Organization, creativity, language use.",
            teachingTips = "Provide model compositions before independent writing."
        ),
        TopicEntity(
            id = 7,
            title = "Chemical Bonding",
            subject = "Chemistry",
            classLevel = "S2",
            lessonPlan = "Differentiate ionic and covalent bonds with particle models.",
            projectIdeas = "Build bonding models from low-cost local materials.",
            assessmentRubric = "Concept accuracy, model quality, explanation depth.",
            teachingTips = "Use visual aids to reduce abstractness."
        ),
        TopicEntity(
            id = 8,
            title = "Force and Motion",
            subject = "Physics",
            classLevel = "S1",
            lessonPlan = "Introduce balanced/unbalanced forces and motion graphs.",
            projectIdeas = "Measure toy car motion on different surfaces.",
            assessmentRubric = "Data capture, graphing, interpretation.",
            teachingTips = "Use short demonstrations before formula introduction."
        ),
        TopicEntity(
            id = 9,
            title = "Early Man in East Africa",
            subject = "History & Political Education",
            classLevel = "S1",
            lessonPlan = "Explore early communities and migration patterns in East Africa.",
            projectIdeas = "Timeline poster of major early historical developments.",
            assessmentRubric = "Historical accuracy, sequencing, collaboration.",
            teachingTips = "Encourage local oral history examples."
        ),
        TopicEntity(
            id = 10,
            title = "Map Reading",
            subject = "Geography",
            classLevel = "S1",
            lessonPlan = "Interpret map symbols, scale, and direction for local maps.",
            projectIdeas = "Draw a simple map of school and surroundings.",
            assessmentRubric = "Symbol usage, scale estimate, orientation.",
            teachingTips = "Use local area sketches before national maps."
        ),
        TopicEntity(
            id = 11,
            title = "Business Ideas and Opportunities",
            subject = "Entrepreneurship",
            classLevel = "S1",
            lessonPlan = "Identify problems in the community and propose business ideas.",
            projectIdeas = "Pitch a micro business idea for school environment.",
            assessmentRubric = "Feasibility, innovation, clarity of pitch.",
            teachingTips = "Use local examples from markets and kiosks."
        ),
        TopicEntity(
            id = 12,
            title = "Crop Production Basics",
            subject = "Agriculture",
            classLevel = "S1",
            lessonPlan = "Introduce seed selection, planting, and crop management.",
            projectIdeas = "Set up a small demonstration garden plot.",
            assessmentRubric = "Planning, crop care, reflection.",
            teachingTips = "Link content to seasonal local farming practices."
        ),
        TopicEntity(
            id = 13,
            title = "Computer Hardware Fundamentals",
            subject = "ICT",
            classLevel = "S1",
            lessonPlan = "Identify computer parts and explain their functions.",
            projectIdeas = "Label hardware components from available lab devices.",
            assessmentRubric = "Correct identification, function explanation.",
            teachingTips = "Use hands-on rotation in small groups."
        ),
        TopicEntity(
            id = 14,
            title = "Kiswahili Communication Basics",
            subject = "Kiswahili",
            classLevel = "S1",
            lessonPlan = "Build common classroom greetings and short conversations.",
            projectIdeas = "Role-play market and school communication scenes.",
            assessmentRubric = "Pronunciation, vocabulary use, confidence.",
            teachingTips = "Promote pair speaking practice."
        ),
        TopicEntity(
            id = 15,
            title = "Religious Values and Ethics",
            subject = "Religious Education",
            classLevel = "S1",
            lessonPlan = "Discuss values, ethics, and responsible behavior in community life.",
            projectIdeas = "Group discussion report on values in school leadership.",
            assessmentRubric = "Participation, reflection quality, relevance.",
            teachingTips = "Use real school-life scenarios to anchor discussion."
        )
    )
}
