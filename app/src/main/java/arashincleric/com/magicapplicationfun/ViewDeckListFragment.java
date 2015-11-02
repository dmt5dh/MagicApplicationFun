package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class ViewDeckListFragment extends ListFragment {
    private final static String DECKLIST_MESSAGE = "com.arashincleric.magicapplicationfun.DECKLIST";
    private final static String DECKNAME_MESSAGE = "com.arashincleric.magicapplicationfun.DECKName";
    private OnViewCardListener mCallback;

    public interface OnViewCardListener {
        public void lookupCard(String cardName);
        public int deleteCard(String cardName);

    }

    public static ViewDeckListFragment newInstance(ArrayList<String> deckList, String deckName) {
        ViewDeckListFragment fragment = new ViewDeckListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(DECKLIST_MESSAGE, deckList);
        args.putString(DECKNAME_MESSAGE, deckName);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewDeckListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mCallback = (OnViewCardListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnViewCardListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_deck_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        final CustomDeckAdapter adapter =
                new CustomDeckAdapter(args.getStringArrayList(DECKLIST_MESSAGE), getActivity(), this);

        getListView().setAdapter(adapter);

    }

    public void searchCardInfo(String cardName){
        mCallback.lookupCard(cardName);
    }

}
