package pages;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.machine.MainViewModel;
import com.android.machine.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import datamodel.BasicFee;
import datamodel.CarInside;
import datamodel.RegularPass;
import event.Var;
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
    private MainViewModel viewModel;
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

            TextView title = root.findViewById(R.id.text_title);
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            title.setText(viewModel.getLotName());
        }
        initButtons(root, input);
        Button btnSearch = root.findViewById(R.id.button_search);
        btnSearch.setOnClickListener(v -> {
            Var<Boolean> found = new Var<>(false);
            String number = input.getText().toString();
            if (!number.isEmpty() && number.length() >= 2) {
                Thread t = new Thread(() -> {
                    String req = ApacheServerRequest.getCarInside(number);
                    if (req != null && !req.isEmpty()) {
                        try {
                            JSONArray array = new JSONArray(req);
                            Vector<CarInside> cars = new Vector<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                CarInside car = gson.fromJson(obj.toString(), CarInside.class);
                                if (checkShouldPay(car) && !isRegularCar(car.getCar_number())) {
                                    cars.add(car);
                                }
                            }
                            if (!cars.isEmpty()) {
                                found.set(true);
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
                    if (!found.get()) {
                        Toast.makeText(getActivity(), getString(R.string.car_id_not_found), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getActivity(), getString(R.string.car_number_over_two), Toast.LENGTH_SHORT).show();
            }
        });
        setNoneEditText(root);
        return root;
    }
    private void setNoneEditText(View root){
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
    private boolean checkShouldPay(CarInside car) {
        if (car.getTime_pay() == null || car.getTime_pay().isEmpty()) {
            return true;
        }
        BasicFee basicFee = getBasicFee();
        if (basicFee != null) {
            try {
                int unit = basicFee.getEnter_time_not_count() * 60;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
                Date nowDate = new Date();
                Date payDate = format.parse(car.getTime_pay());
                return (nowDate.getTime() - payDate.getTime()) / 1000 > unit;
            } catch (Exception e) {
                return true;
            }
        }
        return true;

    }

    private BasicFee getBasicFee() {
        Var<BasicFee> basicFee = new Var<>(null);
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getBasicFee();
            if (json != null) {
                try {
                    JSONArray array = new JSONArray(json);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        BasicFee fee = gson.fromJson(obj.toString(), BasicFee.class);
                        basicFee.set(fee);
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
        return basicFee.get();
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

    private boolean isRegularCar(String carNumber) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getRegularCar(carNumber);
            try {
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    JSONObject obj = array.getJSONObject(0);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    RegularPass pass = gson.fromJson(obj.toString(), RegularPass.class);
                    String dueDate = pass.getDue_date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                    Date date = formatter.parse(dueDate);
                    Date now = new Date();
                    if (date != null && date.getTime() >= now.getTime()) {
                        ret.set(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }
}