package pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.machine.R;
import com.example.machine.MainActivity;
import com.example.machine.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ECPayChooseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ECPayChooseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainViewModel viewModel;
    private TextView countdownText;

    public ECPayChooseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ECPayChooseFragment newInstance(String param1, String param2) {
        ECPayChooseFragment fragment = new ECPayChooseFragment();
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
        View root = inflater.inflate(R.layout.fragment_ecpay_choose, container, false);
        countdownText = root.findViewById(R.id.countdown_text);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), this::setCountdownView);
        }
        Button btnLinePay = root.findViewById(R.id.button_linepay);
        btnLinePay.setOnClickListener(v->{
            //choose line-pay and go to line-pay page
            ((MainActivity) getActivity()).goToPage(8, 0, 50);
        });
        return root;
    }


    private void setCountdownView(Integer integer) {
        countdownText.setText(String.valueOf(integer));
    }
}