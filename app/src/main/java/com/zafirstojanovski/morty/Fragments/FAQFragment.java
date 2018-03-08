package com.zafirstojanovski.morty.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zafirstojanovski.morty.FAQRecyclerView.Question;
import com.zafirstojanovski.morty.FAQRecyclerView.RecyclerAdapter;
import com.zafirstojanovski.morty.R;

import java.util.ArrayList;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public class FAQFragment extends Fragment {

    private OnQuestionSelectedListener onQuestionSelectedListener;

    public interface OnQuestionSelectedListener {
        void onQuestionSelected(String question);
    }

    private RecyclerView finkiRecyclerView;
    private RecyclerView.Adapter finkiRecyclerAdapter;
    private RecyclerView.LayoutManager finkiLayoutManager;
    private ArrayList<Question> finkiQuestions;

    private RecyclerView redditRecyclerView;
    private RecyclerView.Adapter redditRecyclerAdapter;
    private RecyclerView.LayoutManager redditLayoutManager;
    private ArrayList<Question> redditQuestions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    public FAQFragment() {}

    public static FAQFragment newInstance() {
        return new FAQFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onQuestionSelectedListener = (OnQuestionSelectedListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnQuestionSelectedListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchData();
        initRecyclers();
    }


    private void fetchData() {
        finkiQuestions = new ArrayList<>();
        String[] finkiFAQ = getResources().getStringArray(R.array.finkiFAQ);

        for (String question: finkiFAQ){
            finkiQuestions.add(new Question(question));
        }

        redditQuestions = new ArrayList<>();
        String[] redditFAQ = getResources().getStringArray(R.array.redditFAQ);

        for (String question: redditFAQ){
            redditQuestions.add(new Question(question));
        }

    }

    private void initRecyclers() {
        finkiRecyclerView = getActivity().findViewById(R.id.finkiRecyclerView);
        finkiRecyclerView.setHasFixedSize(true);
        finkiRecyclerAdapter = new RecyclerAdapter(getContext(), onQuestionSelectedListener, finkiQuestions);
        finkiLayoutManager = new LinearLayoutManager(getContext());
        finkiRecyclerView.setAdapter(finkiRecyclerAdapter);
        finkiRecyclerView.setLayoutManager(finkiLayoutManager);

        redditRecyclerView = getActivity().findViewById(R.id.redditRecyclerView);
        redditRecyclerView.setHasFixedSize(true);
        redditRecyclerAdapter = new RecyclerAdapter(getContext(), onQuestionSelectedListener, redditQuestions);
        redditLayoutManager = new LinearLayoutManager(getContext());
        redditRecyclerView.setAdapter(redditRecyclerAdapter);
        redditRecyclerView.setLayoutManager(redditLayoutManager);
    }
}