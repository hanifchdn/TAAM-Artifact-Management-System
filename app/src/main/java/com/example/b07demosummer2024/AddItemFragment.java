package com.example.b07demosummer2024;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemFragment extends Fragment {

    private EditText editTextLot;
    private EditText editTextArtifactName;
    private EditText editTextDescription;
    private EditText editTextCulturalOrigin;
    private EditText editTextHeight;
    private EditText editTextWidth;
    private EditText editTextDepth;
    private EditText editTextConditionReport;
    private EditText editTextCurrentLocation;
    private EditText editTextAcquisitionMethod;
    private EditText editTextProvenance;
    private EditText editTextAccessionNumber;
    private EditText editTextNotes;
    private Uri imageUri;
    private ImageView selectedImagePreview;
    private SupabaseImageUploader imageUploader;
    private ActivityResultLauncher<String> imageSelectionLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            selectedImagePreview.setImageURI(uri);
                            imageUploader = new SupabaseImageUploader(getContext());

                            //TODO: Move this example call to where the add button call will be at.
                            ImageDatabaseWriter imageDatabaseWriter = new ImageDatabaseWriter();
                            imageDatabaseWriter.addToDatabase(imageUploader, uri, "test2", (url) -> {
                                if (url == null) {
                                    Log.d("tst", "oop");
                                }
                                else {
                                    Log.d("tst", url);
                                }
                            });
                        }
                    }
            );


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindTextFields(view);
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> imageSelectionLauncher.launch("image/*"));
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);
        Button buttonAddArtifact = view.findViewById(R.id.buttonAddItem);
    }

    private void bindTextFields(@NonNull View view) {
        editTextLot = view.findViewById(R.id.editTextLot);
        editTextArtifactName = view.findViewById(R.id.editTextArtifactName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextCulturalOrigin = view.findViewById(R.id.editTextCulturalOrigin);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        editTextWidth = view.findViewById(R.id.editTextWidth);
        editTextDepth = view.findViewById(R.id.editTextDepth);
        editTextConditionReport = view.findViewById(R.id.editTextConditionReport);
        editTextCurrentLocation = view.findViewById(R.id.editTextCurrentLocation);
        editTextAcquisitionMethod = view.findViewById(R.id.editTextAcquisitionMethod);
        editTextProvenance = view.findViewById(R.id.editTextProvenance);
        editTextAccessionNumber = view.findViewById(R.id.editTextAccessionNumber);
        editTextNotes = view.findViewById(R.id.editTextNotes);
    }

    public EditText getEditTextLot() {
        return editTextLot;
    }

    public EditText getEditTextArtifactName() {
        return editTextArtifactName;
    }

    public EditText getEditTextDescription() {
        return editTextDescription;
    }

    public EditText getEditTextCulturalOrigin() {
        return editTextCulturalOrigin;
    }

    public EditText getEditTextAcquisitionMethod() {
        return editTextAcquisitionMethod;
    }
    public EditText getEditTextHeight() {
        return editTextHeight;
    }

    public EditText getEditTextWidth() {
        return editTextWidth;
    }

    public EditText getEditTextDepth() {
        return editTextDepth;
    }

    public EditText getEditTextConditionReport() {
        return editTextConditionReport;
    }

    public EditText getEditTextCurrentLocation() {
        return editTextCurrentLocation;
    }

    public EditText getEditTextProvenance() {
        return editTextProvenance;
    }

    public EditText getEditTextAccessionNumber() {
        return editTextAccessionNumber;
    }

    public EditText getEditTextNotes() {
        return editTextNotes;
    }

    public Uri getSelectedImageUri() {
        return imageUri;
    }

    public SupabaseImageUploader getSupabaseImageUploader() {return imageUploader;}

}

