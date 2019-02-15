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

<<<<<<< HEAD
    private void setupUIViews(){
        userName = (EditText)findViewById(R.id.rName);
        userEmail = (EditText)findViewById(R.id.rEmail);
        userPassword = (EditText)findViewById(R.id.rPassword);
        userPassword2 = (EditText)findViewById(R.id.rPassword2);
        regButton = (Button)findViewById(R.id.btnRegister);
        userLogin = (TextView)findViewById(R.id.tvUserLogin);
=======
        userLogin.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this, MainActivity.class)));
    }
>>>>>>> 5a7ff91c61662486fbba2b92994a940c3f362948

    private void setupUIViews() {
        userName = findViewById(R.id.rName);
        userEmail = findViewById(R.id.rEmail);
        userPassword = findViewById(R.id.rPassword);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
    }

    private boolean validate() {
        boolean result = false;
        String name = userName.getText().toString();
        String password = userPassword.getText().toString();
        String password2 = userPassword2.getText().toString();
        String email = userEmail.getText().toString();

<<<<<<< HEAD
        if(name.isEmpty() || password.isEmpty() || password2.isEmpty() || email.isEmpty()){
            Toast.makeText(this, "Please enter all the details",Toast.LENGTH_SHORT).show();


        }else if(!password.equals(password2)){
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();

=======
        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
>>>>>>> 5a7ff91c61662486fbba2b92994a940c3f362948
        } else {
            result = true;
        }
        return result;
    }
}
