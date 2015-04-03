package com.example.blog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/* Page1Activity.java */
public class Page1Activity extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.activity_page1, container, false);

		Button testBtn = (Button) layout.findViewById(R.id.testBtn);
		testBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "첫번째 페이지입니다.", Toast.LENGTH_SHORT).show();
			}
		});
		return layout;
	}
}
