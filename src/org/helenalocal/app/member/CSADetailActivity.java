/*
 * Copyright (c) 2014. This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License for Helena Local Inc. All rights reseved.
 */

package org.helenalocal.app.member;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import org.helenalocal.Helena_Local_Hub.R;
import org.helenalocal.app.NavigationDrawerActionBarActivity;
import org.helenalocal.base.Buyer;
import org.helenalocal.base.Hub;

public class CSADetailActivity extends NavigationDrawerActionBarActivity {

    public static final String EXTRA_CSA_ID = "org.helenalocal.extra.csa_id";

    @Override
    public CharSequence getActivityTitle() {
        return getTitle();
    }

    @Override
    protected int getHierarchialParent() {
        return CSA_HOME;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String buyerId = getIntent().getStringExtra(EXTRA_CSA_ID);
        Bundle args = new Bundle();
        args.putString(EXTRA_CSA_ID, buyerId);

        Buyer buyer = Hub.buyerMap.get(buyerId);
        setTitle(buyer.getName());

        Fragment frag = new MemberFragment();
        frag.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.mainContent, frag)
                .commit();

        setupDrawer();
        _drawerToggle.setDrawerIndicatorEnabled(false);
    }
}