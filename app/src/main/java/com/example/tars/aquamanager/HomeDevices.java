package com.example.tars.aquamanager;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageButton;
        import android.widget.TableLayout;
        import android.widget.Toast;

public class HomeDevices extends Fragment {

    public static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_devices, container, false);

        ImageButton add_device_btn = (ImageButton) view.findViewById(R.id.add_device_ibtn);

        add_device_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddDevice.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh_device_table(getContext(), view);
    }

    public static void refresh_device_table(Context context, View view) {
        TableLayout device_table = (TableLayout) view.findViewById(R.id.dev_tl);
        device_table.removeAllViews();
        if (!AquaUtil.populateDeviceRows(context, device_table, "MainMap")) Toast.makeText(context, "Parse Error", Toast.LENGTH_SHORT).show();
    }
}
