package view.entrance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.securevault19.securevault2019.R;
import com.securevault19.securevault2019.user.CurrentUser;
import com.securevault19.securevault2019.user.User;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cryptography.Cryptography;
import local_database.DatabaseClient;
import view.explorer.PatternLockView_Activity;
import view.preferences.SecurityLevel_Activity;
import view_model.records.User_ViewModel;

@SuppressLint("Registered")
public class NewUser_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private String ORIGIN;
    private String CRYPTO_KEY;
    private String returnedPattern;
    private String returnedSecurityLevel;
    private String firstNameUser;
    private String masterPasswordUser;
    private String emailUser;
    private String verifyPasswordUser;
    private String lastNameUser;
    private String dateOfBirthUser;
    private String optionalQuestionUser;
    private String optionalAnswerUser;
    private Cryptography cryptography;
    private EditText calendarBtn;
    private int updateUserMode = 0;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +                        //at least 1 digit
                    "(?=\\S+$)");                        //no white spaces
//                    "(?=.*[a-z])" +                    //at least 1 lower case letter
//                    "(?=.*[A-Z])" +                   //at least 1 upper case letter
//                    //"(?=.*[a-zA-Z])" +             //any letter
//                    "(?=.*[!@,)#&_'$*(%~$%^&+=])" + //at least 1 special character
//                    ".{8,24}" +                   //at least 8 characters, less than 24
//                    "$");

    private static int i = 1;
    private User_ViewModel viewModel;
    private ImageView logo;
    private ImageButton saveBtn, cancelBtn;
    private ImageButton showPass, hidePass, copyPass;
    private MediaPlayer mediaPlayer;
    private Animation animation1, animation2, animation3;
    private ScrollView scrollView;
    private TextView activityTitle;
    private Typeface myFont;
    private EditText password_EditText, userName_EditText, email_EditText, verifyPassword_EditText;
    private EditText lastName_EditText, dateOfRegistration_EditText, optionalQuestion_EditText, optionalAnswer_EditText;
    private ImageButton securityLevelBtn;
    private LinearLayout userName, password, email;
    private String encryptedPassword, encryptedUserName, encryptedEmail, encryptedPattern, encryptedSecurityLevel, encryptedLastName,
            encryptedDateOfBirth, encryptedOptionalQuestion, encryptedOptionalAnswer;
    private String decryptedPassword, decryptedUserName, decryptedEmail, decryptedPattern, decryptedLastName, decryptedDateOfBirth, decryptedOptionalQuestion, decryptedOptionalAnswer;
    private User user = null;
    private ImageButton showVerPass, hideVerPass;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle saveBtndInstanceState) {
        super.onCreate(saveBtndInstanceState);
        setContentView(R.layout.activity_new_user);
        viewModel = ViewModelProviders.of(this).get(User_ViewModel.class);

        ORIGIN = getIntent().getStringExtra("ORIGIN");
        cryptography = new Cryptography();

        viewModel = ViewModelProviders.of(this).get(User_ViewModel.class);


        mediaPlayer = MediaPlayer.create(this, R.raw.button);
        progressBar = findViewById(R.id.progressBar);
        logo = findViewById(R.id.logo);
        securityLevelBtn = findViewById(R.id.securityLevelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        showPass = findViewById(R.id.showPass);
        hidePass = findViewById(R.id.hidePass);
        copyPass = findViewById(R.id.copyPass);
        scrollView = findViewById(R.id.frame);
        activityTitle = findViewById(R.id.activityTitle);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        password_EditText = findViewById(R.id.password_EditText);
        email = findViewById(R.id.email);
        userName_EditText = findViewById(R.id.username_EditText);
        lastName_EditText = findViewById(R.id.lastName_EditText);
        email_EditText = findViewById(R.id.email_EditText);
        verifyPassword_EditText = findViewById(R.id.verifyPassword_EditText);
        dateOfRegistration_EditText = findViewById(R.id.dateOfBirth_EditText);
        optionalQuestion_EditText = findViewById(R.id.optionalQuestion_EditText);
        optionalAnswer_EditText = findViewById(R.id.optionalAnswer_EditText);
        showVerPass = findViewById(R.id.showVerPass);
        hideVerPass = findViewById(R.id.hideVerPass);
        //Animation Sets
        animation1 = AnimationUtils.loadAnimation(NewUser_Activity.this, R.anim.zoomin);
        animation2 = AnimationUtils.loadAnimation(NewUser_Activity.this, R.anim.zoomin_fade);
        animation3 = AnimationUtils.loadAnimation(NewUser_Activity.this, R.anim.buttonpush_anim);
        scrollView.startAnimation(animation2);


        //        Set logo's font to category's text
        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/OutlierRail.ttf");
        activityTitle.setTypeface(myFont);


// if we came from the menu(Main Screen) and we are going to make update.
        if (ORIGIN.equals("MainScreen")) {
            email_EditText.setClickable(false);
            email_EditText.setFocusable(false);
            Log.d("profielUserTest", "origin " + ORIGIN);
            displayUserInfo();
            setSecureLevelIcon();
        }


        password_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordCalculation();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordCalculation();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordCalculation();
            }
        });
    }

    @Override
    public void onBackPressed() {
        cancelWarningMessage(null);
    }

    public void displayUserInfo() {
        securityLevelBtn.setClickable(false);
        activityTitle.setText(getString(R.string.myProfile));
        CRYPTO_KEY = getIntent().getStringExtra("CRYPTO_KEY");
        try {
            email_EditText.setText(cryptography.decrypt(CurrentUser.getInstance().getEmail(), CRYPTO_KEY));
            userName_EditText.setText(cryptography.decrypt(CurrentUser.getInstance().getFirstName(), CRYPTO_KEY));
            lastName_EditText.setText(cryptography.decrypt(CurrentUser.getInstance().getLastName(), CRYPTO_KEY));
            password_EditText.setText(cryptography.decrypt(CurrentUser.getInstance().getMasterPassword(), CRYPTO_KEY));
            optionalQuestion_EditText.setText(cryptography.decrypt(CurrentUser.getInstance().getOptionalQuestion(), CRYPTO_KEY));
            optionalAnswer_EditText.setText(cryptography.decrypt(CurrentUser.getInstance().getOptionalAnswer(), CRYPTO_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // the patterns dosent change if the user didn't touched it.
        try {
            returnedPattern = cryptography.decrypt(CurrentUser.getInstance().getPatternLock(), CRYPTO_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

// sets the icon of the secureLevel.
    public void setSecureLevelIcon() {
        String secureLevel = CurrentUser.getInstance().getSecureLevel();
        if (secureLevel.equals("1")) {
            findViewById(R.id.securityLevelBtn).setBackground(getDrawable(R.drawable.level1_logo));
        } else if (secureLevel.equals("3")) {
            findViewById(R.id.securityLevelBtn).setBackground(getDrawable(R.drawable.level3_logo));
        } else
            findViewById(R.id.securityLevelBtn).setBackground(getDrawable(R.drawable.level2_logo));
    }

    public void showPass(View view) {
        if (view == showPass || view == hidePass) {

            if (showPass.getVisibility() == View.VISIBLE) {
                password_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hidePass.setVisibility(View.VISIBLE);
                showPass.setVisibility(View.GONE);
                showPass.startAnimation(animation3);
            } else {
                password_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hidePass.setVisibility(View.GONE);
                showPass.setVisibility(View.VISIBLE);
                hidePass.startAnimation(animation3);
            }
            password_EditText.requestFocus();
            password_EditText.setSelection(password_EditText.getText().length());
        }


        if (view == showVerPass || view == hideVerPass) {

            if (showVerPass.getVisibility() == View.VISIBLE) {
                verifyPassword_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hideVerPass.setVisibility(View.VISIBLE);
                showVerPass.setVisibility(View.GONE);
                showVerPass.startAnimation(animation3);
            } else {
                verifyPassword_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hideVerPass.setVisibility(View.GONE);
                showVerPass.setVisibility(View.VISIBLE);
                hideVerPass.startAnimation(animation3);
            }
            verifyPassword_EditText.requestFocus();
            verifyPassword_EditText.setSelection(verifyPassword_EditText.getText().length());
        }
    }

    public void cancelWarningMessage(final View view) {
        mediaPlayer.start();
        cancelBtn.startAnimation(animation3);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.cancelation_request);
        alert.setMessage(R.string.cancelation_message);
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Not saved", Toast.LENGTH_SHORT).show();
                back();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Not Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create().show();
    }

    public void back() {
        finish();
    }

    public void copyPass(View view) {
        mediaPlayer.start();
        copyPass.startAnimation(animation3);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(password_EditText.getText().toString());
        Toast.makeText(this, "Password Copied", Toast.LENGTH_SHORT).show();

    }


    // the choosePattern methos public because we want to make changes here from Setting
    public void choosePattern(View view) {
        Intent intent = new Intent(this, PatternLockView_Activity.class);
        intent.putExtra("EXTRA_ORIGIN", "NewUser_Activity");
        startActivityForResult(intent, 1);
    }

    // the choseSecurityLevel method public because we want to make changes here from Setting
    public void chooseSecurityLevel(View view) {
        Intent intent = new Intent(this, SecurityLevel_Activity.class);
        intent.putExtra("ORIGIN", ORIGIN);
        startActivityForResult(intent, 2);
    }

    @SuppressLint("StaticFieldLeak")
    public void createNewAccount(View view) {               // onClick func
        mediaPlayer.start();
        saveBtn.startAnimation(animation3);
        emailUser = email_EditText.getText().toString();
        firstNameUser = userName_EditText.getText().toString();
        lastNameUser = lastName_EditText.getText().toString();
        masterPasswordUser = password_EditText.getText().toString();
        verifyPasswordUser = verifyPassword_EditText.getText().toString();
        dateOfBirthUser = dateOfRegistration_EditText.getText().toString();
        optionalQuestionUser = optionalQuestion_EditText.getText().toString();
        optionalAnswerUser = optionalAnswer_EditText.getText().toString();

        CRYPTO_KEY = emailUser;

        // checking if the password and the verify password are the same
        if (!masterPasswordUser.equals(verifyPasswordUser) || masterPasswordUser.equals("")) {
            Toast.makeText(getApplicationContext(), "Password is not verified", Toast.LENGTH_SHORT).show();
            verifyPassword_EditText.requestFocus();
            return;
        }

        if (returnedPattern == null) {
            if (ORIGIN.equals("MainScreen")) {
                try {
                    // verifying that the user wants to stay with his old pattern.
                    returnedPattern = cryptography.decrypt(CurrentUser.getInstance().getPatternLock(), CRYPTO_KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                // checking if the user entered Pattern
                // if not, make a Toast to remind him.
                Toast.makeText(getApplicationContext(), "You must make Pattern!", Toast.LENGTH_SHORT).show();
                Log.d("returnedPattern", "chosedPattern ");
            }

        } else {
            // if the user didnt chose his secureLevel, the default will set it to 2.
            if (returnedSecurityLevel == null) {
                if (ORIGIN.equals("MainScreen")) {
                    returnedSecurityLevel = CurrentUser.getInstance().getSecureLevel();
                } else
                    returnedSecurityLevel = "2";
            }
            saveUserDetails(view);
        }
    }


    @SuppressLint("StaticFieldLeak")            //preventing memory leak
    protected void saveUserDetails(View view) {


        try {
            // encrypt al lthe data
            encryptedEmail = cryptography.encrypt(CRYPTO_KEY);
            encryptedPattern = cryptography.encryptWithKey(CRYPTO_KEY, returnedPattern);
            encryptedUserName = cryptography.encryptWithKey(CRYPTO_KEY, firstNameUser);
            encryptedPassword = cryptography.encryptWithKey(CRYPTO_KEY, masterPasswordUser);
            encryptedLastName = cryptography.encryptWithKey(CRYPTO_KEY, lastNameUser);
            encryptedDateOfBirth = cryptography.encryptWithKey(CRYPTO_KEY, dateOfBirthUser);
            encryptedOptionalQuestion = cryptography.encryptWithKey(CRYPTO_KEY, optionalQuestionUser);
            encryptedOptionalAnswer = cryptography.encryptWithKey(CRYPTO_KEY, optionalAnswerUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (emailUser.isEmpty()) {
            Toast.makeText(this, "Email is Empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            findViewById(R.id.ProgressBar).setVisibility(View.VISIBLE);

            //@SuppressLint("StaticFieldLeak") //preventing memory leak
            new AsyncTask<Void, Void, Void>() {
                private int flag = 0;

                @Override
                protected Void doInBackground(Void... voids) {

                    user = new User(encryptedUserName, encryptedLastName, encryptedDateOfBirth, encryptedEmail, encryptedOptionalQuestion,
                            encryptedOptionalAnswer, encryptedPassword, returnedSecurityLevel, encryptedPattern);
                    if (ORIGIN.equals("MainScreen")) {
                        // if we came from MainScrren, we probably want to update the users data.
                        viewModel.update(user);
                        CurrentUser.getInstance(user);
                        flag = 2;
                    } else
                        try {
                            // useing the direct way to talk to the database, and not with viewModel.
                            // even if the view model found same user, it igrore it.
                            DatabaseClient.getInstance(getApplicationContext()).getRecordDatabase2().daoUser().insert(user);
                            flag = 1;
                        } catch (Exception e) {
                            // if the email all ready exists, we will get Exception and flag will be  flag == 0;
                            e.printStackTrace();
                        }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (flag == 1) {
                        Toast.makeText(getApplicationContext(), "" + emailUser + " Created!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (flag == 2) {
                        Toast.makeText(getApplicationContext(), "" + emailUser + " Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email all ready exists", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.ProgressBar).setVisibility(View.GONE);
                        return;
                    }
                }
            }.execute();
        }
        mediaPlayer.start();
        saveBtn.startAnimation(animation3);
        findViewById(R.id.ProgressBar).setVisibility(View.GONE);


    }


    // this func is getting value from other activities.
    // it get pattern from PatternLockActivity              ( requestCode 1 )
    // it get securityLevel from SecurityLevelActivity      ( requestCode 2 )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                returnedPattern = returnedIntent.getStringExtra("PATTERN_LOCK");
                findViewById(R.id.patternBtn).setBackground(getDrawable(R.drawable.pattern_icon_done));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Pattern not saved", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                returnedSecurityLevel = returnedIntent.getStringExtra("SECURITY_LEVEL");
                if (returnedSecurityLevel == null) {
                    // didn't choose pattern!
                    returnedSecurityLevel = "2";
                } else {
                    // setting the icon according to the selected secureLevel.
                    if (returnedSecurityLevel.equals("1")) {
                        findViewById(R.id.securityLevelBtn).setBackground(getDrawable(R.drawable.level1_logo));
                    } else if (returnedSecurityLevel.equals("2")) {
                        findViewById(R.id.securityLevelBtn).setBackground(getDrawable(R.drawable.level2_logo));
                    } else if (returnedSecurityLevel.equals("3")) {
                        findViewById(R.id.securityLevelBtn).setBackground(getDrawable(R.drawable.level3_logo));
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Security level not saved", Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void openCalendarUser(View view) {

        mediaPlayer.start();
        dateOfRegistration_EditText.startAnimation(animation3);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = month + "/" + dayOfMonth + "/" + year;
        if (dateOfRegistration_EditText.isFocused()) {
            dateOfRegistration_EditText.setText(date);
        }
    }


    public boolean validateUsername(String userName) {
        String usernameInput = userName;

        if (usernameInput.isEmpty()) {
            return false;
        } else if (usernameInput.length() > 15) {
            return false;
        } else if (usernameInput.contains(" ")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validatePassword(String Password) {
        String passwordInput = Password;

        if (passwordInput.isEmpty()) {
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            return false;
        }

        return true;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isValidEmail(String emailInput) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailInput);
        return matcher.matches();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void passwordCalculation() {
        String temp = password_EditText.getText().toString();
        System.out.println(i + " current password is : " + temp);
        i++;

        int length = 0, uppercase = 0, lowercase = 0, digits = 0, symbols = 0, bonus = 0, requirements = 0;

        int lettersonly = 0, numbersonly = 0, cuc = 0, clc = 0;

        length = temp.length();
        for (int i = 0; i < temp.length(); i++) {
            if (Character.isUpperCase(temp.charAt(i)))
                uppercase++;
            else if (Character.isLowerCase(temp.charAt(i)))
                lowercase++;
            else if (Character.isDigit(temp.charAt(i)))
                digits++;

            symbols = length - uppercase - lowercase - digits;

        }

        for (int j = 1; j < temp.length() - 1; j++) {

            if (Character.isDigit(temp.charAt(j)))
                bonus++;

        }

        for (int k = 0; k < temp.length(); k++) {

            if (Character.isUpperCase(temp.charAt(k))) {
                k++;

                if (k < temp.length()) {

                    if (Character.isUpperCase(temp.charAt(k))) {

                        cuc++;
                        k--;

                    }

                }

            }

        }

        for (int l = 0; l < temp.length(); l++) {

            if (Character.isLowerCase(temp.charAt(l))) {
                l++;

                if (l < temp.length()) {

                    if (Character.isLowerCase(temp.charAt(l))) {

                        clc++;
                        l--;

                    }

                }

            }

        }

        System.out.println("length" + length);
        System.out.println("uppercase" + uppercase);
        System.out.println("lowercase" + lowercase);
        System.out.println("digits" + digits);
        System.out.println("symbols" + symbols);
        System.out.println("bonus" + bonus);
        System.out.println("cuc" + cuc);
        System.out.println("clc" + clc);

        if (length > 7) {
            requirements++;
        }

        if (uppercase > 0) {
            requirements++;
        }

        if (lowercase > 0) {
            requirements++;
        }

        if (digits > 0) {
            requirements++;
        }

        if (symbols > 0) {
            requirements++;
        }

        if (bonus > 0) {
            requirements++;
        }

        if (digits == 0 && symbols == 0) {
            lettersonly = 1;
        }

        if (lowercase == 0 && uppercase == 0 && symbols == 0) {
            numbersonly = 1;
        }

        int Total = (length * 4) + ((length - uppercase) * 2)
                + ((length - lowercase) * 2) + (digits * 4) + (symbols * 6)
                + (bonus * 2) + (requirements * 2) - (lettersonly * length * 2)
                - (numbersonly * length * 3) - (cuc * 2) - (clc * 2);

        System.out.println("Total" + Total);

        if (Total < 30) {
            progressBar.setProgress(Total - 15);
        } else if (Total >= 40 && Total < 50) {
            progressBar.setProgress(Total - 20);
        } else if (Total >= 56 && Total < 70) {
            progressBar.setProgress(Total - 25);
        } else if (Total >= 76) {
            progressBar.setProgress(Total - 30);
        } else {
            progressBar.setProgress(Total - 20);
        }

    }

}



