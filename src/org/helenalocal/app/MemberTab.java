/*
 * Copyright (c) 2014. This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License for Helena Local Inc. All rights reseved.
 */

package org.helenalocal.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import org.helenalocal.Helena_Local_Hub.R;
import org.helenalocal.base.Hub;
import org.helenalocal.base.HubInit;
import org.helenalocal.base.Item;
import org.helenalocal.base.Order;
import org.helenalocal.base.get.OrderHub;
import org.helenalocal.utils.ImageCache;

import java.util.*;

public class MemberTab extends TabBase {

    private static final String LogTag = "MemberTab";

    private List<Object> _itemList;
    private MemberItemAdapter _arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeReceiver(HubInit.HubType.ITEM_HUB);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.member_tab, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageCache cache = ((HubApplication)getActivity().getApplication()).getImageCache();

        _itemList = new ArrayList<Object>();
        _arrayAdapter = new MemberItemAdapter(getActivity(), cache, _itemList);

        ListView listView = (ListView)getActivity().findViewById(R.id.memberListView);
        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));
        listView.setAdapter(_arrayAdapter);
    }

    @Override
    protected void onRefresh() {

        // display all Helena Local bought for CSA!
        TreeMap<String, List<Item>> productMap = new TreeMap<String, List<Item>>();
        for (Order order : OrderHub.getOrdersForBuyer(HubInit.HELENA_LOCAL_BUYER_ID)) {
            Item item = Hub.itemMap.get(order.getItemID());

            List<Item> itemList = productMap.get(item.getCategory());
            if (itemList == null) {
                itemList = new ArrayList<Item>();
                productMap.put(item.getCategory(), itemList);
            }

            itemList.add(item);

        }

        // now that we have the data sorted by and organized by category, flatten it out into a list.
        // NOTE: This list contains both String (category) and Item (product)
        _itemList.clear();
        for (Map.Entry<String, List<Item>> entry : productMap.entrySet()) {

            // add the category
            _itemList.add(entry.getKey());

            // sort the products
            List<Item> productList = entry.getValue();
            Collections.sort(productList, new Comparator<Item>() {
                public int compare(Item i1, Item i2) {
                    return i1.getProductDesc().compareTo(i2.getProductDesc());
                }
            });


            // add the products
            for (Item item : productList) {
                Log.w(LogTag, item.getProductDesc());
                _itemList.add(item);
            }
        }

        _arrayAdapter.notifyDataSetChanged();
    }
}