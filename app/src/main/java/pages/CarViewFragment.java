package pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import datamodel.BasicFee;
import datamodel.CarInside;
import datamodel.DayHoliday;
import datamodel.RegularPass;
import event.Var;
import util.ApacheServerRequest;
import util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int index = 0;
    private Vector<CarInside> cars;

    public CarViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarViewFragment newInstance(String param1, String param2) {
        CarViewFragment fragment = new CarViewFragment();
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
        View root = inflater.inflate(R.layout.fragment_car_view, container, false);
        Button btnCancel = root.findViewById(R.id.button_cancel);
        Button btnPrevious = root.findViewById(R.id.button_previous);
        Button btnNext = root.findViewById(R.id.button_next);
        if (getActivity() != null) {
            ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
            btnCancel.setOnClickListener(v -> {
                viewPager.setCurrentItem(0, true);
            });
            btnPrevious.setOnClickListener(v -> {
                if (cars != null && index > 0) {
                    index--;
                    refreshCarView();
                }
            });
            btnNext.setOnClickListener(v -> {
                if (cars != null && ((index + 1) * 4 < cars.size())) {
                    index++;
                    refreshCarView();
                }
            });
            MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getCars().observe(getViewLifecycleOwner(), this::setCarView);

            TextView title = root.findViewById(R.id.text_title);
            title.setText(viewModel.getLotName());
        }
        return root;
    }

    private void setCarView(Vector<CarInside> cars) {
        index = 0;
        this.cars = cars;
        refreshCarView();
    }

    private void refreshCarView() {
        if (cars != null && !cars.isEmpty()) {
            for (int i = index * 4; i < index * 4 + 4; i++) {
                if (i < cars.size()) {
                    CarInside car = cars.get(i);
                    switch (i % 4) {
                        case 0:
                            setCar1(car);
                            break;
                        case 1:
                            setCar2(car);
                            break;
                        case 2:
                            setCar3(car);
                            break;
                        case 3:
                            setCar4(car);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (i % 4) {
                        case 0:
                            setCar1(null);
                            break;
                        case 1:
                            setCar2(null);
                            break;
                        case 2:
                            setCar3(null);
                            break;
                        case 3:
                            setCar4(null);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private Handler handler = new Handler();

    private void setCar1(CarInside car) {
        if (getView() == null) {
            return;
        }
        ImageView image = getView().findViewById(R.id.image_car1);
        TextView carNumber = getView().findViewById(R.id.car_number1);
        TextView time = getView().findViewById(R.id.car_time_1);
        LinearLayout view = getView().findViewById(R.id.car_view1);
        if (car != null) {
            try {
                String url = car.getPicture_url();
                image.setImageBitmap(getPictureByPath(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            carNumber.setText(car.getCar_number());
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_in()));

            view.setOnClickListener(v -> {
                if (getActivity() != null) {
                    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                    viewModel.setSelectedCars(car);
                    ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                    if (setSelectedCar(car)) {//shouldPay >0
                        viewPager.setCurrentItem(2, true);
                    } else {
                        viewModel.setSelectedCars(null);
                        viewPager.setCurrentItem(6);
                        // Schedule to change the page to index 0 after 10 seconds
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(0);
                            }
                        }, 5000); // 10000 milliseconds = 10 seconds
                    }
                }
            });
        } else {
            image.setImageBitmap(null);
            carNumber.setText("");
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), ""));

            view.setOnClickListener(null);
        }

    }

    private void setCar2(CarInside car) {
        if (getView() == null) {
            return;
        }
        ImageView image = getView().findViewById(R.id.image_car2);
        TextView carNumber = getView().findViewById(R.id.car_number2);
        TextView time = getView().findViewById(R.id.car_time_2);
        LinearLayout view = getView().findViewById(R.id.car_view2);
        if (car != null) {
            try {
                String url = car.getPicture_url();
                image.setImageBitmap(getPictureByPath(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            carNumber.setText(car.getCar_number());
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_in()));

            view.setOnClickListener(v -> {
                if (getActivity() != null) {
                    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                    viewModel.setSelectedCars(car);
                    setSelectedCar(car);
                    ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                    if (setSelectedCar(car)) {//shouldPay >0
                        viewPager.setCurrentItem(2, true);
                    } else {
                        viewModel.setSelectedCars(null);
                        viewPager.setCurrentItem(6);
                        // Schedule to change the page to index 0 after 10 seconds
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(0);
                            }
                        }, 5000); // 10000 milliseconds = 10 seconds
                    }
                }
            });
        } else {
            image.setImageBitmap(null);
            carNumber.setText("");
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), ""));

            view.setOnClickListener(null);
        }
    }

    private void setCar3(CarInside car) {
        if (getView() == null) {
            return;
        }
        ImageView image = getView().findViewById(R.id.image_car3);
        TextView carNumber = getView().findViewById(R.id.car_number3);
        TextView time = getView().findViewById(R.id.car_time_3);
        LinearLayout view = getView().findViewById(R.id.car_view3);
        if (car != null) {
            try {
                String url = car.getPicture_url();
                image.setImageBitmap(getPictureByPath(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            carNumber.setText(car.getCar_number());
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_in()));

            view.setOnClickListener(v -> {
                if (getActivity() != null) {
                    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                    viewModel.setSelectedCars(car);
                    setSelectedCar(car);
                    ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                    if (setSelectedCar(car)) {//shouldPay >0
                        viewPager.setCurrentItem(2, true);
                    } else {
                        viewModel.setSelectedCars(null);
                        viewPager.setCurrentItem(6);
                        // Schedule to change the page to index 0 after 10 seconds
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(0);
                            }
                        }, 5000); // 10000 milliseconds = 10 seconds
                    }
                }
            });
        } else {
            image.setImageBitmap(null);
            carNumber.setText("");
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), ""));

            view.setOnClickListener(null);
        }
    }

    private void setCar4(CarInside car) {
        if (getView() == null) {
            return;
        }
        ImageView image = getView().findViewById(R.id.image_car4);
        TextView carNumber = getView().findViewById(R.id.car_number4);
        TextView time = getView().findViewById(R.id.car_time_4);
        LinearLayout view = getView().findViewById(R.id.car_view4);
        if (car != null) {
            try {
                String url = car.getPicture_url();
                image.setImageBitmap(getPictureByPath(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
            carNumber.setText(car.getCar_number());
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_in()));

            view.setOnClickListener(v -> {
                if (getActivity() != null) {
                    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                    viewModel.setSelectedCars(car);
                    setSelectedCar(car);
                    ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
                    if (setSelectedCar(car)) {//shouldPay >0
                        viewPager.setCurrentItem(2, true);
                    } else {
                        viewModel.setSelectedCars(null);
                        viewPager.setCurrentItem(6);
                        // Schedule to change the page to index 0 after 10 seconds
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(0);
                            }
                        }, 5000); // 10000 milliseconds = 10 seconds
                    }
                }
            });
        } else {
            image.setImageBitmap(null);
            carNumber.setText("");
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), ""));

            view.setOnClickListener(null);
        }
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
            ret.put(2, holiday.get().getMonday() == 1);
            ret.put(3, holiday.get().getTuesday() == 1);
            ret.put(4, holiday.get().getWednesday() == 1);
            ret.put(5, holiday.get().getThursday() == 1);
            ret.put(6, holiday.get().getFriday() == 1);
            ret.put(7, holiday.get().getSaturday() == 1);
            ret.put(1, holiday.get().getSunday() == 1);
        }
        return ret;
    }

    /***
     * calculate total parking fee
     * @param startDate enter time
     * @param endDate pay time
     * @return total fee of the car
     */
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
        if ((durationInMillis / 1000) <= 0) {
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
        int unitMinute = 360;
        if (basicFee.getAfter_one_hour_unit() == 0) {
            unitMinute = 30;
        } else {
            unitMinute = 60 * basicFee.getAfter_one_hour_unit();
        }
        long duration = durationOfTheDayInMillis / (1000L * unitMinute * 60) + (durationOfTheDayInMillis % (1000L * unitMinute * 60) > 0 ? 1 : 0);
        return (int) Math.min(duration * basic, most);
    }

    private boolean checkIfHoliday(Date date, Map<Integer, Boolean> dayHoliday) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (Boolean.TRUE.equals(dayHoliday.get(weekday))) {
            return true;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (checkHoliday(format.format(date))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setSelectedCar(CarInside car) {
        //set time out
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        String nowString = format.format(nowDate);
        //calculate money
        try {
            Date timeInDate = (car.getTime_pay() != null && !car.getTime_pay().isEmpty()) ? format.parse(car.getTime_pay()) : format.parse(car.getTime_in());
            RegularPass pass = getRegularCar(car.getCar_number());
            if (pass != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dueString = pass.getDue_date();
                Date dueDate = dateFormat.parse(dueString);
                if (dueDate != null && timeInDate != null && dueDate.getTime() > timeInDate.getTime()) {
                    timeInDate = dueDate;
                }
            }
            if (timeInDate != null) {
                int totalMoney = calculateFee(timeInDate, nowDate);
                if (getActivity() != null) {
                    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                    viewModel.resetTotalPay();
                    viewModel.setTotalMoney(totalMoney);
                    viewModel.setDiscountMoney(car.getDiscount());
                    viewModel.setPayTime(nowString);
                    int shouldPay = totalMoney - car.getDiscount();
                    viewModel.setShouldPayMoney(Math.max(shouldPay, 0));
                    if (shouldPay > 0) {
                        return true;
                    } else {
                        new Thread(() -> {
                            ApacheServerRequest.setCarInsidePayWithServerTime(car.getCar_number(), viewModel.getTotalMoney().getValue(),
                                    viewModel.getDiscountMoney().getValue(), "", "D");
                        }).start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private RegularPass getRegularCar(String carNumber) {
        Var<RegularPass> ret = new Var<>();
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getRegularCar(carNumber);
            try {
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    JSONObject obj = array.getJSONObject(0);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    RegularPass pass = gson.fromJson(obj.toString(), RegularPass.class);
                    ret.set(pass);
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