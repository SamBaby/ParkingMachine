package pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.machine.R;
import com.example.machine.MainActivity;
import com.example.machine.MainViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import datamodel.CarInside;
import datamodel.LinePay;
import ecpay.EcpayFunction;
import event.Var;
import util.ApacheServerRequest;
import util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LinePayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinePayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LinePayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LinePayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LinePayFragment newInstance(String param1, String param2) {
        LinePayFragment fragment = new LinePayFragment();
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
    private TextView countdownText;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_line_pay, container, false);
        countdownText = root.findViewById(R.id.countdown_text);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getSelectedCars().observe(getViewLifecycleOwner(), this::setCarView);
            viewModel.getTotalMoney().observe(getViewLifecycleOwner(), this::setTotal);
            viewModel.getDiscountMoney().observe(getViewLifecycleOwner(), this::setDiscount);
            viewModel.getShouldPayMoney().observe(getViewLifecycleOwner(), this::setShouldPay);
            viewModel.getPayTime().observe(getViewLifecycleOwner(), this::setPayTime);
            viewModel.getPayWay().observe(getViewLifecycleOwner(), this::startPay);
            viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), this::setCountdownView);
            Button cancelBtn = root.findViewById(R.id.cancel_button);
            cancelBtn.setOnClickListener(v -> {
                //cancel and go to home page
                viewModel.setSelectedCars(null);
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
        txt.requestFocus();
        countdownText.setText(String.valueOf(integer));
    }

    EditText txt;
    private boolean issuing = false;

    private void setNoneEditText(View root) {
        txt = root.findViewById(R.id.edit_none);
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //check line pay code
                if (s.length() > 0) {
                    char lastCharacter = s.charAt(s.length() - 1);

                    if (lastCharacter == '\n') {
                        String barcode = s.subSequence(0, s.length() - 1).toString();
                        txt.setText("");
                        if (!issuing) {
                            new BackgroundTask(barcode).execute();
                        }
                    }
                }
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
        final String barCode;

        public BackgroundTask(String barCode) {
            this.barCode = barCode;
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
            issuing = true;
            ((MainActivity) getActivity()).cancelCountdown();
            boolean success = false;
            Var<String> postRet = new Var<>("");
            Thread t = new Thread(() -> {
                int index = 0;
                while (index < 3 && postRet.get().isEmpty()) {
                    String ret = linePayPost(1, barCode);
                    if (!ret.isEmpty()) {
                        postRet.set(ret);
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    index++;
                }
            });
            try {
                t.start();
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!postRet.get().isEmpty()) {
                    JSONObject obj = new JSONObject(postRet.get());
                    if (obj.has("returnCode")) {
                        int returnCode = obj.getInt("returnCode");
                        if (returnCode == 0) {
                            success = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (success) {
                ((MainActivity) getActivity()).goToPage(4, 0, 30);
            } else {
                getActivity().runOnUiThread(() -> {
                    Util.showWarningDialog(getContext(), getString(R.string.line_pay_error));
                    ((MainActivity) getActivity()).resetCountdown(50, 0);
                });
            }
            issuing = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Hide the progress bar after completing the background task
            progressBar.setVisibility(View.GONE);
        }
    }

    private String sendLinePayRequest(String code) {
        Var<String> ret = new Var<>("");

        return ret.get();
    }

    /***
     *
     * @param amount money that user pays
     * @param oneTimeID line pay QR Code
     * @return POST return message
     */
    public static synchronized String linePayPost(int amount, String oneTimeID) {
        try {
            LinePay data = getLinePayData();
            if (data != null) {
                //line Pay API url
                //official:https://api-pay.line.me/v2/payments/oneTimeKeys/pay
                //test: https://sandbox-api-pay.line.me/v2/payments/oneTimeKeys/pay
                String url = data.getTest() == 1 ? "https://sandbox-api-pay.line.me/v2/payments/oneTimeKeys/pay" : "https://api-pay.line.me/v2/payments/oneTimeKeys/pay";
                URL urlPass = new URL(url);

                // Create connection
                HttpURLConnection conn = (HttpURLConnection) urlPass.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-LINE-ChannelId", data.getChannelId());
                conn.setRequestProperty("X-LINE-ChannelSecret", data.getChannelSecret());
                conn.setDoOutput(true);
                String content = "{\"productName\": \"停車費\",\n" +
                        "          \"amount\": " + amount + ",\n" +
                        "          \"currency\": \"TWD\",\n" +
                        "          \"orderId\": \"" + EcpayFunction.genUnixTimeStamp() + "\",\n" +
                        "          \"oneTimeKey\": \"" + oneTimeID + "\"}";
                // Write parameters to output stream
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] postDataBytes = content.getBytes("UTF-8");
                    os.write(postDataBytes);
                }

                int responseCode = conn.getResponseCode();
                // Read response
                // TODO: Handle response here
                // 读取响应内容
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                // Close connection
                conn.disconnect();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static LinePay getLinePayData() {
        Var<LinePay> linePayData = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getLinePay();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < 1; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        LinePay linePay = gson.fromJson(obj.toString(), LinePay.class);
                        linePayData.set(linePay);
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

        return linePayData.get();
    }
}