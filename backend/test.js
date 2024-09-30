const Lesson = require('./models/lesson');
const {
  TrueFalseQuestion,
  MultipleChoiceQuestion,
  MatchingQuestion,
  SequenceQuestion,
  SpellingCorrectionQuestion
} = require('./models/question');
const lessons = [
    {
        title: "Gramatika - Lekcija 1",
        description: "Osnove gramatike: Imenice, pridevi i glagoli.",
        questions: [
            new TrueFalseQuestion({
                question: "Glagoli označavaju radnju.",
                correctAnswer: true,
                type: "gramatika",
                position: 1
            }),
            new MultipleChoiceQuestion({
                question: "Koja od sledećih reči je imenica?",
                options: ["trčati", "pas", "lep", "veliki"],
                correctAnswer: "pas",
                type: "gramatika",
                position: 2
            }),
            new MatchingQuestion({
                question: "Upari imenice sa odgovarajućim pridevima:",
                matchingPairs: [
                    { left: "pas", right: "veliki" },
                    { left: "mačka", right: "mala" }
                ],
                type: "gramatika",
                position: 3
            }),
            new SequenceQuestion({
                question: "Poređaj reči po abecednom redu:",
                sequence: ["pas", "mačka", "riba"],
                type: "gramatika",
                position: 4
            }),
            new SpellingCorrectionQuestion({
                question: "Ispravi grešku u reči:",
                incorrectSpelling: "knjiga",
                correctedSpelling: "knjiga",
                type: "gramatika",
                position: 5
            })
        ]
    },
    {
        title: "Pravopis - Lekcija 1",
        description: "Pravila pisanja velikog početnog slova.",
        questions: [
            new TrueFalseQuestion({
                question: "Imena država se pišu velikim početnim slovom.",
                correctAnswer: true,
                type: "pravopis",
                position: 1
            }),
            new MultipleChoiceQuestion({
                question: "Koja rečenica je pravilno napisana?",
                options: ["Ja idem u Beograd.", "ja idem u beograd.", "Ja Idem U Beograd."],
                correctAnswer: "Ja idem u Beograd.",
                type: "pravopis",
                position: 2
            }),
            new SpellingCorrectionQuestion({
                question: "Ispravi grešku u reči:",
                incorrectSpelling: "srbija",
                correctedSpelling: "Srbija",
                type: "pravopis",
                position: 3
            })
        ]
    },
    {
        title: "Književnost - Lekcija 1",
        description: "Uvod u srpsku književnost: Narodna književnost.",
        questions: [
            new MultipleChoiceQuestion({
                question: "Koji od sledećih pisaca je poznat po narodnim pesmama?",
                options: ["Vuk Karadžić", "Ivo Andrić", "Branko Radičević"],
                correctAnswer: "Vuk Karadžić",
                type: "knjizevnost",
                position: 1
            }),
            new TrueFalseQuestion({
                question: "Narodne pesme su se prenosile usmenim putem.",
                correctAnswer: true,
                type: "knjizevnost",
                position: 2
            }),
            new SequenceQuestion({
                question: "Poređaj pisce po hronološkom redu:",
                sequence: ["Vuk Karadžić", "Ivo Andrić", "Branko Radičević"],
                type: "knjizevnost",
                position: 3
            })
        ]
    }
];

// Sačuvaj lekcije u bazi
lessons.forEach(async (lesson) => {
    try {
        const newLesson = new Lesson(lesson);
        await newLesson.save();
        console.log(`Lekcija "${lesson.title}" uspešno sačuvana.`);
    } catch (error) {
        console.error(`Greška pri čuvanju lekcije "${lesson.title}":`, error);
    }
});
