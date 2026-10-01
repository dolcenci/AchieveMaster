package com.example.achievemaster.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.achievemaster.R
import com.example.achievemaster.data.model.Exercise
import com.example.achievemaster.ui.activities.ExerciseDetailActivity

class ExerciseAdapter(
    private val context: Context,
    private var exercises: List<Exercise>
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvMuscles: TextView = itemView.findViewById(R.id.tvMuscles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        holder.tvExerciseName.text = exercise.name.replaceFirstChar { it.uppercase() }
        holder.tvCategory.text = "Category: ${exercise.category}"
        holder.tvMuscles.text = "Muscle: ${exercise.muscle}"

        holder.cardView.setOnClickListener {
            try {
                val intent = Intent(context, ExerciseDetailActivity::class.java)
                // Put exercise data as individual extras instead of the whole object
                intent.putExtra("exercise_name", exercise.name)
                intent.putExtra("exercise_type", exercise.type)
                intent.putExtra("exercise_muscle", exercise.muscle)
                intent.putExtra("exercise_equipment", exercise.equipment)
                intent.putExtra("exercise_difficulty", exercise.difficulty)
                intent.putExtra("exercise_instructions", exercise.instructions)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = exercises.size

    fun updateData(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}