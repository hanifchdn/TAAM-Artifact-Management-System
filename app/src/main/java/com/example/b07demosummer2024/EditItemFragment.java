package com.example.b07demosummer2024;

import android.net.Uri;
import android.os.Bundle;
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

public class EditItemFragment extends Fragment {

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


    /**
     * DO NOT USE NO ARGUMENT CONSTRUCTOR
     * no-arg Constructor is used to prevent crashing when rotating phone
     */
    public EditItemFragment() {}

    /**
     * Create the edit fragment using an existing artifact to fill in the details
     * @param exisitingArtifact
     */
    public EditItemFragment(Artifact exisitingArtifact) {
        super();
        artifactModel = exisitingArtifact;
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
        return inflater.inflate(R.layout.fragment_edit_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // UI setup
        super.onViewCreated(view, savedInstanceState);
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

    }

    private void updateArtifact(Artifact artifact){
        ArtifactDatabaseWriter writer = new ArtifactDatabaseWriter();
        writer.updateDatabase(artifact, new WriteCallback() {
            @Override
            public void onSuccess(){
                Toast.makeText(requireContext(), "Artifact added successfully.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String e){
                Toast.makeText(requireContext(), "Failed to add artifact: " + e, Toast.LENGTH_SHORT).show();
            }

        });
    }
    private boolean isValidLot(String lot){
        return lot.matches("^[a-zA-Z0-9-]+$");
    }
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
        editTextAccessionNumber.setText(artifactModel.getAcquisitionMethod());
        editTextNotes.setText(artifactModel.getNotes());
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

