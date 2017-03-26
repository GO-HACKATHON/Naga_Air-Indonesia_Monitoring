package com.naga_air.studioteam.indonesiamonitoring.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naga_air.studioteam.indonesiamonitoring.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements View.OnClickListener{


    public ReportFragment() {
        // Required empty public constructor
    }
    private float longitude,latitude;
    private String categories,opini;
    private int score;
    private Spinner mCategory,mScore;
    private EditText mLatitude, mLongitude,mOpini;
    private Button mSubmitBtn;
    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        mSelectImage = (ImageButton)view.findViewById(R.id.imageSelect);
        mSubmitBtn = (Button)view.findViewById(R.id.submitButton);
        mCategory = (Spinner)view.findViewById(R.id.catspin);
        mScore = (Spinner)view.findViewById(R.id.scspin);
        mLatitude = (EditText)view.findViewById(R.id.latitude);
        mLongitude = (EditText)view.findViewById(R.id.longitude);
        mOpini = (EditText)view.findViewById(R.id.opiniEditText);

        ArrayAdapter<CharSequence> cAdapter = ArrayAdapter.createFromResource(ReportFragment.this.getActivity(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(cAdapter);

        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(ReportFragment.this.getActivity(),
                R.array.score_array, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mScore.setAdapter(sAdapter);


        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                categories = parent.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                if(parent.getItemAtPosition(i) == "A+"){
                    score = 10;
                }if(parent.getItemAtPosition(i) == "A"){
                    score = 8;
                }if(parent.getItemAtPosition(i) == "B"){
                    score = 6;
                }if(parent.getItemAtPosition(i) == "C"){
                    score = 4;
                }if(parent.getItemAtPosition(i) == "D"){
                    score = 2;
                }if(parent.getItemAtPosition(i) == "E"){
                    score = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("geofireTest");

        mProgress = new ProgressDialog(ReportFragment.this.getActivity());

        mSubmitBtn.setOnClickListener(this);
        mSelectImage.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageSelect:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                break;
            case R.id.submitButton:
                submitReport();
                MonitoringFragment monitoringFragment = new MonitoringFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_home, monitoringFragment).addToBackStack("tag").commit();
                Toast.makeText(ReportFragment.this.getActivity(), "Thankyou for your help to make Indonesia better!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
        }
    }

    public void submitReport(){
        mProgress.setMessage("Submit in Progress...");
        mProgress.show();
        latitude = Float.valueOf(mLatitude.getText().toString());
        longitude = Float.valueOf(mLongitude.getText().toString());
        opini = mOpini.getText().toString();

        if(!TextUtils.isEmpty(String.valueOf(latitude)) && !TextUtils.isEmpty(String.valueOf(longitude))
                && mImageUri != null && !TextUtils.isEmpty(opini)){

            StorageReference filepath = mStorage.child("photos/").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newMark = mDatabase.child("features").push();
                    newMark.child("geometry").child("coodinates").child("0").setValue(longitude);
                    newMark.child("geometry").child("coodinates").child("1").setValue(latitude);
                    newMark.child("geometry").child("type").setValue("Point");
                    newMark.child("properties").child("category").setValue(categories);
                    newMark.child("properties").child("opinion").setValue(opini);
                    newMark.child("properties").child("imageUrl").setValue(downloadUrl.toString());
                    newMark.child("properties").child("score").setValue(score);
                    newMark.child("type").setValue("Feature");
                    mProgress.dismiss();

                }
            });
        }else{
            Toast.makeText(ReportFragment.this.getActivity(), "Please fill all the requirement", Toast.LENGTH_SHORT).show();
            return;
        }


    }
}
