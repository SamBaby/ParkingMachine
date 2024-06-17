package pages;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.machine.R;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_carrier, container, false);
        EditText editText = root.findViewById(R.id.edittext_carrier);
        if (getActivity() != null) {
            viewPager = getActivity().findViewById(R.id.view_pager);
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            TextView title = root.findViewById(R.id.text_title);
            title.setText(viewModel.getLotName());
            Button previousBtn = root.findViewById(R.id.button_previous);
            previousBtn.setOnClickListener(v -> viewPager.setCurrentItem(4));
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
                    if (!s.toString().isEmpty()) {
                        String id = s.toString();
                        if (id.lastIndexOf("/") >= 0) {
                            id = id.substring(id.lastIndexOf("/"));
                            editText.setText(id);
                        }
                        Boolean check = checkCarrierId(id);
                        if (check) {
                            ECPayData data = Util.getECPayData();
                            Var<String> number = new Var<>("");
                            String invoice = EcpayFunction.invoiceIssueOffline(data.getTest() == 1, getActivity(), viewModel.getInvoiceConnector(), viewModel.getInvoiceCxt(),
                                    data.getMerchantID(), data.getMachineID(), null, id, viewModel.getTotalMoney().getValue(), data.getHashKey(), data.getHashIV());
                            CarInside car = viewModel.getSelectedCars().getValue();
                            if (invoice != null) {
                                number.set(invoice);
                            }
                            new Thread(() -> {
                                ApacheServerRequest.setCarInsidePay(car.getCar_number(), viewModel.getPayTime().getValue(), viewModel.getTotalMoney().getValue(),
                                        viewModel.getDiscountMoney().getValue(), number.get(), viewModel.getPayment());
                                ApacheServerRequest.addPayHistory(car.getCar_number(), car.getTime_in(), viewModel.getPayTime().getValue(),
                                        viewModel.getTotalMoney().getValue(), number.get(), viewModel.getPayment());
                            }).start();
                            editText.setText("");
                            viewModel.setSelectedCars(null);
                            viewPager.setCurrentItem(6);
                            // Schedule to change the page to index 0 after 10 seconds
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewPager.setCurrentItem(0);
                                }
                            }, 5000); // 10000 milliseconds = 10 seconds
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.carrier_id_not_found) + ":" + id, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        return root;
    }

    private Boolean checkCarrierId(String id) {
        ECPayData data = Util.getECPayData();
        return EcpayFunction.barcodeCheck(data.getTest() == 1, data.getMachineID(), data.getHashKey(), data.getHashIV(), id);
    }
}