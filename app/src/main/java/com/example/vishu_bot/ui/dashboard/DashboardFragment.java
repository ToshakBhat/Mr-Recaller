package com.example.vishu_bot.ui.dashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vishu_bot.R;
import com.example.vishu_bot.TextSimilarity;
import com.example.vishu_bot.databinding.FragmentDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    LinearLayout main_layout;
    TextView result;
    static int red_score = 0;
    static int green_score = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        main_layout = root.findViewById(R.id.main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> user_answers = new ArrayList<>();
        ArrayList<String> real_answers = new ArrayList<>();
        result = root.findViewById(R.id.result);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    for (int i=0 ; i<5;i++){
                        String q1 = userSnapshot.child("Question"+i).child("Question").getValue().toString();
                        String real_answer = userSnapshot.child("Question"+i).child("real_answers").getValue().toString();
                        String user_answer = userSnapshot.child("Question"+i).child("user_answers").getValue().toString();
                        questions.add(q1);
                        user_answers.add(user_answer);
                        real_answers.add(real_answer);
                    }
                }

                for (int i = 0;i<5;i++){
                    double similarityScore = TextSimilarity.getCosineSimilarity(user_answers.get(i), real_answers.get(i));
                    createLinearLayout(i,questions.get(i),real_answers.get(i),user_answers.get(i),""+Math.round(similarityScore));
                }

                if (green_score > red_score){
                    result.setText("Keep Learning Keep Revising");
                }else{
                    result.setText("Revise your Concepts well !!");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createLinearLayout(int i,String question,String real_answer,String user_answer,String match_score){
        LinearLayout layout1 = new LinearLayout(getContext());

        //layout1.setId(uniqueId);
        layout1.setOrientation(LinearLayout.VERTICAL);
        layout1.setBackgroundColor(getResources().getColor(R.color.blue_900));


        int heightInPixels2 = 500;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // Width
                ViewGroup.LayoutParams.WRAP_CONTENT// Height
        );
        layoutParams.gravity = Gravity.CENTER;

        int leftMargin = 30; // in pixels
        int topMargin = 30; // in pixels
        int rightMargin = 30; // in pixels
        int bottomMargin = 30; // in pixels

        layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        // Apply LayoutParams to the LinearLayout
        layout1.setLayoutParams(layoutParams);

        TextView quest = new TextView(getContext());
        quest.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //quest.setTypeface(Typeface.DEFAULT_BOLD);
        quest.setTextAppearance(android.R.style.TextAppearance_Large);
        quest.setText("Q"+(i+1)+") "+question);


        TextView real_ans = new TextView(getContext());
        real_ans.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        real_ans.setTextAppearance(android.R.style.TextAppearance_Holo);
        real_ans.setTypeface(null, Typeface.ITALIC);
        real_ans.setText("Expected Answer : "+ real_answer);

        TextView user_ans = new TextView(getContext());
        user_ans.setTextAppearance(android.R.style.TextAppearance_Holo_Medium);

        user_ans.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        user_ans.setText("Your Answer : "+user_answer);

        TextView score = new TextView(getContext());
        score.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        score.setTextAppearance(android.R.style.TextAppearance_Medium);
        if(Integer.parseInt(match_score) < 10){
            red_score += 1;
            score.setTextColor(getResources().getColor(R.color.red));
        }else{
            green_score += 1;
            score.setTextColor(getResources().getColor(R.color.green));
        }

        score.setText("Matching Percentage : "+match_score+"%");

        quest.setPadding(0, 10, 0, 10); // Adjust the values as needed
        real_ans.setPadding(0, 15, 0, 15);
        user_ans.setPadding(0, 15, 0, 15);
        score.setPadding(0, 15, 0, 15);

        layout1.addView(quest);
        layout1.addView(user_ans);
        layout1.addView(real_ans);
        layout1.addView(score);


        main_layout.addView(layout1);


    }
}