package pages;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.machine.R;
import com.example.machine.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EndingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView textTime;

    public EndingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EndingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EndingFragment newInstance(String param1, String param2) {
        EndingFragment fragment = new EndingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_ending, container, false);
        if (getActivity() != null) {
            TextView title = root.findViewById(R.id.text_title);
            MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            title.setText(viewModel.getLotName());
            textTime = root.findViewById(R.id.text_time);
            viewModel.getExitCountTime().observe(getViewLifecycleOwner(), this::setTime);
        }
        setNoneEditText(root);
        return root;
    }

    private void setNoneEditText(View root) {
        EditText txt = root.findViewById(R.id.edit_none);
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                s.clear();
            }
        });
    }

    private void setTime(Integer time) {
        textTime.setText(getString(R.string.exit_desc1) + time + getString(R.string.exit_desc2));
    }
}