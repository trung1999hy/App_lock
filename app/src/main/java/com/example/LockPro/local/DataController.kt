package com.example.LockPro.local

import android.util.Log
import com.example.LockPro.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class   DataController(uid: String) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val reference: DocumentReference
    private val uid: String
    private var onListenerFirebase: OnListenerFirebase? = null

    init {
        Log.e("DataController", "DataController $uid")
        this.uid = uid
        reference = db.document("$NAME_COLLECTION/$uid")
    }

    fun writeNewUser(uid: String, coin: Int) {
        val user = User(uid, coin)
        val postValues: Map<String, Any> = user.toMap()
        reference.set(postValues).addOnSuccessListener {
            Log.e(
                "DataController",
                "DocumentSnapshot added with ID: "
            )
        }.addOnFailureListener { e -> Log.e("DataController", "onFailure " + e.message) }
    }

    val user: Unit
        get() {
            val user: Array<User> = arrayOf<User>(User())
            val docRef: DocumentReference = db.collection(NAME_COLLECTION).document(uid)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result
                    if (document.exists()) {
//                        user[0] = document.toObject(User::class.java)!!
                        onListenerFirebase?.onCompleteGetUser(document.toObject(User::class.java))
                        Log.d("DataController", "DocumentSnapshot data: " + document.data)
                    } else {
                        onListenerFirebase?.onCompleteGetUser(null)
                    }
                } else {
                    onListenerFirebase?.onFailure()
                    Log.d("DataController", "get failed with ", task.exception)
                }
            }
        }

    fun updateDocument(coin: Int) {
        val washingtonRef: DocumentReference = db.collection(NAME_COLLECTION).document(uid)
        washingtonRef
            .update("coin", coin)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                onListenerFirebase?.onSuccess()
            }
            .addOnFailureListener { e ->
                onListenerFirebase?.onFailure()
                Log.w(TAG, "Error updating document", e)
            }
    }

    fun setOnListenerFirebase(onListenerFirebase: OnListenerFirebase?) {
        this.onListenerFirebase = onListenerFirebase
    }

    interface OnListenerFirebase {
        fun onCompleteGetUser(user: User?)
        fun onSuccess()
        fun onFailure()
    }

    companion object {
        private const val NAME_COLLECTION = "user"
        private const val TAG = "DataController"
    }
}