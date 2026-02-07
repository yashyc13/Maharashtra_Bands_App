package com.maharashtra.bands.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.data.model.Submission


class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    fun getApprovedBands(
        pagination: Pagination,
        onSuccess: (bands: List<Band>, lastDocument: DocumentSnapshot?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query = firestore.collection(COLLECTION_BANDS)
            .whereEqualTo(FIELD_IS_APPROVED, true)
            .orderBy(FIELD_NAME, Query.Direction.ASCENDING)
            .limit(pagination.limit)

        pagination.lastDocument?.let { lastSnapshot ->
            query = query.startAfter(lastSnapshot)
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                val bands = snapshot.documents.map { document ->
                    document.toObject(Band::class.java)?.copy(id = document.id)
                }.filterNotNull()
                val lastDocument = snapshot.documents.lastOrNull()
                onSuccess(bands, lastDocument)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun searchBandsByName(
        queryText: String,
        onSuccess: (List<Band>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (queryText.isBlank()) {
            onSuccess(emptyList())
            return
        }

        val query = firestore.collection(COLLECTION_BANDS)
            .whereEqualTo(FIELD_IS_APPROVED, true)
            .orderBy(FIELD_NAME)
            .startAt(queryText)
            .endAt("$queryText\uf8ff")

        query.get()
            .addOnSuccessListener { snapshot ->
                val bands = snapshot.documents.map { document ->
                    document.toObject(Band::class.java)?.copy(id = document.id)
                }.filterNotNull()
                onSuccess(bands)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun filterBandsByCityAndType(
        city: String,
        type: String,
        onSuccess: (List<Band>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query = firestore.collection(COLLECTION_BANDS)
            .whereEqualTo(FIELD_IS_APPROVED, true)

        if (city.isNotBlank()) {
            query = query.whereEqualTo(FIELD_CITY, city)
        }

        if (type.isNotBlank()) {
            query = query.whereEqualTo(FIELD_TYPE, type)
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                val bands = snapshot.documents.map { document ->
                    document.toObject(Band::class.java)?.copy(id = document.id)
                }.filterNotNull()
                onSuccess(bands)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun submitBand(
        submission: Submission,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection(COLLECTION_SUBMISSIONS)
            .add(submission)
            .addOnSuccessListener { documentReference ->
                onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun uploadSubmissionImage(
        fileUri: android.net.Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "submission_${System.currentTimeMillis()}.jpg"
        val reference = submissionsImageRef().child(fileName)
        reference.putFile(fileUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                reference.downloadUrl
            }
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun observeApprovedBands(
        queryText: String,
        city: String,
        type: String,
        onSuccess: (List<Band>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        /**
         * Example indexed queries:
         *  - Approved + name prefix:
         *      whereEqualTo("isApproved", true).orderBy("name").startAt("Mu").endAt("Mu\\uf8ff")
         *  - Approved + city + type:
         *      whereEqualTo("isApproved", true).whereEqualTo("city", "Pune").whereEqualTo("type", "Brass")
         *          .orderBy("name")
         */
        var query = firestore.collection(COLLECTION_BANDS)
            .whereEqualTo(FIELD_IS_APPROVED, true)

        if (city.isNotBlank()) {
            query = query.whereEqualTo(FIELD_CITY, city)
        }

        if (type.isNotBlank()) {
            query = query.whereEqualTo(FIELD_TYPE, type)
        }

        // Firestore index note:
        // Combining whereEqualTo filters with orderBy on name requires a composite index.
        query = query.orderBy(FIELD_NAME)

        if (queryText.isNotBlank()) {
            query = query.startAt(queryText).endAt("$queryText\uf8ff")
        }

        return query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onFailure(error)
                return@addSnapshotListener
            }
            val documents = snapshot?.documents.orEmpty()
            val bands = documents.mapNotNull { document ->
                document.toObject(Band::class.java)?.copy(id = document.id)
            }
            onSuccess(bands)
        }
    }

    companion object {
        private const val COLLECTION_BANDS = "bands"
        private const val COLLECTION_SUBMISSIONS = "submissions"
        private const val FIELD_NAME = "name"
        private const val FIELD_CITY = "city"
        private const val FIELD_TYPE = "type"
        private const val FIELD_IS_APPROVED = "isApproved"
    }

    private fun submissionsImageRef(): StorageReference {
        return storage.reference.child("submissions")
    }
}


data class Pagination(
    val limit: Long = 20,
    val lastDocument: DocumentSnapshot? = null
)
