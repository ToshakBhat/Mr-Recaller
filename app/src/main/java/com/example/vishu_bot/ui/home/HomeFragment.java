package com.example.vishu_bot.ui.home;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.vishu_bot.GeminiPro;
import com.example.vishu_bot.R;
import com.example.vishu_bot.ResponseCallback;
import com.example.vishu_bot.databinding.FragmentHomeBinding;
import com.example.vishu_bot.ui.dashboard.DashboardFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TextView response;
    ImageView img;
    Button submit;
    String[] greets = {"Hey , Can you tell me , ","Tell me now , ","Let's crack this one  , ","No way you will answer this , ","Give it a shot  ,"};
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> user_answers = new ArrayList<>();
    ArrayList<String> real_answers = new ArrayList<>();

    private Handler handler;
    boolean status = false;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    TextView description;
    TextView question;
    TextToSpeech t1;
    private SpeechRecognizer speechRecognizer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        img = root.findViewById(R.id.logo);
        response = root.findViewById(R.id.response);
        question = root.findViewById(R.id.question);
        submit = root.findViewById(R.id.submit);
        handler = new Handler();


        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate_question();
            }
        });
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    int[] count = {0};
    public void generate_question(){
        speak();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //t1.speak("Click on the button below to experience Passive Recalling.", TextToSpeech.QUEUE_ADD,null);
                submit.setVisibility(View.VISIBLE);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        demo();

                    }
                });

            }

        },3000);
    }
    public void get_real_answers(GeminiPro model, String response2){
        model.getResponse("Generate a simple very short answer based on following question " + response2, new ResponseCallback() {
            @Override
            public void onResponse(String response3) {
                real_answers.add(response3);
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void demo(){
        GeminiPro model = new GeminiPro();


        model.getResponse("Generate 1 very simple question based on most important keyword in a way that can spark conversation." + response.getText().toString(), new ResponseCallback() {
            @Override
            public void onResponse(String response2) {
                question.setText(response2);
                questions.add(response2);
                get_real_answers(model,response2);
                if (count[0] == 5) {
                    Toast.makeText(getContext(), "Hurrray !!!", Toast.LENGTH_SHORT).show();
                    question.setText("Hurray ! Check the History for evaluation.");
                    for (int i=0; i < 5;i++){
                        reference.child("User").child("Question"+i).child("Question").setValue(questions.get(i));
                        reference.child("User").child("Question"+i).child("real_answers").setValue(real_answers.get(i));
                        reference.child("User").child("Question"+i).child("user_answers").setValue(user_answers.get(i));
                    }


                    response.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.INVISIBLE);


                } else {
                    t1.speak(greets[count[0]] + response2, TextToSpeech.QUEUE_ADD, null); //QUEUE_ADD means that suppose it needed to speak Hello first, then the other things needed to be said will be added to the Queue
                    count[0] += 1;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            generate_question();
                        }
                    }, 8000);


                }

                //progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
            }
        });

    }
    private void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi speak something");

        try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
        }
        catch(Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    response.setText(result.get(0));
                    user_answers.add(result.get(0));


                }
            }
        }
    }

}