package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.grindallday.endrone_mobile_app.model.Client;

public class ClientSpinnerAdapter extends ArrayAdapter<Client> {
    private Context context;
    private Client[] clients;

    public ClientSpinnerAdapter(@NonNull Context context, int resource, Client[] clients) {
        super(context, resource);
        this.clients = clients;
        this.context = context;
    }

    @Override
    public int getCount() {
        return clients.length;
    }

    @Nullable
    @Override
    public Client getItem(int position) {
        return clients[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(clients[position].getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(clients[position].getName());
        return label;
    }
}
