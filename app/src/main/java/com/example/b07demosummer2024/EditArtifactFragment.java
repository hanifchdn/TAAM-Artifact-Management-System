package com.example.b07demosummer2024;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class EditArtifactFragment extends Fragment {

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

    private Artifact artifactModel;
    private String artifactLOT;
    private boolean isDatabaseQueryRunning;


    /**
     * DO NOT USE NO ARGUMENT CONSTRUCTOR
     * no-arg Constructor is used to prevent crashing when rotating phone
     */
    public EditArtifactFragment() {}

    /**
     * Create the edit fragment using an existing artifact to fill in the text fields
     * @param existingArtifact to display in fields.
     */
    public EditArtifactFragment(Artifact existingArtifact) {
        super();
        artifactModel = existingArtifact;
        artifactLOT = artifactModel.getLOT();
    }

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
        return inflater.inflate(R.layout.fragment_edit_artifact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // UI setup
        super.onViewCreated(view, savedInstanceState);
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        bindTextFields(view);
        bindSpinners(view);
        configureCategorySpinner();
        configureMaterialSpinner();
        configureDynastySpinner();

        //image setup
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> imageSelectionLauncher.launch("image/*"));
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);

        //set textfields based on artifact
        setTextFields();

        //onclick listener
        Button buttonEditArtifact = view.findViewById(R.id.buttonEdit);
        buttonEditArtifact.setOnClickListener(v -> {
            //ensure input stays valid
            if (!validateInputs()) {
                return;
            }
            if (isDatabaseQueryRunning) {
                Toast.makeText(requireContext(), "Woah! The Artifact is being updated, please wait before trying again", Toast.LENGTH_SHORT).show();
                return;
            }
            isDatabaseQueryRunning = true;

            // update artifact model
            artifactModel.setName(editTextArtifactName.getText().toString());
            artifactModel.setDescription(editTextDescription.getText().toString());
            artifactModel.setCategory(spinnerCategory.getSelectedItem().toString());
            artifactModel.setMaterial(spinnerMaterial.getSelectedItem().toString());
            artifactModel.setDynasty(spinnerDynasty.getSelectedItem().toString());
            artifactModel.setHeight(editTextHeight.getText().toString());
            artifactModel.setDepth(editTextDepth.getText().toString());
            artifactModel.setWidth(editTextWidth.getText().toString());
            artifactModel.setCulturalOrigin(editTextCulturalOrigin.getText().toString());
            artifactModel.setCondition(editTextConditionReport.getText().toString());
            artifactModel.setCurrentLocation(editTextCurrentLocation.getText().toString());
            artifactModel.setAcquisitionMethod(editTextAcquisitionMethod.getText().toString());
            artifactModel.setProvenance(editTextProvenance.getText().toString());
            artifactModel.setAccessionNumber(editTextAccessionNumber.getText().toString());
            artifactModel.setNotes(editTextNotes.getText().toString());

            //url checker and artifact db updater
            if (imageUri != null) {
                ImageDatabaseWriter imageDatabaseWriter = new ImageDatabaseWriter();
                imageDatabaseWriter.addToDatabase(imageUploader, imageUri, artifactModel.getLOT(), (url) -> {
                    if (url != null) {
                        artifactModel.setImageUrl(url);
                    } else {
                        Toast.makeText(requireContext(), "Image upload failed.", Toast.LENGTH_SHORT).show();
                    }
                    updateArtifact(artifactModel);
                });
            } else {
                updateArtifact(artifactModel);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    /**
                     * Ignore the back button while a delete
                     * request is running.
                     */
                    @Override
                    public void handleOnBackPressed() {
                        if (isDatabaseQueryRunning) {
                            return;
                        }
                        setEnabled(false);
                        getParentFragmentManager().popBackStack();
                    }
                }
        );

    }

    /**
     * Runs an update artifact query based on the artifactModel
     * Also updates isDatabaseQueryRunning to true/false when query is running
     * @param artifact to update in firebase db
     */
    private void updateArtifact(Artifact artifact){
        ArtifactDatabaseWriter writer = new ArtifactDatabaseWriter();
        writer.updateDatabase(artifact, new WriteCallback() {
            @Override
            public void onSuccess(){
                Toast.makeText(requireContext(), "Artifact updated successfully.", Toast.LENGTH_SHORT).show();
                isDatabaseQueryRunning = false;
                getParentFragmentManager().popBackStack();
            }
            @Override
            public void onFailure(String e){
                Toast.makeText(requireContext(), "Failed to update artifact: " + e, Toast.LENGTH_SHORT).show();
                isDatabaseQueryRunning = false;
            }

        });
    }

    /**
     * Determines if a LOT is valid
     * @param lot
     * @return boolean isValid
     */
    private boolean isValidLot(String lot){
        return lot.matches("^[a-zA-Z0-9-]+$");
    }

    /**
     * Validates the user's input, if invalid, let user know through textfield warning
     * @return true if input is valid, false otherwise
     */
    private boolean validateInputs(){
        String lot = editTextLot.getText().toString().trim();
        String name = editTextArtifactName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String material = spinnerMaterial.getSelectedItem().toString();
        String dynasty = spinnerDynasty.getSelectedItem().toString();
        if (lot.isEmpty()){
            editTextLot.setError("LOT Number is required");
            return false;
        }
        if( !isValidLot(lot)){
            editTextLot.setError("LOT Number can only contain letters, numbers, and hyphens");
            return false;
        }
        if (name.isEmpty()){
            editTextArtifactName .setError("Name is required");
            return false;
        }
        if (description.isEmpty()){
            editTextDescription.setError("Description is required");
            return false;
        }
        if (category.equals("Select category")){
            Toast.makeText(requireContext(), "Please select a category.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (material.equals("Select material")){
            Toast.makeText(requireContext(), "Please select a material.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dynasty.equals("Select dynasty/period")){
            Toast.makeText(requireContext(), "Please select a dynasty/period.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    /**
     * Sets the textfields and spinners to be the values of artifactModel
     */
    private void setTextFields() {
        //set spinners
        ArrayAdapter<CharSequence> materialAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.artifact_materials,
                        android.R.layout.simple_spinner_item);
        if (artifactModel.getMaterial() != null && !artifactModel.getMaterial().isEmpty()) {
            spinnerMaterial.setSelection(materialAdapter.getPosition(artifactModel.getMaterial()));
        }
        ArrayAdapter<CharSequence> categoryAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.artifact_categories,
                        android.R.layout.simple_spinner_item);
        if (artifactModel.getCategory() != null && !artifactModel.getCategory().isEmpty()) {
            spinnerCategory.setSelection(categoryAdapter.getPosition(artifactModel.getCategory()));
        }
        ArrayAdapter<CharSequence> dynastyAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.artifact_dynasties,
                        android.R.layout.simple_spinner_item);
        if (artifactModel.getDynasty() != null && !artifactModel.getDynasty().isEmpty()) {
            spinnerDynasty.setSelection(dynastyAdapter.getPosition(artifactModel.getDynasty()));
        }

        //set text fields
        editTextLot.setText(artifactLOT);
        editTextArtifactName.setText(artifactModel.getName());
        editTextDescription.setText(artifactModel.getDescription());
        editTextHeight.setText(artifactModel.getHeight());
        editTextDepth.setText(artifactModel.getDepth());
        editTextWidth.setText(artifactModel.getWidth());
        editTextCulturalOrigin.setText(artifactModel.getCulturalOrigin());
        editTextConditionReport.setText(artifactModel.getCondition());
        editTextCurrentLocation.setText(artifactModel.getCurrentLocation());
        editTextAcquisitionMethod.setText(artifactModel.getAcquisitionMethod());
        editTextProvenance.setText(artifactModel.getProvenance());
        editTextAccessionNumber.setText(artifactModel.getAccessionNumber());
        editTextNotes.setText(artifactModel.getNotes());

        //set image if there is already one
        if (artifactModel.getImageUrl() != null && !artifactModel.getImageUrl().isEmpty()) {
            Glide.with(this).load(artifactModel.getImageUrl()).into(selectedImagePreview);
        }
    }

    /**
     * Clears references to the fragment's views when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editTextLot = null;
        editTextArtifactName = null;
        editTextDescription = null;
        editTextCulturalOrigin = null;
        editTextHeight = null;
        editTextWidth = null;
        editTextDepth = null;
        editTextConditionReport = null;
        editTextCurrentLocation = null;
        editTextAcquisitionMethod = null;
        editTextProvenance = null;
        editTextAccessionNumber = null;
        editTextNotes = null;
        spinnerCategory = null;
        spinnerMaterial = null;
        spinnerDynasty = null;
        selectedImagePreview = null;
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

