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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemFragment extends Fragment {

    private Uri imageUri;
    private ImageView selectedImagePreview;
    private ActivityResultLauncher<String> imageSelectionLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            selectedImagePreview.setImageURI(uri);
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
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> imageSelectionLauncher.launch("image/*"));
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);
    }

    public Uri getSelectedImageUri() {
        return imageUri;
    }
}

