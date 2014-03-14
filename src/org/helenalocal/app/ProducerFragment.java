/*
 * Copyright (c) 2014. This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License for Helena Local Inc. All rights reseved.
 */

package org.helenalocal.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import org.helenalocal.Helena_Local_Hub.R;
import org.helenalocal.base.Hub;
import org.helenalocal.base.HubInit;
import org.helenalocal.base.Producer;
import org.helenalocal.base.get.ProducerHub;
import org.helenalocal.utils.ImageCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProducerFragment extends FragmentBase {

    private static String Tag = "ProducerFragment";
    private List<Producer> _producerList;
    private ProducerListAdapter _arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeReceiver(HubInit.HubType.PRODUCER_HUB);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.producer_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageCache cache = ((HubApplication)getActivity().getApplication()).getImageCache();
        _producerList = new ArrayList<Producer>();
        _arrayAdapter = new ProducerListAdapter(getActivity(), _producerList, cache);

        ListView listView = (ListView) getActivity().findViewById(R.id.producerListView);
        listView.setAdapter(_arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Producer producer = (Producer) ((ListView)parent).getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), ProducerDetailActivity.class);
                intent.putExtra(ProducerDetailActivity.EXTRA_PRODUCER_ID, producer.getPID());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        super.onRefresh();

        _producerList.clear();

        // add producers who are at serviceLevel = 2 (local) or (serviceLevel > 0 and have at least one order)!
        ArrayList<Producer> localList = new ArrayList<Producer>();
        ArrayList<Producer> regionalList = new ArrayList<Producer>();
        ArrayList<Producer> producerArrayList = new ArrayList<Producer>(Hub.producerMap.values());
        for (int j = 0; j < producerArrayList.size(); j++) {
            Producer producer = producerArrayList.get(j);
            // now set the producer order count
            producer.setOrderCnt(ProducerHub.getOrderItemCnt(producer.getPID()));

            int serviceLevel = Integer.valueOf(producer.getServiceLevel().trim());
            // Service Level (0-off, 1-regional, 2-local)
            if ((serviceLevel == 2) || (producer.getOrderCnt() > 0)) {
                switch (serviceLevel) {
                    case 0:
                        break;
                    case 2:
                        localList.add(producer);
                        break;
                    default:
                        regionalList.add(producer);
                        break;
                }
            }
            Log.w(Tag, "producer.getName() = " + producer.getName() + "; serviceLevel = " + serviceLevel + "; orderCnt = " + producer.getOrderCnt());
        }
        // sort
        Collections.sort(localList, new Comparator<Producer>() {
            public int compare(Producer o1, Producer o2) {
                return o2.getOrderCnt().compareTo(o1.getOrderCnt());
            }
        });

        Collections.sort(regionalList, new Comparator<Producer>() {
            public int compare(Producer o1, Producer o2) {
                return o2.getOrderCnt().compareTo(o1.getOrderCnt());
            }
        });

        // add them in order
        _producerList.addAll(localList);
        _producerList.addAll(regionalList);
        _arrayAdapter.notifyDataSetChanged();
    }
}