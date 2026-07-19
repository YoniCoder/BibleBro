package com.yonas.biblequizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yonas.biblequizapp.R
import com.yonas.biblequizapp.data.ProgressManager

class ProgressFragment : Fragment() {

    private lateinit var progressManager: ProgressManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressManager = ProgressManager(requireContext())

        val oldPassed = progressManager.getTotalPassed("old")
        val newPassed = progressManager.getTotalPassed("new")
        val totalPassed = oldPassed + newPassed

        view.findViewById<TextView>(R.id.overallProgress).text = "$totalPassed/10 ደረጃዎች አልፈዋል"

        val recyclerView = view.findViewById<RecyclerView>(R.id.progressRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val allLevels = mutableListOf<ProgressItem>()

        // Old Testament levels
        for (i in 1..5) {
            val passed = progressManager.isLevelPassed("old", i)
            val score = progressManager.getScore("old", i)
            val status = if (passed) "✅ አልፈዋል" else if (score > 0) "❌ ${score}/10" else "🔒"
            allLevels.add(ProgressItem("ብሉይ ኪዳን - ደረጃ $i", "$score/10", status))
        }

        // New Testament levels
        for (i in 1..5) {
            val passed = progressManager.isLevelPassed("new", i)
            val score = progressManager.getScore("new", i)
            val status = if (passed) "✅ አልፈዋል" else if (score > 0) "❌ ${score}/10" else "🔒"
            allLevels.add(ProgressItem("ሐዲስ ኪዳን - ደረጃ $i", "$score/10", status))
        }

        recyclerView.adapter = ProgressAdapter(allLevels)
    }

    data class ProgressItem(
        val name: String,
        val score: String,
        val status: String
    )

    class ProgressAdapter(
        private val items: List<ProgressItem>
    ) : RecyclerView.Adapter<ProgressAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_progress_level, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.score.text = item.score
            holder.status.text = when {
                item.status.contains("✅") -> "✅"
                item.status.contains("🔒") -> "🔒"
                else -> "❌"
            }
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.progressLevelName)
            val score: TextView = itemView.findViewById(R.id.progressLevelScore)
            val status: TextView = itemView.findViewById(R.id.progressLevelStatus)
        }
    }
}