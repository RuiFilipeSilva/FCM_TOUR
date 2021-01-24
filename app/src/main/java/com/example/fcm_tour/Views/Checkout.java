package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;

import org.w3c.dom.Text;


public class Checkout extends Fragment {
    Button comeBack, checkout;
    View v;
    EditText nameClient;
    TextView total;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_checkout, container, false);
        //String[] titles = bundle.getStringArray("titles");
        total = v.findViewById(R.id.total);
        total.setText(Preferences.readShoppingCartPrice());
        nameClient = v.findViewById(R.id.nametxt);
        nameClient.setText(Preferences.readUsername());
        comeBack = v.findViewById(R.id.comeBack);
        comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnShoppingCart();
            }
        });

        checkout = v.findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                alert.setTitle(R.string.conf_tittle_pay);
                alert.setMessage(getString(R.string.conf_message_pay));
                alert.setPositiveButton(R.string.confirmOrder, (dialog, whichButton) -> {
                    dialog.dismiss();
                    final int homeContainer = R.id.shopPage;
                    CatalogPage catalogPage = new CatalogPage();
                    backFragment(catalogPage, homeContainer);
                    Toast.makeText(getContext(), R.string.toast_get_awards, Toast.LENGTH_LONG).show();
                });
                alert.setNegativeButton(R.string.cancelBtn,
                        (dialog, which) -> {
                            dialog.dismiss();
                            return;
                        });

                alert.show();
            }
        });

        return v;

    }
    public void returnShoppingCart() {
        final int homeContainer = R.id.shopPage;
        ShoppingCart shoppingCart = new ShoppingCart();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, shoppingCart);
        ft.commit();
    }

    private void backFragment(CatalogPage catalogPage, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
        ft.addToBackStack(null);
        ft.replace(homeContainer, catalogPage);
        ft.commit();
    }
}