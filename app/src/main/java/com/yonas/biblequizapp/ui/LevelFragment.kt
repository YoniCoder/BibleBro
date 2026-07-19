package com.yonas.biblequizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment  // ← THIS IS THE IMPORT YOU NEED
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yonas.biblequizapp.MainActivity
import com.yonas.biblequizapp.R
import com.yonas.biblequizapp.data.ProgressManager
import com.yonas.biblequizapp.data.QuestionBank

// ✅ This data class stays here
data class LevelItem(
    val level: Int,
    val isUnlocked: Boolean,
    val isPassed: Boolean,
    val score: Int
)

// ✅ Make sure this extends Fragment()
class LevelFragment : Fragment() {

    private lateinit var progressManager: ProgressManager
    private lateinit var section: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_levels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressManager = ProgressManager(requireContext())
        section = arguments?.getString("section") ?: "old"

        val title = if (section == "old") "ብሉይ ኪዳን" else "ሐዲስ ኪዳን"
        view.findViewById<android.widget.TextView>(R.id.sectionTitle).text = title

        val recyclerView = view.findViewById<RecyclerView>(R.id.levelsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val totalLevels = if (section == "old") QuestionBank.oldTestamentLevels.size else QuestionBank.newTestamentLevels.size

        // Build the level list
        val levelList = mutableListOf<LevelItem>()
        for (level in 1..totalLevels) {
            levelList.add(
                LevelItem(
                    level = level,
                    isUnlocked = progressManager.isLevelUnlocked(section, level),
                    isPassed = progressManager.isLevelPassed(section, level),
                    score = progressManager.getScore(section, level)
                )
            )
        }

        recyclerView.adapter = LevelAdapter(levelList) { level: Int ->
            val fragment = QuizFragment().apply {
                arguments = Bundle().apply {
                    putString("section", section)
                    putInt("level", level - 1)
                }
            }
            (activity as MainActivity).replaceFragment(fragment)
        }
    }

    // ===== LEVEL ADAPTER INNER CLASS =====
    class LevelAdapter(
        private val levels: List<LevelItem>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_level, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = levels[position]
            holder.levelNumber.text = item.level.toString()
            holder.levelName.text = "ደረጃ ${item.level}"

            val statusText = when {
                item.isPassed -> "✅ ${item.score}/10 አልፈዋል"
                !item.isUnlocked -> "🔒 ተቆልፏል"
                else -> "❌ አልተሳካም (${item.score}/10)"
            }
            holder.levelStatus.text = statusText
            holder.levelStatus.setTextColor(
                when {
                    item.isPassed -> holder.itemView.context.getColor(android.R.color.holo_green_dark)
                    !item.isUnlocked -> holder.itemView.context.getColor(android.R.color.darker_gray)
                    else -> holder.itemView.context.getColor(android.R.color.holo_red_dark)
                }
            )

            holder.levelIcon.setImageResource(
                when {
                    item.isPassed -> R.drawable.ic_check
                    else -> R.drawable.ic_lock
                }
            )

            holder.itemView.isEnabled = item.isUnlocked
            holder.itemView.alpha = if (item.isUnlocked) 1.0f else 0.6f

            holder.itemView.setOnClickListener {
                if (item.isUnlocked) onItemClick(item.level)
            }
        }

        override fun getItemCount(): Int = levels.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val levelNumber: android.widget.TextView = itemView.findViewById(R.id.levelNumber)
            val levelName: android.widget.TextView = itemView.findViewById(R.id.levelName)
            val levelStatus: android.widget.TextView = itemView.findViewById(R.id.levelStatus)
            val levelIcon: android.widget.ImageView = itemView.findViewById(R.id.levelIcon)
        }
    }
}