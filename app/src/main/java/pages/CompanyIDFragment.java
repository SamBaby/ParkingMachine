package pages;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
 * Use the {@link CompanyIDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompanyIDFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView input;
    ViewPager viewPager;
    MainViewModel viewModel;
    private Handler handler = new Handler();
    private ProgressBar progressBar;

    public CompanyIDFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompanyIDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompanyIDFragment newInstance(String param1, String param2) {
        CompanyIDFragment fragment = new CompanyIDFragment();
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

    private TextView countdownText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_company_i_d, container, false);
        countdownText = root.findViewById(R.id.countdown_text);
        if (getActivity() != null) {
            viewPager = getActivity().findViewById(R.id.view_pager);
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), this::setCountdownView);
            TextView title = root.findViewById(R.id.text_title);
            title.setText(viewModel.getLotName());
        }
        input = root.findViewById(R.id.input_company_id);
        initButtons(root, input);
        progressBar = root.findViewById(R.id.progressBar);
        setNoneEditText(root);
        return root;
    }

    private void setCountdownView(Integer integer) {
        countdownText.setText(String.valueOf(integer));
        if (integer == 1 && ((MainActivity) getActivity()).getCurrentPage() == 4) {
            ((MainActivity) getActivity()).cancelCountdown();
            new printTask().execute();
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

    private Button buttonPrint;
    private Button buttonCarrier;

    private void initButtons(View view, TextView input) {
        Button button0 = view.findViewById(R.id.button_0);
        button0.setOnClickListener(v -> {
            input.setText(input.getText() + "0");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button1 = view.findViewById(R.id.button_1);
        button1.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "1");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button2 = view.findViewById(R.id.button_2);
        button2.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "2");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button3 = view.findViewById(R.id.button_3);
        button3.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "3");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button4 = view.findViewById(R.id.button_4);
        button4.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "4");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button5 = view.findViewById(R.id.button_5);
        button5.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "5");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button6 = view.findViewById(R.id.button_6);
        button6.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "6");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button7 = view.findViewById(R.id.button_7);
        button7.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "7");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button8 = view.findViewById(R.id.button_8);
        button8.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "8");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button button9 = view.findViewById(R.id.button_9);
        button9.setOnClickListener(v -> {
            input.setText(input.getText().toString() + "9");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                input.setText(text.substring(0, text.length() - 1));
            }
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        Button buttonClear = view.findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(v -> {
            input.setText("");
            ((MainActivity) getActivity()).resetCountdown(30, 6);
        });
        buttonCarrier = view.findViewById(R.id.button_carrier);
        buttonCarrier.setOnClickListener(v -> {
            String id = input.getText().toString();
            if (!id.isEmpty()) {
                Util.showWarningDialog(getContext(), getString(R.string.company_carrier_not_same_time));
            } else {
                input.setText("");
                ((MainActivity) getActivity()).goToPage(5, 0, 30);
            }
        });
        buttonPrint = view.findViewById(R.id.button_print);
        buttonPrint.setOnClickListener(v -> {
            new BackgroundTask().execute();
        });
    }

    private boolean checkCompanyID(String id) {
        ECPayData data = Util.getECPayData();
        return EcpayFunction.taxIDCheck(data.getTest() == 1, data.getMerchantID(), data.getHashKey(), data.getHashIV(), id);
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the progress bar before starting the background task
            progressBar.setVisibility(View.VISIBLE);
            buttonPrint.setEnabled(false);
            buttonCarrier.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform the background task here
            // For example, a long-running operation like downloading data or heavy computation
            // Simulating a long-running task with Thread.sleep()
            String id = input.getText().toString();
            ECPayData data = Util.getECPayData();
            if (id.isEmpty()) {
                if (data != null) {
                    ((MainActivity) getActivity()).cancelCountdown();
                    CarInside car = viewModel.getSelectedCars().getValue();
                    Var<String> number = new Var<>("");
                    if (viewModel.getInvoiceConnector() != null && viewModel.getInvoiceCxt() != null) {
                        try {
                            String invoice = EcpayFunction.invoiceIssue(data.getTest() == 1, getActivity(), viewModel.getInvoiceConnector(), viewModel.getInvoiceCxt(), data.getMerchantID(), id, "", viewModel.getTotalMoney().getValue(), data.getHashKey(), data.getHashIV(), car.getTime_in());
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
                        ApacheServerRequest.setCarInsidePay(car.getCar_number(), payTime, viewModel.getTotalMoney().getValue(), viewModel.getDiscountMoney().getValue(), number.get(), viewModel.getPayment());
                        ApacheServerRequest.addPayHistory(car.getCar_number(), car.getTime_in(), payTime, viewModel.getTotalMoney().getValue(), number.get(), viewModel.getPayment());
                    }).start();
                    viewModel.setSelectedCars(null);
                    viewModel.setExitCountTime();
                    ((MainActivity) getActivity()).goToPage(6, 0, 5);
                }
            } else {
                boolean idPass = checkCompanyID(id);
                if (idPass) {
                    if (data != null) {
                        ((MainActivity) getActivity()).cancelCountdown();
                        CarInside car = viewModel.getSelectedCars().getValue();
                        Var<String> number = new Var<>("");
                        if (viewModel.getInvoiceConnector() != null && viewModel.getInvoiceCxt() != null) {
                            try {
                                String invoice = EcpayFunction.invoiceIssue(data.getTest() == 1, getActivity(), viewModel.getInvoiceConnector(), viewModel.getInvoiceCxt(), data.getMerchantID(), id, "", viewModel.getTotalMoney().getValue(), data.getHashKey(), data.getHashIV(), car.getTime_in());
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
                            ApacheServerRequest.setCarInsidePay(car.getCar_number(), payTime, viewModel.getTotalMoney().getValue(), viewModel.getDiscountMoney().getValue(), number.get(), viewModel.getPayment());
                            ApacheServerRequest.addPayHistory(car.getCar_number(), car.getTime_in(), payTime, viewModel.getTotalMoney().getValue(), number.get(), viewModel.getPayment());
                        }).start();
                        getActivity().runOnUiThread(() -> {
                            input.setText("");
                        });
                        viewModel.setSelectedCars(null);
                        viewModel.setExitCountTime();
//                        handler.postDelayed(() -> viewPager.setCurrentItem(6), 0);
//                        // Schedule to change the page to index 0 after 10 seconds
//                        handler.postDelayed(() -> viewPager.setCurrentItem(0), 5000); // 10000 milliseconds = 10 seconds
                        ((MainActivity) getActivity()).goToPage(6, 0, 5);
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        Util.showWarningDialog(getContext(), getString(R.string.company_id_not_found));
                        input.setText("");
                    });
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Hide the progress bar after completing the background task
            progressBar.setVisibility(View.GONE);
            buttonPrint.setEnabled(true);
            buttonCarrier.setEnabled(true);
        }
    }

    private class printTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the progress bar before starting the background task
            progressBar.setVisibility(View.VISIBLE);
            buttonPrint.setEnabled(false);
            buttonCarrier.setEnabled(false);
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
                        String invoice = EcpayFunction.invoiceIssue(data.getTest() == 1, getActivity(), viewModel.getInvoiceConnector(), viewModel.getInvoiceCxt(), data.getMerchantID(), "", "", viewModel.getTotalMoney().getValue(), data.getHashKey(), data.getHashIV(), car.getTime_in());
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
                    ApacheServerRequest.setCarInsidePay(car.getCar_number(), payTime, viewModel.getTotalMoney().getValue(), viewModel.getDiscountMoney().getValue(), number.get(), viewModel.getPayment());
                    ApacheServerRequest.addPayHistory(car.getCar_number(), car.getTime_in(), payTime, viewModel.getTotalMoney().getValue(), number.get(), viewModel.getPayment());
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
            buttonPrint.setEnabled(true);
            buttonCarrier.setEnabled(true);
        }
    }
}