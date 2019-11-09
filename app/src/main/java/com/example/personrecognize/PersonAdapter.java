package com.example.personrecognize;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person> {
    private List<Person> personList;
    private int rs;
    public PersonAdapter(@NonNull Context context, int resource, @NonNull List<Person> objects) {
        super(context, resource, objects);
        this.personList = objects;
        this.rs  = resource;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Nullable
    @Override
    public Person getItem(int position) {
        return personList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(this.getContext()).inflate(this.rs,parent,false);
        TextView nameTv =convertView.findViewById(R.id.person_name);
        TextView infoTv =convertView.findViewById(R.id.person_info);
        Person person =getItem(position);

        nameTv.setText(person.getName());
        infoTv.setText(person.getInfo());
        return convertView;
    }
}
