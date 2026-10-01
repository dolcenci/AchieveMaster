package com.example.achievemaster.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.achievemaster.R
import com.example.achievemaster.data.model.FitnessGoal

class GoalAdapter(
    private var goals: List<FitnessGoal>,
    private val onDeleteClick: (FitnessGoal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvGoalTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvGoalDescription)
        val tvMuscle: TextView = itemView.findViewById(R.id.tvGoalMuscle)
        val tvProgress: TextView = itemView.findViewById(R.id.tvGoalProgress)
        val tvCount: TextView = itemView.findViewById(R.id.tvGoalCount)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressGoal)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteGoal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]

        holder.tvTitle.text = goal.title
        holder.tvDescription.text = goal.description.ifEmpty { "No description" }
        holder.tvMuscle.text = goal.relatedMuscle.replaceFirstChar { it.uppercase() }
        holder.progressBar.progress = goal.progressPercent
        holder.tvProgress.text = "${goal.progressPercent}%"
        holder.tvCount.text = "${goal.completedExercises} / ${goal.targetExercises} exercises"

        holder.btnDelete.setOnClickListener { onDeleteClick(goal) }
    }

    override fun getItemCount() = goals.size

    fun updateData(newGoals: List<FitnessGoal>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}