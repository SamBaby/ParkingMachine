package pages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.machine.MainViewModel;
import com.android.machine.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import datamodel.CarInside;
import util.ApacheServerRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView input;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        input = root.findViewById(R.id.edit_car_number);
        input.requestFocus();
        // Hide the soft keyboard
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
        initButtons(root, input);
        Button btnSearch = root.findViewById(R.id.button_search);
        btnSearch.setOnClickListener(v -> {
            String number = input.getText().toString();
            if (!number.isEmpty()) {
                Thread t = new Thread(() -> {
                    String req = ApacheServerRequest.getCarInside(number);
                    if (req != null && !req.isEmpty()) {
                        try {
                            JSONArray array = new JSONArray(req);
                            Vector<CarInside> cars = new Vector<CarInside>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                CarInside car = gson.fromJson(obj.toString(), CarInside.class);
                                cars.add(car);
                            }
                            if (!cars.isEmpty()) {
                                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                                getActivity().runOnUiThread(() -> {
                                    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                                    viewModel.setCars(cars);
                                    input.setText("");
                                    viewPager.setCurrentItem(1, true);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    t.start();
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        ImageView car = root.findViewById(R.id.image_car1);
//        byte[] decodedBytes = android.util.Base64.decode("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",0);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//        car.setImageBitmap(bitmap);
        return root;
    }

    private void initButtons(View view, TextView input) {
        Button button0 = view.findViewById(R.id.button_0);
        button0.setOnClickListener(v -> {
            input.setText(input.getText() + "0");
        });
        Button button1 = view.findViewById(R.id.button_1);
        button1.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "1");
        });
        Button button2 = view.findViewById(R.id.button_2);
        button2.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "2");
        });
        Button button3 = view.findViewById(R.id.button_3);
        button3.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "3");
        });
        Button button4 = view.findViewById(R.id.button_4);
        button4.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "4");
        });
        Button button5 = view.findViewById(R.id.button_5);
        button5.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "5");
        });
        Button button6 = view.findViewById(R.id.button_6);
        button6.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "6");
        });
        Button button7 = view.findViewById(R.id.button_7);
        button7.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "7");
        });
        Button button8 = view.findViewById(R.id.button_8);
        button8.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "8");
        });
        Button button9 = view.findViewById(R.id.button_9);
        button9.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "9");
        });
        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                input.setText(text.substring(0, text.length() - 1));
            }
        });
        Button buttonClear = view.findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(v -> {
            input.setText("");
        });
    }
}