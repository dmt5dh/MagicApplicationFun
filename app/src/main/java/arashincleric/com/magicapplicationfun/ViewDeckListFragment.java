package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Fragment to display a specific deck list
 */
public class ViewDeckListFragment extends ListFragment {
    private final static String DECKLIST_MESSAGE_MAIN = "com.arashincleric.magicapplicationfun.DECKLISTMAIN";
    private final static String DECKLIST_MESSAGE_SIDE = "com.arashincleric.magicapplicationfun.DECKLISTSIDE";
    private final static String DECKNAME_MESSAGE = "com.arashincleric.magicapplicationfun.DECKName";
    private OnViewCardListener mCallback;
    private CustomDeckAdapter adapter;

    public interface OnViewCardListener {
        public void lookupCard(String cardName);
        public int deleteCard(String cardName, boolean inMain);

    }

    public static ViewDeckListFragment newInstance(ArrayList<String> mainList, ArrayList<String> sideList, String deckName) {
        ViewDeckListFragment fragment = new ViewDeckListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(DECKLIST_MESSAGE_MAIN, mainList);
        args.putStringArrayList(DECKLIST_MESSAGE_SIDE, sideList);
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
        try{ //Make sure parent activity implements interface
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
        Bundle args = getArguments(); //Get the deck list and add to adapter
        adapter =
                new CustomDeckAdapter(getActivity(), this);
        adapter.updateList(args.getStringArrayList(DECKLIST_MESSAGE_MAIN), true);
        adapter.updateList(args.getStringArrayList(DECKLIST_MESSAGE_SIDE), false);
        getListView().setAdapter(adapter);

    }

    /**
     * Helper for adapter to call when looking up card
     * @param cardName
     */
    public void searchCardInfo(String cardName){
        mCallback.lookupCard(cardName);
    }

    /**
     * Delete a card from this deck
     * @param cardName Name of card to remove
     * @param inMain <tt>true</tt> if deleting from main, <tt>false</tt> otherwise
     */
    public void deleteCard(final String cardName, final boolean inMain) {
        String delConfirm = getResources().getString(R.string.alert_confirm_delete);
        String delConfirmMsg = String.format(delConfirm, cardName);
        new AlertDialog.Builder(getActivity())
                .setMessage(delConfirmMsg)
                .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result = mCallback.deleteCard(cardName, inMain);
                        new AlertDialog.Builder(getActivity())
                                .setMessage((result != -1) ? R.string.alert_success_delete : R.string.alert_error_delete)
                                .setNegativeButton(R.string.alert_close, null)
                                .show();

                        if(result != -1){
                            adapter.removeCard(result,inMain);
                            getListView().invalidateViews();
                            getListView().refreshDrawableState();
                        }
                    }
                })
                .setNegativeButton(R.string.alert_cancel, null)
                .show();
    }

}
