package com.yonas.biblequizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yonas.biblequizapp.R
import com.yonas.biblequizapp.data.ProgressManager
import com.yonas.biblequizapp.data.QuestionBank
import com.yonas.biblequizapp.data.Question

class QuizFragment : Fragment() {

    private lateinit var progressManager: ProgressManager
    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var score = 0
    private var answered = false
    private lateinit var section: String
    private lateinit var level: String
    private var levelIndex = 0

    // UI Elements
    private lateinit var questionText: TextView
    private lateinit var progressText: TextView
    private lateinit var scoreText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var optionsContainer: LinearLayout
    private lateinit var resultContainer: LinearLayout
    private lateinit var resultEmoji: TextView
    private lateinit var resultScore: TextView
    private lateinit var resultMessage: TextView
    private lateinit var resultUnlockMessage: TextView
    private lateinit var retryButton: Button
    private lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressManager = ProgressManager(requireContext())
        section = arguments?.getString("section") ?: "old"
        levelIndex = arguments?.getInt("level") ?: 0
        level = levelIndex.toString()

        // Get the full pool of questions for this level (20 questions)
        val fullPool = if (section == "old") {
            QuestionBank.oldTestamentLevels[levelIndex]
        } else {
            QuestionBank.newTestamentLevels[levelIndex]
        }

        // Shuffle: pick 10 random questions, and shuffle their options
        questions = QuestionBank.getRandomQuestionsForLevel(fullPool, 10)

        // Init UI
        questionText = view.findViewById(R.id.questionText)
        progressText = view.findViewById(R.id.progressText)
        scoreText = view.findViewById(R.id.scoreText)
        progressBar = view.findViewById(R.id.progressBar)
        optionsContainer = view.findViewById(R.id.optionsContainer)
        resultContainer = view.findViewById(R.id.resultContainer)
        resultEmoji = view.findViewById(R.id.resultEmoji)
        resultScore = view.findViewById(R.id.resultScore)
        resultMessage = view.findViewById(R.id.resultMessage)
        resultUnlockMessage = view.findViewById(R.id.resultUnlockMessage)
        retryButton = view.findViewById(R.id.retryButton)
        nextButton = view.findViewById(R.id.nextButton)

        retryButton.setOnClickListener { retryQuiz() }
        nextButton.setOnClickListener { goToNextLevel() }

        loadQuestion()
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) {
            showResult()
            return
        }

        answered = false
        val q = questions[currentQuestionIndex]
        questionText.text = q.question
        progressText.text = "${currentQuestionIndex + 1}/${questions.size}"
        progressBar.progress = (currentQuestionIndex + 1) * 100 / questions.size
        scoreText.text = "✅ $score"

        optionsContainer.removeAllViews()
        q.options.forEachIndexed { index, option ->
            val btn = Button(requireContext()).apply {
                text = option
                setBackgroundResource(R.drawable.bg_option)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setOnClickListener { selectOption(index, q.correctAnswerIndex) }
                typeface = resources.getFont(R.font.shiromeda_regular)
                textSize = 16f
                setPadding(20, 16, 20, 16)
            }
            optionsContainer.addView(btn)
        }

        resultContainer.visibility = View.GONE
    }

    private fun selectOption(selected: Int, correct: Int) {
        if (answered) return
        answered = true

        val childCount = optionsContainer.childCount
        for (i in 0 until childCount) {
            val btn = optionsContainer.getChildAt(i) as Button
            btn.isEnabled = false
            if (i == correct) {
                btn.setBackgroundResource(R.drawable.bg_option_correct)
                btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else if (i == selected && selected != correct) {
                btn.setBackgroundResource(R.drawable.bg_option_wrong)
                btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
        }

        if (selected == correct) score++

        optionsContainer.postDelayed({
            currentQuestionIndex++
            loadQuestion()
        }, 1200)
    }

    private fun showResult() {
        val total = questions.size
        val percent = (score * 100) / total

        resultContainer.visibility = View.VISIBLE
        optionsContainer.visibility = View.GONE
        questionText.visibility = View.GONE
        progressText.visibility = View.GONE
        progressBar.visibility = View.GONE

        val emoji = when {
            percent >= 90 -> "🎉"
            percent >= 70 -> "👏"
            percent >= 50 -> "💪"
            else -> "😢"
        }
        val message = when {
            percent >= 90 -> "እጅግ ጥሩ ስራ!"
            percent >= 70 -> "ጥሩ ስራ!"
            percent >= 50 -> "ይቻላል!"
            else -> "ደግሞ ሞክር!"
        }

        resultEmoji.text = emoji
        resultScore.text = "$score/$total"
        resultMessage.text = message

        // Save progress (using levelIndex+1 for 1-based storage)
        progressManager.saveScore(section, levelIndex + 1, score)

        val passed = score >= 6
        val levelNum = levelIndex + 1
        val nextLevelUnlocked = passed && levelNum < 5

        if (passed) {
            if (nextLevelUnlocked) {
                resultUnlockMessage.text = "✅ ደረጃ ${levelNum + 1} ተከፍቷል!"
                resultUnlockMessage.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE
                retryButton.text = "ደግሞ ሞክር"
            } else if (levelNum == 5) {
                resultUnlockMessage.text = "🎊 ሁሉንም ደረጃዎች አጠናቀዋል!"
                resultUnlockMessage.visibility = View.VISIBLE
                nextButton.visibility = View.GONE
                retryButton.text = "ወደ መነሻ"
            } else {
                resultUnlockMessage.visibility = View.GONE
                nextButton.visibility = View.GONE
                retryButton.text = "ወደ መነሻ"
            }
        } else {
            resultUnlockMessage.visibility = View.GONE
            nextButton.visibility = View.GONE
            retryButton.text = "ደግሞ ሞክር"
        }
    }

    private fun retryQuiz() {
        // If failed, re-shuffle a new set of 10 questions from the pool
        val fullPool = if (section == "old") {
            QuestionBank.oldTestamentLevels[levelIndex]
        } else {
            QuestionBank.newTestamentLevels[levelIndex]
        }
        questions = QuestionBank.getRandomQuestionsForLevel(fullPool, 10)

        currentQuestionIndex = 0
        score = 0
        resultContainer.visibility = View.GONE
        optionsContainer.visibility = View.VISIBLE
        questionText.visibility = View.VISIBLE
        progressText.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        loadQuestion()
    }

    private fun goToNextLevel() {
        val nextLevel = levelIndex + 1
        if (nextLevel < 5) {
            // Create a new instance of QuizFragment with the next level
            val fragment = QuizFragment().apply {
                arguments = Bundle().apply {
                    putString("section", section)
                    putInt("level", nextLevel)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            // All levels done – go back
            parentFragmentManager.popBackStack()
        }
    }
}