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
    private Spinner spinnerCategory;
    private Spinner spinnerMaterial;
    private Spinner spinnerDynasty;

    private Uri imageUri;
    private ImageView selectedImagePreview;
    private SupabaseImageUploader imageUploader;
    private ActivityResultLauncher<String> imageSelectionLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            selectedImagePreview.setImageURI(uri);
                            imageUploader = new SupabaseImageUploader(getContext());
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
        bindSpinners(view);
        configureCategorySpinner();
        configureMaterialSpinner();
        configureDynastySpinner();
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> imageSelectionLauncher.launch("image/*"));
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);
        Button buttonAddArtifact = view.findViewById(R.id.buttonAddItem);
    }

    /**
     * Binds EditText variables to the views in fragment_add_item.xml.
     */
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

    /**
     * Binds Spinner variables to the views in fragment_add_item.xml.
     */
    private void bindSpinners(@NonNull View view) {
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerMaterial = view.findViewById(R.id.spinnerMaterial);
        spinnerDynasty = view.findViewById(R.id.spinnerDynasty);
    }

    /**
     * Adds choices to the category Spinner.
     */
    private void configureCategorySpinner() {
        ArrayAdapter<CharSequence> categoryAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.artifact_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    /**
     * Adds choices to the material Spinner.
     */
    private void configureMaterialSpinner() {
        ArrayAdapter<CharSequence> materialAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.artifact_materials, android.R.layout.simple_spinner_item);
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(materialAdapter);
    }

    /**
     * Adds choices to the dynasty Spinner.
     */
    private void configureDynastySpinner() {
        ArrayAdapter<CharSequence> dynastyAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.artifact_dynasties, android.R.layout.simple_spinner_item);
        dynastyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDynasty.setAdapter(dynastyAdapter);
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

    public Spinner getSpinnerCategory() {
        return spinnerCategory;
    }

    public Spinner getSpinnerMaterial() {
        return spinnerMaterial;
    }

    public Spinner getSpinnerDynasty() {
        return spinnerDynasty;
    }

    public Uri getSelectedImageUri() {
        return imageUri;
    }

    public SupabaseImageUploader getSupabaseImageUploader() {return imageUploader;}

}

