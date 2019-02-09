package android.tracking.com.trimetracker1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
private Button btnwelcome;


    private EditText Email;
    private EditText Password;
    private Button Login;
    private TextView userRegister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email = (EditText)findViewById(R.id.userEmail);
        Password = (EditText)findViewById(R.id.userPassword);
        Login = (Button)findViewById(R.id.btnUserLogin);
        userRegister = (TextView)findViewById(R.id.tvRegister);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Email.getText().toString(), Password.getText().toString());
            }
        });

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        btnwelcome = (Button)findViewById(R.id.btnWelcome);
        btnwelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
            }
        });
    }
    private  void validate(String userEmail, String userPassword){
        if((userEmail.equals("Admin")) && (userPassword.equals("1234"))){
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"Incorrect Email or Password!", Toast.LENGTH_LONG);
            toast.show();

        }
    }
}
