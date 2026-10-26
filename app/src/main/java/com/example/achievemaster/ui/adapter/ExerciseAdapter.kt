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
        val tvExerciseEmoji: TextView = itemView.findViewById(R.id.tvExerciseEmoji)
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

        holder.tvExerciseEmoji.text = getEmojiForExercise(exercise)
        holder.tvExerciseName.text = exercise.name.replaceFirstChar { it.uppercase() }
        holder.tvCategory.text = exercise.category
        holder.tvMuscles.text = "Muscle: ${exercise.muscle.replaceFirstChar { it.uppercase() }}"

        holder.cardView.setOnClickListener {
            try {
                val intent = Intent(context, ExerciseDetailActivity::class.java)
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

    /**
     * Returns a matching emoji for an exercise based on its category,
     * muscle group, and name. Falls back to a generic dumbbell emoji.
     * All string access is null-safe so the adapter never crashes on
     * partially-populated API responses.
     */
    private fun getEmojiForExercise(exercise: Exercise): String {
        val name = (exercise.name ?: "").lowercase()
        val muscle = (exercise.muscle ?: "").lowercase()
        val category = (exercise.category ?: "").lowercase()
        val equipment = (exercise.equipment ?: "").lowercase()

        return when {
            name.contains("run") || name.contains("jog") -> "🏃"
            name.contains("walk") -> "🚶"
            name.contains("jump") -> "🤸"
            name.contains("squat") -> "🦵"
            name.contains("deadlift") -> "🏋️"
            name.contains("bench") || name.contains("press") -> "💪"
            name.contains("pull") || name.contains("row") -> "🚣"
            name.contains("curl") -> "💪"
            name.contains("plank") || name.contains("crunch") || name.contains("sit-up") -> "🧎"
            name.contains("push-up") || name.contains("pushup") -> "💪"
            name.contains("yoga") || name.contains("stretch") -> "🧘"
            name.contains("cycle") || name.contains("bike") -> "🚴"
            name.contains("swim") -> "🏊"
            name.contains("box") -> "🥊"

            category.contains("cardio") -> "🏃"
            category.contains("flexibility") -> "🧘"

            muscle.contains("chest") -> "💪"
            muscle.contains("back") -> "🏋️"
            muscle.contains("leg") -> "🦵"
            muscle.contains("shoulder") -> "🤸"
            muscle.contains("bicep") -> "💪"
            muscle.contains("tricep") -> "💪"
            muscle.contains("abs") || muscle.contains("core") -> "🧎"
            muscle.contains("glute") -> "🦵"
            muscle.contains("calf") -> "🦶"

            equipment.contains("bodyweight") || equipment.isEmpty() -> "🤸"
            equipment.contains("dumbbell") || equipment.contains("barbell") -> "🏋️"
            equipment.contains("kettlebell") -> "🏋️"
            equipment.contains("band") -> "🎗️"
            equipment.contains("machine") -> "⚙️"

            else -> "🏋️"
        }
    }
}