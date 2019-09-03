package com.dso30bt.project2019.potapp.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dso30bt.project2019.potapp.interfaces.IFirebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Created by Joesta on 2019/08/26.
 */
public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private Context context;

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public void incrementId(final String updateField, String collection, String document) {

        DocumentReference docRef = getDocumentReference(collection, document);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction(transaction -> {

            DocumentSnapshot snapshot = transaction.get(docRef);

            int updatedFieldValue = Integer.parseInt(snapshot.get(updateField).toString()) + 1;
            transaction.update(docRef, updateField, updatedFieldValue);


            return null;

        }).addOnFailureListener(((Activity) context), e -> Log.e(TAG, e.getLocalizedMessage()));
    }

    public void incrementId(IFirebase.DocumentFieldCallback callback, final String updateField, String collection, String document) {
        Log.i(TAG, "incrementId: starting field increment in overloaded method");
        DocumentReference docRef = getDocumentReference(collection, document);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction(transaction -> {

            DocumentSnapshot snapshot = transaction.get(docRef);

            int updatedFieldValue = Integer.parseInt(snapshot.get(updateField).toString()) + 1;
            transaction.update(docRef, updateField, updatedFieldValue);

            callback.onFieldUpdate(updatedFieldValue);
            Log.i(TAG, "incrementId: field increment completed in overloaded method");
            return null;

        }).addOnFailureListener(((Activity) context), e -> Log.e(TAG, e.getLocalizedMessage()));
    }

    private DocumentReference getDocumentReference(String fromCollection, String... optionalDocument) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return
                (optionalDocument.length > 0) ?
                        db.collection(fromCollection).document(optionalDocument[0]) : db.collection(fromCollection).document();
    }

}
