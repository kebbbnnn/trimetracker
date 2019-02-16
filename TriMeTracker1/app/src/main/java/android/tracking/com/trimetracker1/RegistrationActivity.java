package android.tracking.com.trimetracker1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.tracking.com.trimetracker1.data.UserData;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userPassword, userPassword2, userEmail, userContact;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        getSupportActionBar().setTitle("Create Account");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account. Please wait...");

        regButton.setOnClickListener(v -> {
            progressDialog.show();
            if (validate()) {
                //register to the database
                String user_email = userEmail.getText().toString().trim();
                String user_password = userPassword.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        String userId = user.getUid();
                        String name = userName.getText().toString();
                        String email = user.getEmail();
                        String mobile = userContact.getText().toString();
                        UserData userData = new UserData(userId, name, email, mobile);

                        UserProfileChangeRequest userUpdate = new UserProfileChangeRequest.Builder().setDisplayName(userData.name).build();
                        user.updateProfile(userUpdate).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").push();
                                userRef.setValue(userData, (error, databaseReference) -> {
                                    progressDialog.dismiss();
                                    if (error != null) {
                                        showError();
                                    } else {
                                        showSuccess();
                                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                    }
                                });
                            } else {
                                showError();
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        showError();
                    }
                });
            }

        });

        userLogin.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this, MainActivity.class)));
    }

    private void setupUIViews() {
        userName = findViewById(R.id.rName);
        userEmail = findViewById(R.id.rEmail);
        userContact = findViewById(R.id.rContact);
        userPassword = findViewById(R.id.rPassword);
        userPassword2 = findViewById(R.id.rPassword2);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
    }

    private boolean validate() {
        boolean result = false;
        String name = userName.getText().toString();
        String password = userPassword.getText().toString();
        String password2 = userPassword2.getText().toString();
        String email = userEmail.getText().toString();

        if (name.isEmpty() || password.isEmpty() || password2.isEmpty() || email.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(password2)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }


    private void showError() {
        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
    }

    private void showSuccess() {
        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
    }
}
