package pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.machine.MainActivity;
import com.example.machine.MainViewModel;
import com.android.machine.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import datamodel.BasicFee;
import datamodel.CarInside;
import datamodel.DayHoliday;
import event.Var;
import util.ApacheServerRequest;
import util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentChooseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentChooseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainViewModel viewModel;

    public PaymentChooseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentChooseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentChooseFragment newInstance(String param1, String param2) {
        PaymentChooseFragment fragment = new PaymentChooseFragment();
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

    private Handler handler = new Handler();
    private TextView countdownText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment_choose, container, false);
        countdownText = root.findViewById(R.id.countdown_text);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getSelectedCars().observe(getViewLifecycleOwner(), this::setCarView);
            viewModel.getTotalMoney().observe(getViewLifecycleOwner(), this::setTotal);
            viewModel.getDiscountMoney().observe(getViewLifecycleOwner(), this::setDiscount);
            viewModel.getShouldPayMoney().observe(getViewLifecycleOwner(), this::setShouldPay);
            viewModel.getPayTime().observe(getViewLifecycleOwner(), this::setTimeOut);
            viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), this::setCountdownView);
            TextView title = root.findViewById(R.id.text_title);
            title.setText(viewModel.getLotName());
            ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
            Button btnCancel = root.findViewById(R.id.button_cancel);
            btnCancel.setOnClickListener(v -> {
                viewModel.setSelectedCars(null);
                ((MainActivity) getActivity()).goToPage(0, 0, 0);
            });

            Button btnCash = root.findViewById(R.id.button_cash);
//            Button btnEZPay = root.findViewById(R.id.button_ezpay);
            Button btnECPay = root.findViewById(R.id.button_ecpay);
            //pay with cash button
            btnCash.setOnClickListener(v -> {
                if (viewModel.getCoinDevice() == null
                        || viewModel.getCoinInputDevice() == null || viewModel.getPaperInputDevice() == null) {
                    viewModel.setSelectedCars(null);
                    Util.showWarningDialog(getContext(), getString(R.string.coin_broken));
                    ((MainActivity) getActivity()).goToPage(0, 0, 0);
                } else {
                    viewModel.setPayWay(0);
                    ((MainActivity) getActivity()).goToPage(3, 0, 50);
                }
            });
//            btnEZPay.setOnClickListener(v -> {
//                viewModel.setPayWay(1);
//                viewPager.setCurrentItem(3, true);
//            });
            //pay with EC-pay button
            btnECPay.setOnClickListener(v -> {
                viewModel.setPayWay(2);
//                viewPager.setCurrentItem(7, false);
                ((MainActivity) getActivity()).goToPage(7, 0, 50);
            });
        }
        setNoneEditText(root);
        return root;
    }

    private void setCountdownView(Integer integer) {
        countdownText.setText(String.valueOf(integer));
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

    private void setCarView(CarInside car) {
        if (car != null && getView() != null) {
            ImageView image = getView().findViewById(R.id.image_car);
            TextView carNumber = getView().findViewById(R.id.car_number);
            TextView timeIn = getView().findViewById(R.id.time_in);
            TextView timeOut = getView().findViewById(R.id.time_out);
            //set image
            try {
                String url = car.getPicture_url();
                image.setImageBitmap(getPictureByPath(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //set car number
            carNumber.setText(car.getCar_number());
            //set time in
            if (car.getTime_pay() != null && !car.getTime_pay().isEmpty()) {
                timeIn.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_pay()));
            } else {
                timeIn.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_in()));
            }
        }
    }

    private void setTimeOut(String timeOut) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.time_out);
            text.setText(String.format("%s %s", getResources().getString(R.string.exit_time), timeOut));
        }
    }

    private void setTotal(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.total);
            text.setText(String.format("%s:%s", getString(R.string.total_count), String.valueOf(amount)));
        }

    }

    private void setShouldPay(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.should_pay);
            text.setText(String.format("%s:%s", getString(R.string.end_count), String.valueOf(amount)));
        }
    }

    private void setDiscount(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.discount);
            text.setText(String.format("%s:%s", getString(R.string.discount_count), String.valueOf(amount)));
        }
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

    private boolean checkHoliday(String date) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getHoliday(date);
            if (json != null) {
                try {
                    JSONArray array = new JSONArray(json);
                    if (array.length() > 0) {
                        ret.set(true);
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
        return ret.get();
    }

    private Map<Integer, Boolean> getDayHoliday() {
        Var<DayHoliday> holiday = new Var<>(null);
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getDayHoliday();
            if (json != null) {
                try {
                    JSONArray array = new JSONArray(json);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        DayHoliday fee = gson.fromJson(obj.toString(), DayHoliday.class);
                        holiday.set(fee);
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
        Map<Integer, Boolean> ret = new HashMap<>();
        if (holiday.get() != null) {
            ret.put(1, holiday.get().getMonday() == 1);
            ret.put(2, holiday.get().getTuesday() == 1);
            ret.put(3, holiday.get().getWednesday() == 1);
            ret.put(4, holiday.get().getThursday() == 1);
            ret.put(5, holiday.get().getFriday() == 1);
            ret.put(6, holiday.get().getSaturday() == 1);
            ret.put(7, holiday.get().getSunday() == 1);
        }
        return ret;
    }

    public int calculateFee(Date startDate, Date endDate) {
        BasicFee basicFee = getBasicFee();
        Map<Integer, Boolean> dayHoliday = getDayHoliday();

        int totalFee = 0;

        // Calculate fee for each day between start and end dates
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MINUTE, basicFee.getEnter_time_not_count());
        //reset startDate with no count minutes
        startDate = calendar.getTime();

        long durationInMillis = endDate.getTime() - startDate.getTime();

        // If duration is negative, no pay
        if ((durationInMillis / (1000 * 60)) <= 0) {
            return 0;
        }

        long totalDurationInDays = Util.daysBetween(startDate, endDate);
        if (totalDurationInDays > 0) {
            for (int i = 0; i <= totalDurationInDays; i++) {
                if (i == 0) {
                    // Calculate end time of the current day
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);
                    Date dayEndTime = calendar.getTime();
                    totalFee += calculateOneDayFee(startDate, dayEndTime, basicFee, dayHoliday);
                } else if (i == totalDurationInDays) {
                    // Calculate start time of the current day
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date dayStartTime = calendar.getTime();
                    totalFee += calculateOneDayFee(dayStartTime, endDate, basicFee, dayHoliday);
                } else {
                    // Calculate start time of the current day
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date dayStartTime = calendar.getTime();
                    // Calculate end time of the current day
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);
                    Date dayEndTime = calendar.getTime();
                    totalFee += calculateOneDayFee(dayStartTime, dayEndTime, basicFee, dayHoliday);
                }
                // Move calendar to the next day
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            totalFee += calculateOneDayFee(startDate, endDate, basicFee, dayHoliday);
        }


        return totalFee;
    }

    private int calculateOneDayFee(Date startDate, Date endDate, BasicFee basicFee, Map<Integer, Boolean> dayHoliday) {
        long durationOfTheDayInMillis = endDate.getTime() - startDate.getTime();
        boolean isHoliday = checkIfHoliday(startDate, dayHoliday);
        int basic = isHoliday ? basicFee.getHoliday_fee() : basicFee.getWeekday_fee();
        int most = isHoliday ? basicFee.getHoliday_most_fee() : basicFee.getWeekday_most_fee();
        int unitMinute = basicFee.getAfter_one_hour_unit() == 0 ? 30 : 60;
        long duration = durationOfTheDayInMillis / (1000L * unitMinute * 60) + (durationOfTheDayInMillis % (1000L * unitMinute * 60) > 0 ? 1 : 0);
        return (int) Math.min(duration * basic, most);
    }

    private boolean checkIfHoliday(Date date, Map<Integer, Boolean> dayHoliday) {
        boolean ret = false;
        Calendar c = Calendar.getInstance();
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (Boolean.TRUE.equals(dayHoliday.get(weekday))) {
            return true;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (checkHoliday(format.format(date))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bitmap getPictureByPath(String path) {
        Var<Bitmap> bitmap = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String base = ApacheServerRequest.getBase64Picture(path);
                if (base != null) {
                    byte[] bytes = Util.getBase64Decode(base);
                    bitmap.set(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
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
        return bitmap.get();
    }
}