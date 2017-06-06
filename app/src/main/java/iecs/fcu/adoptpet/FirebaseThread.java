package iecs.fcu.adoptpet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.style.MaskFilterSpan;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Transaction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/6/6.
 */

public class FirebaseThread extends Thread {

    private DataSnapshot dataSnapshot;
    private HotelArrayAdapter adapter = null;
    private static final int LIST_HOTELS = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_HOTELS: {
                    List<Hotel> hotels = (List<Hotel>)msg.obj;
                    refreshHotelList(hotels);
                    break;
                }
            }
        }
    };
    private void refreshHotelList(List<Hotel> hotels) {
        adapter.clear();
        adapter.addAll(hotels);

    }


    public FirebaseThread(DataSnapshot dataSnapshot, HotelArrayAdapter adapter) {
        this.dataSnapshot = dataSnapshot;
        this.adapter = adapter;
    }

    public void run() {
        List<Hotel> lsHotels = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            DataSnapshot dsSAdd = ds.child("Add");
            DataSnapshot dsSName = ds.child("Name");

            String add = (String) dsSAdd.getValue();
            String name = (String) dsSName.getValue();

            DataSnapshot dsImg = ds.child("Picture1");
            String imgUrl = (String) dsImg.getValue();
            Bitmap hotelImg = getImgBitmap(imgUrl);

            Hotel aHotel = new Hotel();
            aHotel.setAddress(add);
            aHotel.setName(name);
            aHotel.setImgUrl(hotelImg);
            lsHotels.add(aHotel);
            Log.v("AdoptPet", add + ";" + name);

            Message msg = new Message();
            msg.what = LIST_HOTELS;
            msg.obj = lsHotels;
            handler.sendMessage(msg);

        }
    }

    private Bitmap getImgBitmap(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            Bitmap bm = BitmapFactory.decodeStream(
                    url.openConnection().getInputStream());
            return bm;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
