package com.nickamor.movieclub.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nickamor.movieclub.R;
import com.nickamor.movieclub.model.Party;

/**
 * Fragment for party details.
 */
public class PartyDetailFragment extends Fragment {
    static final int PICK_CONTACT_REQUEST = 1;
    private Party mParty;
    private ViewHolder mHolder;

    /**
     * Inflate the layout of the fragment.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_party, container, false);

        mHolder = new ViewHolder(view);

        return view;
    }

    public void setParty(Party party) {
        mParty = party;

        mHolder.bind(party);
    }

    /**
     * Get the email of the selected contact.
     *
     * @param requestCode PICK_CONTACT_REQUEST if this was our contact request.
     * @param resultCode  The result of the Contact picker intent.
     * @param data        The result of the Intent.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Email.ADDRESS};

                Cursor cursor = getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                String email = cursor.getString(column);

                //// add email to invitees
                String textEntry = mHolder.mInvitees.getText().toString();

                if (textEntry.length() > 0) {
                    textEntry = String.format("%s,\n%s", textEntry, email);
                } else {
                    textEntry = email;
                }

                mHolder.mInvitees.setText(textEntry);
            }
        }
    }

    /**
     * ViewHolder for party details.
     */
    class ViewHolder {
        public final EditText mDate;
        public final EditText mVenue;
        public final EditText mLocation;
        public final EditText mInvitees;
        public final Button mNewInvitee;
        public final Button mNewParty;

        ViewHolder(View v) {
            mDate = (EditText) v.findViewById(R.id.party_date);
            mVenue = (EditText) v.findViewById(R.id.party_venue);
            mLocation = (EditText) v.findViewById(R.id.party_location);
            mInvitees = (EditText) v.findViewById(R.id.party_invitees);
            mNewInvitee = (Button) v.findViewById(R.id.party_new_invite);
            mNewParty = (Button) v.findViewById(R.id.party_new);
        }

        public void bind(Party party) {

        }
    }
}
