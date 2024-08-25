package pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.machine.R;
import com.example.machine.MainActivity;
import com.example.machine.MainViewModel;

import datamodel.CarInside;
import event.Var;
import util.ApacheServerRequest;
import util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CoinPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoinPayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    public CoinPayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoinPayFragment newInstance(String param1, String param2) {
        CoinPayFragment fragment = new CoinPayFragment();
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

    MainViewModel viewModel;
    private TextView countdownText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coin_pay, container, false);
        countdownText = root.findViewById(R.id.countdown_text);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getSelectedCars().observe(getViewLifecycleOwner(), this::setCarView);
            viewModel.getTotalMoney().observe(getViewLifecycleOwner(), this::setTotal);
            viewModel.getDiscountMoney().observe(getViewLifecycleOwner(), this::setDiscount);
            viewModel.getShouldPayMoney().observe(getViewLifecycleOwner(), this::setShouldPay);
            viewModel.getPayTime().observe(getViewLifecycleOwner(), this::setPayTime);
            viewModel.getPayWay().observe(getViewLifecycleOwner(), this::startPay);
            viewModel.getTotalPay().observe(getViewLifecycleOwner(), this::refreshTotalPay);
            viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), this::setCountdownView);
            Button cancelBtn = root.findViewById(R.id.cancel_button);
            cancelBtn.setOnClickListener(v -> {
                viewModel.setSelectedCars(null);
                boolean success = viewModel.cancelCoinPay();
                if (!success) {
                    Util.showWarningDialog(getContext(), getString(R.string.coin_not_enough));
                }
//                ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
//                viewPager.setCurrentItem(0, true);
                ((MainActivity) getActivity()).goToPage(0, 0, 0);
            });

            TextView title = root.findViewById(R.id.text_title);
            title.setText(viewModel.getLotName());
            progressBar = root.findViewById(R.id.progressBar);
        }
        setNoneEditText(root);
        return root;
    }

    private void setCountdownView(Integer integer) {
        countdownText.setText(String.valueOf(integer));
        if (integer == 45 && ((MainActivity) getActivity()).getCurrentPage() == 3) {
            viewModel.setTotalPay(viewModel.getTotalMoney().getValue() + 115);
        }
        if (integer == 1 && ((MainActivity) getActivity()).getCurrentPage() == 3) {
            ((MainActivity) getActivity()).cancelCountdown();
            viewModel.setSelectedCars(null);
            boolean success = viewModel.cancelCoinPay();
            if (!success) {
                Util.showWarningDialog(getContext(), getString(R.string.coin_not_enough));
            }
            ((MainActivity) getActivity()).goToPage(0, 0, 0);
        }
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
            timeOut.setText(String.format("%s %s", getResources().getString(R.string.exit_time), viewModel.getPayTime()));
        }
    }

    private void refreshTotalPay(Integer integer) {
        if (getView() != null && getActivity() != null) {
            TextView text = getView().findViewById(R.id.text_already_pay);
            text.setText(String.valueOf(integer));
            if (viewModel.isStartCoinPay()) {
                if (integer >= viewModel.getTotalMoney().getValue()) {
                    viewModel.stopCoinPay();
                    new BackgroundTask().execute();
                } else {
                    ((MainActivity) getActivity()).resetCountdown(50, 0);
                }
            }
        }
    }

    private void setTotal(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.total);
            text.setText(String.format("%s:%s", getString(R.string.total_count), String.valueOf(amount)));

            TextView bigText = getView().findViewById(R.id.text_total);
            bigText.setText(String.format(String.valueOf(amount)));
        }

    }

    private void setShouldPay(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.should_pay);
            text.setText(String.format("%s:%s", getString(R.string.end_count), String.valueOf(amount)));
            TextView bigText = getView().findViewById(R.id.text_should_pay);
            bigText.setText(String.format(String.valueOf(amount)));
        }
    }

    private void setDiscount(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.discount);
            text.setText(String.format("%s:%s", getString(R.string.discount_count), String.valueOf(amount)));
        }
    }

    private void setPayTime(String time) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.time_out);
            text.setText(time);
        }
    }

    private void startPay(int payWay) {
        switch (payWay) {
            case 0://Cash
                break;
            case 1://EZ Pay
                break;
            case 2://Line Pay
                break;
            default:
                break;
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

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the progress bar before starting the background task
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform the background task here
            // For example, a long-running operation like downloading data or heavy computation
            // Simulating a long-running task with Thread.sleep()
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
//            handler.postDelayed(() -> viewPager.setCurrentItem(4), 0);
            ((MainActivity) getActivity()).goToPage(4, 0, 30);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Hide the progress bar after completing the background task
            progressBar.setVisibility(View.GONE);
        }
    }
}