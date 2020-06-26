package com.example.location;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Created by gurleensethi on 15/01/18.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {
    RecyclerView recyclerView;
    String s1[];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView=(RecyclerView)getView().findViewById(R.id.recyclerView);
        s1=getResources().getStringArray(R.array.stops);

        MyRecyclerViewAdapter myRecyclerViewAdapter=new MyRecyclerViewAdapter(s1);
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        //Set the custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(view);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
    }

}
