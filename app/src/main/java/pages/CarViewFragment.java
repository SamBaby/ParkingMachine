package pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.example.machine.R;

import java.util.Vector;

import datamodel.CarInside;
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
                url = url.replace(" ", "+");
                byte[] decodedBytes = Util.getBase64Decode(url);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                image.setImageBitmap(bitmap);
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
                    viewPager.setCurrentItem(2, true);
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
                url = url.replace(" ", "+");
                byte[] decodedBytes = Util.getBase64Decode(url);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                image.setImageBitmap(bitmap);
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
                    viewPager.setCurrentItem(2, true);
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
                url = url.replace(" ", "+");
                byte[] decodedBytes = Util.getBase64Decode(url);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                image.setImageBitmap(bitmap);
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
                    viewPager.setCurrentItem(2, true);
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
                url = url.replace(" ", "+");
                byte[] decodedBytes = Util.getBase64Decode(url);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                image.setImageBitmap(bitmap);
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
                    viewPager.setCurrentItem(2, true);
                }
            });
        } else {
            image.setImageBitmap(null);
            carNumber.setText("");
            time.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), ""));

            view.setOnClickListener(null);
        }
    }
}