package pages;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.machine.R;
import com.example.machine.MainActivity;
import com.example.machine.MainViewModel;

import datamodel.CarInside;
import datamodel.ECPayData;
import ecpay.EcpayFunction;
import event.Var;
import util.ApacheServerRequest;
import util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarrierFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarrierFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager viewPager;
    MainViewModel viewModel;
    private Handler handler = new Handler();
    private ProgressBar progressBar;
    private EditText editText;
    private boolean isResettingKeyboard = false;
    private TextView countdownText;

    public CarrierFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarrierFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarrierFragment newInstance(String param1, String param2) {
        CarrierFragment fragment = new CarrierFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_carrier, container, false);
        editText = root.findViewById(R.id.edittext_carrier);
        progressBar = root.findViewById(R.id.progressBar);
        countdownText = root.findViewById(R.id.countdown_text);
        if (getActivity() != null) {
            viewPager = getActivity().findViewById(R.id.view_pager);
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), this::setCountdownView);
            TextView title = root.findViewById(R.id.text_title);
            title.setText(viewModel.getLotName());
            Button previousBtn = root.findViewById(R.id.button_previous);
            previousBtn.setOnClickListener(v -> {
                ((MainActivity) getActivity()).goToPage(4, 0, 30);
            });
            Button btnClear = root.findViewById(R.id.button_clear);
            btnClear.setOnClickListener(v -> {
                editText.setText("");
            });
            editText.requestFocus();
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (issueing) {
                        s.clear();
                        return;
                    }
                    if (!s.toString().startsWith("／") && !s.toString().startsWith("/")) {
                        s.clear();
                        return;
                    }
                    if (!s.toString().isEmpty() && s.toString().length() == 8) {
                        String id = s.toString().replaceAll("／", "/");
                        if (id.lastIndexOf("/") >= 0 && id.length() == 8) {
                            new BackgroundTask(id).execute();
                        }
                        s.clear();
                    }
                }
            });
        }
        return root;
    }

    private void setCountdownView(Integer integer) {
        countdownText.setText(String.valueOf(integer));
        if (integer == 1 && ((MainActivity) getActivity()).getCurrentPage() == 5) {
            ((MainActivity) getActivity()).cancelCountdown();
            new printTask().execute();
        }
    }

    private Boolean checkCarrierId(String id) {
        ECPayData data = Util.getECPayData();
        return EcpayFunction.barcodeCheck(data.getTest() == 1, data.getMerchantID(), data.getHashKey(), data.getHashIV(), id);
    }

    private boolean issueing = false;

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private String id;

        public BackgroundTask(String id) {
            this.id = id;
        }

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
            issueing = true;
            Boolean check = checkCarrierId(id);
            if (check) {
                ECPayData data = Util.getECPayData();
                Var<String> number = new Var<>("");
                CarInside car = viewModel.getSelectedCars().getValue();
                String invoice = EcpayFunction.invoiceIssue(data.getTest() == 1, getActivity(), viewModel.getInvoiceConnector(), viewModel.getInvoiceCxt(), data.getMerchantID(), null, id, viewModel.getTotalMoney().getValue(), data.getHashKey(), data.getHashIV(), car.getTime_in());
                if (invoice != null && !invoice.isEmpty()) {
                    number.set(invoice);
                } else {
                    getActivity().runOnUiThread(() -> Util.showWarningDialog(getContext(), getString(R.string.internet_error)));
                }
                new Thread(() -> {
                    String payTime = Util.getServerTime();
                    ApacheServerRequest.setCarInsidePay(car.getCar_number(), payTime, viewModel.getTotalMoney().getValue(), viewModel.getDiscountMoney().getValue(), number.get(), viewModel.getPayment());
                    ApacheServerRequest.addPayHistory(car.getCar_number(), car.getTime_in(), payTime, viewModel.getTotalMoney().getValue(), number.get(), viewModel.getPayment());
                }).start();
                viewModel.setSelectedCars(null);
                ((MainActivity) getActivity()).goToPage(6, 0, 5);
            } else {
                Util.showWarningDialog(getContext(), getString(R.string.carrier_id_not_found) + ":" + id);
            }
            issueing = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Hide the progress bar after completing the background task
            progressBar.setVisibility(View.GONE);
        }
    }

    private class printTask extends AsyncTask<Void, Void, Void> {

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
            ECPayData data = Util.getECPayData();
            if (data != null) {
                CarInside car = viewModel.getSelectedCars().getValue();
                Var<String> number = new Var<>("");
                if (viewModel.getInvoiceConnector() != null && viewModel.getInvoiceCxt() != null) {
                    try {
                        String invoice = EcpayFunction.invoiceIssue(data.getTest() == 1, getActivity(), viewModel.getInvoiceConnector(), viewModel.getInvoiceCxt(),
                                data.getMerchantID(), "", "", viewModel.getTotalMoney().getValue(), data.getHashKey(), data.getHashIV(), car.getTime_in());
                        if (invoice != null && !invoice.isEmpty()) {
                            number.set(invoice);
                        } else {
                            getActivity().runOnUiThread(() -> Util.showWarningDialog(getContext(), getString(R.string.internet_error)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(() -> Util.showWarningDialog(getContext(), getString(R.string.print_broken)));
                }
                new Thread(() -> {
                    String payTime = Util.getServerTime();
                    ApacheServerRequest.setCarInsidePay(car.getCar_number(), payTime, viewModel.getTotalMoney().getValue(),
                            viewModel.getDiscountMoney().getValue(), number.get(), viewModel.getPayment());
                    ApacheServerRequest.addPayHistory(car.getCar_number(), car.getTime_in(), payTime,
                            viewModel.getTotalMoney().getValue(), number.get(), viewModel.getPayment());
                }).start();
                viewModel.setSelectedCars(null);
                viewModel.setExitCountTime();
                ((MainActivity) getActivity()).goToPage(6, 0, 5);
            }
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