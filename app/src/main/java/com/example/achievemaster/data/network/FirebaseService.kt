package com.example.achievemaster.data.network

import android.util.Log
import com.example.achievemaster.data.model.FitnessGoal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = Firebase.database
    private val usersRef: DatabaseReference = database.getReference("users")
    private val goalsRef: DatabaseReference = database.getReference("goals")
    private val logsRef: DatabaseReference = database.getReference("exercise_logs")

    companion object {
        private const val TAG = "FirebaseService"
    }

    init {
        goalsRef.keepSynced(true)
    }

    suspend fun registerUser(
        email: String,
        password: String,
        name: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            user?.let {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                it.updateProfile(profileUpdates).await()

                val userData = mapOf(
                    "userId" to it.uid,
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )
                usersRef.child(it.uid).setValue(userData).await()
            }

            Result.success(user!!)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user

            user?.let {
                val userData = mapOf(
                    "userId" to it.uid,
                    "name" to (it.displayName ?: "User"),
                    "email" to (it.email ?: ""),
                    "createdAt" to System.currentTimeMillis()
                )
                usersRef.child(it.uid).setValue(userData).await()
            }

            Result.success(user!!)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed: ${e.message}")
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun getUserProfile(): Result<Map<String, Any>> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
            val snapshot = usersRef.child(user.uid).get().await()
            val data = snapshot.value as? Map<String, Any>
            if (data != null) Result.success(data)
            else Result.failure(Exception("Profile not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveGoal(goal: FitnessGoal): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
            val goalId = if (goal.id.isEmpty()) UUID.randomUUID().toString() else goal.id
            val goalData = goal.copy(id = goalId)
            goalsRef.child(user.uid).child(goalId).setValue(goalData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Save goal failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllGoals(): Result<List<FitnessGoal>> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
            val snapshot = goalsRef.child(user.uid).get().await()
            val goals = mutableListOf<FitnessGoal>()
            snapshot.children.forEach { child ->
                child.getValue(FitnessGoal::class.java)?.let { goals.add(it) }
            }
            Result.success(goals)
        } catch (e: Exception) {
            Log.e(TAG, "Get goals failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteGoal(goalId: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
            goalsRef.child(user.uid).child(goalId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun incrementCompletedExercises(muscle: String) {
        val user = auth.currentUser ?: return
        val normalised = muscle.trim().lowercase().replace(" ", "_")
        if (normalised.isEmpty()) return

        goalsRef.child(user.uid).get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                val goal = child.getValue(FitnessGoal::class.java) ?: continue
                val goalMuscle = goal.relatedMuscle.trim().lowercase().replace(" ", "_")
                if (goalMuscle != normalised) continue

                val newCount = (goal.completedExercises + 1).coerceAtMost(goal.targetExercises)
                val isCompleted = newCount >= goal.targetExercises

                child.ref.updateChildren(
                    mapOf(
                        "completedExercises" to newCount,
                        "isCompleted" to isCompleted
                    )
                )
            }
        }
    }

    fun logExerciseCompletion(
        exerciseName: String,
        muscle: String,
        type: String,
        difficulty: String
    ) {
        val user = auth.currentUser ?: return
        val logId = UUID.randomUUID().toString()
        val logData = mapOf(
            "exerciseName" to exerciseName,
            "muscle" to muscle,
            "type" to type,
            "difficulty" to difficulty,
            "completedAt" to System.currentTimeMillis()
        )
        logsRef.child(user.uid).child(logId).setValue(logData)
    }
}