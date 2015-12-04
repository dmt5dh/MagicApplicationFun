package arashincleric.com.magicapplicationfun;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
 * Adapter created to display deck. This handles each item that has multiple actions and
 * keeps track of the main and sideboard of the deck.
 * Created by Dan on 11/2/2015.
 */
public class CustomDeckAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private Fragment fragment;
    private int mainSeparatorPos = -1;
    private int sideSeparatorPos = -1;

    public CustomDeckAdapter(Context context, Fragment fragment){
        this.context = context;
        this.fragment = fragment;

        list.add("Mainboard");
        list.add("Sideboard");

        mainSeparatorPos = 0;
        sideSeparatorPos = 1;
    }

    /**
     * Add a card to main and shift things accordingly
     * @param cardName Name of card to add
     */
    public void addToMain(String cardName){
       list.add(mainSeparatorPos + 1, cardName);
        sideSeparatorPos++;
    }

    /**
     * Remove a card from the deck list
     * @param pos Position of the card to remove
     * @param inMain <tt>true</tt> if removing from main, <tt>false</tt> otherwise.
     */
    public void removeCard(int pos, boolean inMain){
        if(inMain) {
            list.remove(pos + 1);
            sideSeparatorPos--;
        }
        else {
            list.remove(sideSeparatorPos + pos + 1);
        }

        notifyDataSetChanged();
    }

    /**
     * Add a card to the sideboard and shift things accordingly.
     * @param cardName Name of card to add.
     */
    public void addToSide(String cardName) {
        list.add(sideSeparatorPos + 1, cardName);
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
        if(view == null){ //If view not inflated, inflate it
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.deck_item, null);
        }
        final TextView cardItem = (TextView)view.findViewById(R.id.card_item);
        cardItem.setText(list.get(position));

        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button infoBtn = (Button)view.findViewById(R.id.info_btn);

        //If this is not a separator then hook listeners to button and show them on this item
        if(position != sideSeparatorPos && position != mainSeparatorPos){
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean inMain = position < sideSeparatorPos;
                    ((ViewDeckListFragment)fragment).deleteCard(list.get(position), inMain);
                }
            });

            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewDeckListFragment)fragment).searchCardInfo(list.get(position));
                }
            });
            deleteBtn.setVisibility(View.VISIBLE);
            infoBtn.setVisibility(View.VISIBLE);
            cardItem.setTextColor(Color.DKGRAY);
            cardItem.setTextSize(15);
        }
        else{
            deleteBtn.setVisibility(View.GONE);
            infoBtn.setVisibility(View.GONE);
            cardItem.setTextColor(Color.DKGRAY);
            cardItem.setTextSize(25);
            cardItem.setTypeface(null, Typeface.ITALIC);
        }



        return view;
    }

    /** TODO: make this better?
     * Updates the deck list
     * @param updatedList List of items to update
     * @param inMain <tt>true</tt> if updating main, <tt>false</tt> otherwise.
     */
    public void updateList(ArrayList<String> updatedList, boolean inMain){
        if(inMain){
            for(int i = 1; i < sideSeparatorPos; i++){
                list.remove(i);
            }
            sideSeparatorPos = 1;
            for(String s : updatedList){
                list.add(sideSeparatorPos, s);
                sideSeparatorPos++;
            }
        }
        else{
            for(int i = sideSeparatorPos + 1; i < list.size(); i++){
                list.remove(i);
            }
            for(String s : updatedList){
                list.add(s);
            }
        }
        notifyDataSetChanged();
    }
}
