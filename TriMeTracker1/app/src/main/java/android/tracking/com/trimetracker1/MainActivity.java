package android.tracking.com.trimetracker1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private Button btnwelcome;
    private EditText Email;
    private EditText Password;
    private Button Login;
    private TextView userRegister, reset;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session.getInstance().initPushNotification();
        setContentView(R.layout.activity_main);

        Email = findViewById(R.id.userEmail);
        Password = findViewById(R.id.userPassword);
        Login = findViewById(R.id.btnUserLogin);
        userRegister = findViewById(R.id.tvRegister);
        reset = findViewById(R.id.tvForgotPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this, HomePageNav.class));
            finish();
            return;
        }

        Login.setOnClickListener(v -> validate(Email.getText().toString(), Password.getText().toString()));

        userRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegistrationActivity.class)));


        reset.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
            startActivity(intent);
        });
    }

    private void validate(String userEmail, String userPassword) {
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
//                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                checkEmailVerification();
//                startActivity(new Intent(MainActivity.this, HomePageNav.class));
            } else {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        startActivity(new Intent(MainActivity.this, HomePageNav.class));

//        if(emailflag){
//            finish();
//            startActivity(new Intent(MainActivity.this, SecondActivity.class));
//        }else{
//            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
//            firebaseAuth.signOut();
//        }
    }

}
