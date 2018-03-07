package com.zafirstojanovski.morty.FAQRecyclerView;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zafirstojanovski.morty.Fragments.FAQFragment;
import com.zafirstojanovski.morty.R;

import java.util.ArrayList;

/**
 * Created by Zafir Stojanovski on 3/6/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{

    private ArrayList<Question> questions;
    private FAQFragment.OnQuestionSelectedListener listener;

    public RecyclerAdapter(FAQFragment.OnQuestionSelectedListener listener, ArrayList<Question> questions) {
        this.questions = questions;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item_layout, parent, false);
        return new RecyclerViewHolder(listener, itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.questionTextView.setText(question.getQuestion());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView questionTextView;
        FAQFragment.OnQuestionSelectedListener listener;

        public RecyclerViewHolder(FAQFragment.OnQuestionSelectedListener listener, View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            this.listener = listener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.onQuestionSelected(questionTextView.getText().toString());
        }
    }
}