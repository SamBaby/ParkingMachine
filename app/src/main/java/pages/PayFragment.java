package pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.machine.MainViewModel;
import com.android.machine.R;
import com.ftdi.j2xx.FT_Device;

import datamodel.CarInside;
import event.Var;
import util.ApacheServerRequest;
import util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FT_Device coinInputDevice;
    private FT_Device paperInputDevice;
    private FT_Device coin5Device;
    private FT_Device coin10Device;
    private FT_Device coin50Device;
    private boolean readCoinInput = false;
    private boolean readPaperInput = false;
    private int shouldPay = 0;
    private int pay = 0;
    public PayFragment() {
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
    public static PayFragment newInstance(String param1, String param2) {
        PayFragment fragment = new PayFragment();
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
        View root = inflater.inflate(R.layout.fragment_pay, container, false);
        if (getActivity() != null) {
            MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            viewModel.getSelectedCars().observe(getViewLifecycleOwner(), this::setCarView);
            viewModel.getTotalMoney().observe(getViewLifecycleOwner(), this::setTotal);
            viewModel.getDiscountMoney().observe(getViewLifecycleOwner(), this::setDiscount);
            viewModel.getShouldPayMoney().observe(getViewLifecycleOwner(), this::setShouldPay);
            viewModel.getPayTime().observe(getViewLifecycleOwner(), this::setPayTime);
            viewModel.getPayWay().observe(getViewLifecycleOwner(), this::startPay);

            coinInputDevice = viewModel.getCoinInputDevice().getValue();
            paperInputDevice = viewModel.getPaperInputDevice().getValue();
            coin5Device = viewModel.getCoin5Device().getValue();
            coin10Device = viewModel.getCoin10Device().getValue();
            coin50Device = viewModel.getCoin50Device().getValue();
        }
        return root;
    }

    private void setCarView(CarInside car) {
        if (car != null && getView() != null) {
            ImageView image = getView().findViewById(R.id.image_car);
            TextView carNumber = getView().findViewById(R.id.car_number);
            TextView timeIn = getView().findViewById(R.id.time_in);
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
            timeIn.setText(String.format("%s %s", getResources().getString(R.string.entrance_time), car.getTime_in()));
        }
    }

    private void setTotal(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.total);
            text.setText(String.valueOf(amount));
        }

    }

    private void setShouldPay(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.should_pay);
            text.setText(String.valueOf(amount));
        }
        shouldPay = amount;
    }

    private void setDiscount(int amount) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.discount);
            text.setText(String.valueOf(amount));
        }
    }

    private void setPayTime(String time) {
        if (getView() != null) {
            TextView text = getView().findViewById(R.id.time_out);
            text.setText(time);
        }
    }

    private void startPay(int payWay) {
        pay = 0;
        switch(payWay){
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
    private Bitmap getPictureByPath(String path){
        Var<Bitmap> bitmap = new Var<>();
        Thread t = new Thread(()->{
            try {
                String base = ApacheServerRequest.getBase64Picture(path);
                if(base != null){
                    byte[] bytes = Util.getBase64Decode(base);
                    bitmap.set(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap.get();
    }
}