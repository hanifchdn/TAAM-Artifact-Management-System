package com.example.b07demosummer2024;

import android.net.Uri;
import android.os.Bundle;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddArtifactFragment extends Fragment {

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

    /**
     * Launches the Android photo picker to allow the user to select an image.
     * When an image is selected, updates the preview and initializes the image
     * uploader for future uploads.
     */
    private ActivityResultLauncher<String> imageSelectionLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            selectedImagePreview.setImageURI(uri);
                            imageUploader = new SupabaseImageUploader(getContext());
                        }
                    }
            );


    /**
     * Creates and returns the AddArtifact view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Inflated AddArtifact view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_artifact, container, false);
    }

    /**
     * Initializes UI components and click listeners.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton ToHome = view.findViewById(R.id.ToHome);
        ToHome.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        bindTextFields(view);
        bindSpinners(view);
        configureCategorySpinner();
        configureMaterialSpinner();
        configureDynastySpinner();

        // Bind the buttons and preview
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> imageSelectionLauncher.launch("image/*"));
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);
        Button buttonAddArtifact = view.findViewById(R.id.buttonAdd);

        // Setup buttonAddArtifact clickListener
        buttonAddArtifact.setOnClickListener(v -> {
            if (!validateInputs()) {
                return;
            }
            String lot = editTextLot.getText().toString();
            String name = editTextArtifactName.getText().toString();
            String description = editTextDescription.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String material = spinnerMaterial.getSelectedItem().toString();
            String dynasty = spinnerDynasty.getSelectedItem().toString();
            ArtifactDatabaseReader reader = new ArtifactDatabaseReader();
            reader.contains(lot, new ArtifactDatabaseReader.ContainsArtifactItemCallback() {
                @Override
                public void onSuccess(boolean contains) {
                    if (contains) {
                        Toast.makeText(getContext(), "LOT number already exists.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Artifact newArtifact = new Artifact(lot, name, description, category, material, dynasty);
                    newArtifact.setHeight(editTextHeight.getText().toString());
                    newArtifact.setDepth(editTextDepth.getText().toString());
                    newArtifact.setWidth(editTextWidth.getText().toString());
                    newArtifact.setCulturalOrigin(editTextCulturalOrigin.getText().toString());
                    newArtifact.setCondition(editTextConditionReport.getText().toString());
                    newArtifact.setCurrentLocation(editTextCurrentLocation.getText().toString());
                    newArtifact.setAcquisitionMethod(editTextAcquisitionMethod.getText().toString());
                    newArtifact.setProvenance(editTextProvenance.getText().toString());
                    newArtifact.setAccessionNumber(editTextAccessionNumber.getText().toString());
                    newArtifact.setNotes(editTextNotes.getText().toString());
                    if (imageUri != null) {
                        ImageDatabaseWriter imageDatabaseWriter = new ImageDatabaseWriter();
                        imageDatabaseWriter.addToDatabase(imageUploader, imageUri, lot, (url) -> {
                            if (url != null) {
                                newArtifact.setImageUrl(url);
                            } else {
                                Toast.makeText(requireContext(), "Image upload failed.", Toast.LENGTH_SHORT).show();
                            }
                            writeArtifact(newArtifact);
                        });
                    } else {
                        writeArtifact(newArtifact);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(requireContext(), "Could not verify LOT uniqueness", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Makes a query to add artifact to the database
     * @param artifact artifact to be added to the database
     */
    private void writeArtifact(Artifact artifact){
        ArtifactDatabaseWriter  writer = new ArtifactDatabaseWriter(requireContext());
        writer.addToDatabase(artifact, new WriteCallback() {
            @Override
            public void onSuccess(){
                Toast.makeText(requireContext(), "Artifact added successfully.", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
            @Override
            public void onFailure(String e){
                Toast.makeText(requireContext(), "Failed to add artifact: " + e, Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     *
     * @param lot lot to be checked
     * @return Whether lot is valid
     */
    private boolean isValidLot(String lot){
        return lot.matches("^[a-zA-Z0-9-]+$");
    }

    /**
     * Checks whether all required fields are filled in
     * @return Whether input is valid
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

}

