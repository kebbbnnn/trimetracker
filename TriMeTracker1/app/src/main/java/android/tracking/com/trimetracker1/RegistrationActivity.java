package android.tracking.com.trimetracker1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userPassword, userPassword2, userEmail;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(v -> {
            if (validate()) {
                //register to the database
                String user_email = userEmail.getText().toString().trim();
                String user_password = userPassword.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(task -> {
                    Log.e("test", task.getResult().getUser().getUid());
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        userLogin.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this, MainActivity.class)));
    }

    private void setupUIViews() {
        userName = findViewById(R.id.rName);
        userEmail = findViewById(R.id.rEmail);
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
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(password2)) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }
}
