package arashincleric.com.magicapplicationfun;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import arashincleric.com.magicapplicationfun.R;

/**
 * Created by Dan on 11/2/2015.
 */
public class CustomDeckAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private Fragment fragment;

    public CustomDeckAdapter(ArrayList<String> list, Context context, Fragment fragment){
        this.list = list;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int pos){
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.deck_item, null);
        }
        final TextView cardItem = (TextView)view.findViewById(R.id.card_item);
        cardItem.setText(list.get(position));

        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewDeckListFragment)fragment).deleteCard(list.get(position));
            }
        });

        Button infoBtn = (Button)view.findViewById(R.id.info_btn);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewDeckListFragment)fragment).searchCardInfo(list.get(position));
            }
        });


        return view;
    }

    public void updateList(ArrayList<String> updatedList){
        this.list = updatedList;
        notifyDataSetChanged();
    }
}
